package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.MongoFragment;
import eu.dbortoluzzi.consumer.model.StatisticsCounter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

public interface FragmentRepositoryCustom {
    public void insertIfNotExists(MongoFragment fragment);
    public List<MongoFragment> findNotSyncedFragments(Date syncedDate, Integer limit);
    public List<StatisticsCounter> countFragmentFiltered(Date from, Date to, Long elapsedInSeconds, Criteria criteria);
}
