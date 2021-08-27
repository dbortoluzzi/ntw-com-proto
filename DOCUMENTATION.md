# Architettura di sistema

1. **Spring boot**: sviluppati due microservizi:
   - *producer-service*: (aka "Servizio A"). Invia i frammenti di un file di testo acquisito in una cartella predefinita.
   - *consumer-service*: (aka "Servizio B"). Ricevi i frammenti da un file di testo e avvia un processo di sincronizzazione con le altre instanze *consumer*.
2. **Docker**: isola i singoli componenti del sistema in container diversi e permette la loro replicazione
3. **Mongodb**: database utilizzato dal *producer-service*. Ogni istanza del producer utilizza uno schema apposito.
4. **Nginx**: API gateway.

# Funzionamento
## Producer (servizio A)
### Generale
- Ciascuna istanza del producer: 
  - conosce l'elenco dei possibili consumer da contattare;
  - conosce il nome della propria istanza.

### Lettura file
- Attraverso la libreria *JAVA NIO*, viene creata la cartella (se non esiste) di deposito;
- Il producer si sottoscrive all'evento di creazione di un file se non già presente;
- Essendo il file potenzialmente di grosse dimensioni, attende che esso venga completamente copiato nel file system;
- Il file viene letto in un buffer da X (configurabile) bytes; queste letture vengono raggruppate in un numero Y (configurabile) di "passate";  
- Per ciascuna passata, vengono creati i *Fragment* da inviare al producer;
- I frammenti creati da ciascuna passata vengono inviati in parallelo al producer grazie alle CompletableFuture.- Quando l'invio dei frammenti di una passata si completa con successo, allora il producer passa alla successiva passata.
- Qualora il file venga inviato completamente (ad es. senza un crash del producer), il file viene cancellato dal file system. 
> TODO: 
> - recupero dei file non completamente acquisiti allo start up del servizi

### Invio frammento
- Il frammento inviato è così strutturato:
    ```{
      "payload": {
      "metadata": {
          "instance": "ntw-com-proto_producer-service_1",
          "checksum": "946E0A158D4CB88CCABE3D7A6859A124"
          },
        "text": "01130119011F0125012B01320138013E0145014C015201590160..."
      },
      "timestamp": "2021-08-27T10:10:51.550Z",
      "filename": "WordNet_1X.txt",
      "total": "3096",
      "index": "7"
      }
  ```
    - *instance*: rappresenta il nome dell'instanza del producer
    - *checksum*: rappresenta il checksum MD5 di *text* (utilizzato per garantire la conformità del testo inviato)
    - *text*: hex dump del frammento del testo
    - *timestamp*: timestamp di inizio lettura del file
    - *filename*: nome file di partenza
    - *total*: numero di frammenti totali per file
    - *index*: indice del frammento corrente    
- Viene generato l'hex dump del JSON del frammento e il suo corrispettivo checksum MD5, per inviarli successivamente al consumer
- Viene tentato l'invio del frammento a rotazione verso tutti i consumers
- Qualora l'invio fallisca (codice di ritorno != *OK*), si passa al successivo.

## Consumer (servizio B)
### Generale
- Ciascuna istanza del consumer:
    - conosce l'elenco di tutti i consumer possibili;
    - conosce il nome della propria istanza.

### Ricezione frammento
- Il frammento viene ricevuto da un servizio in POST
- Viene confrontato il checksum con l'MD5 del POST PAYLOAD ricevuto, utilizzato per validare formalmente il contenuto
- Viene decodificato l'hex dump del POST PAYLOAD, in modo da deserializzare la stringa e riottenere il JSON originale
- Viene arricchito il json del frammento con altre informazioni:
    ```{
       "creationDate":"2021-08-27T10:10:51.701Z",
       "uniqueFileName":"WordNet_1X.pdf-1630059051550",
       "synced":true,
       "instancesSynced":[
          "ntw-com-proto_consumer-service_2"
       ]
    }
  ```
    - *creationDate*: data di generazione del nuovo frammento nel consumer
    - *uniqueFileName*: generazione dell'indentificativo univoco del file (fileName+timestamp inviato dal producer), in modo da gestire anche invii multipli di più file con lo stesso nome.
    - *synced*: flag per segnalare che il frammento è sincronizzato con tutte le istanze conosciute dal consumer e che non ha bisogno di ulteriori sincronizzazioni.
    - *instancesSynced*: elenco di istanze con il quale il frammento è stato sincronizzato con successo.
- Il frammento viene inserito nel database mongodb, ma solo se non è già presente un frammento con uguale [*uniqueFileName*, *index*, *instance*] (è stato creato un indice composto ad hoc).
- Se il frammento era già presente oppure è stato inserito correttamente, allora viene restituto al consumer il codice *OK*.

### Sincronizzazione
- Allo startup del sistema, viene avviato un *TimerTask* con un delay configurabile.
- Il TimerTask avvia un processo di ricerca dei frammenti persistiti su mongodb, con un limite massimo per ciascun run di X elementi (configurabile), ancora da sincronizzare.
- Per ciascun frammento, viene creato un CompletableFuture che prevede l'invio di chiamate REST verso un servizio di sincronizzazione verso ciascuna delle istanze di consumer ancora da sincronizzare.
- Al termine delle chiamate di sync di ciascun frammento, viene aggiornato il record su mongodb, inserendo:
  - *instancesSynced*: nome delle istanze correttamente sincronizzate 
  - *synced*: synced = true se tutte le chiamate da effettuare sono andate a buon fine (esito "*OK*")
- Al termine del processo di sync di tutti i frammenti, viene memorizzata sul database l'ultima data di syncronizzazione effettuata
- Se il processo non terminasse nel tempo prestabilito, esiste un lock su database per impedire l'esecuzione duplicata del processo.

### Ricezione di un frammento da sincronizzare richiesto da un altro consumer
- Il frammento viene ricevuto da un servizio in POST, con anche l'informazione del nome dell'istanza consumer che richiede la sincronizzazione.
- Viene invocato lo stesso flow di ricezione del frammento da un producer, con solo queste differenze:
  - viene salvato su mongodb nel frammento il nome dell'istanza consumer da cui proviene la richiesta di sync nel campo *syncedFromConsumer*  
  - viene salvato su mongodb nel frammento il flag *synced=true*, in modo da non risottoporre il secondo consumer alla sincronizzazione (già in corso nel consumer richiedente)
  
### Performance
- Queste, ad esempio, le statistiche di "inserimento frammenti" nella macchina di sviluppo, senza tuning (intervallo orario di un minuto):
```
 {
    "startDate": "20210827162100",
    "endDate": "20210827162200",
    "count": 53299
  },
  {
    "startDate": "20210827162200",
    "endDate": "20210827162300",
    "count": 59472
  },
  {
    "startDate": "20210827162300",
    "endDate": "20210827162400",
    "count": 55342
  }
```
- La configurazione attuale della sincronizzazione è di 1000 elementi da elaborare ogni 2secondi.