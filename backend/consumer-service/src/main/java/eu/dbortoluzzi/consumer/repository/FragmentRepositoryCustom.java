package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.MongoFragment;

import java.util.Date;
import java.util.List;

public interface FragmentRepositoryCustom {
    public void insertIfNotExists(MongoFragment fragment);
    public List<MongoFragment> getNotSynced(Date syncedDate, Integer limit);
}
