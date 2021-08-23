package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.AtmIndexable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AtmsRepository extends MongoRepository<AtmIndexable, String> {

}
