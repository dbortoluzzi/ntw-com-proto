package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.MongoFragment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FragmentRepository extends MongoRepository<MongoFragment, String> {

}
