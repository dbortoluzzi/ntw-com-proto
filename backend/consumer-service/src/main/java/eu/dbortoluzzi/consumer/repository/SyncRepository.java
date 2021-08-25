package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.Sync;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SyncRepository extends MongoRepository<Sync, String>, SyncRepositoryCustom {

}
