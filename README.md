# User manual

## Installation requirements
1. *nodejs* (version >= 12)
2. *java* (version >= 11)
2. *npm* (version >= 5)
4. *docker* (version up to date)
5. *docker-compose* (version up to date)

## Build Steps
1. Clone repository
2. Build backend services
3. Build and run docker compose

## Run
1. Build backend and docker images: `./build.sh`
2. Run docker images: `docker-compose up --scale consumer-service=2 --scale producer-service=2`

## Clear MongoDB data
1. Connect with SSH to DB container: `docker exec -it ntw-com-proto_db_1 /bin/bash`
2. Clear all databases: `mongo --quiet --eval 'db.getMongo().getDBNames().forEach(function(i){db.getSiblingDB(i).dropDatabase()})'`