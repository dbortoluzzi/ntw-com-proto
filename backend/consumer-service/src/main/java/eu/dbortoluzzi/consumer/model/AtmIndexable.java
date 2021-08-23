package eu.dbortoluzzi.consumer.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;

@Document(collection = "atm")
public class AtmIndexable extends Atm{
    @Indexed(unique=true)
    private String searchTerm;

    public AtmIndexable(Integer distance, String type, Address address) {
        super(distance, type, address);
        this.searchTerm = generateSearchTerms();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String generateSearchTerms(){
        StringJoiner joiner = new StringJoiner(" "); // Use 'space' as the delimiter
        joiner.add(distance.toString())
                .add(type);
        if (address != null) {
            joiner.add(address.toString());
        }

        return joiner.toString();

    }
}

