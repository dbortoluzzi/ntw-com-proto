package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.MongoFragment;

public interface FragmentRepositoryCustom {
    public void insertIfNotExists(MongoFragment fragment);
}
