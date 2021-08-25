package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.MongoFragment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class FragmentRepositoryImpl implements FragmentRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insertIfNotExists(MongoFragment fragment) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uniqueFileName").is(fragment.getUniqueFileName()));
        query.addCriteria(Criteria.where("index").is(fragment.getIndex()));
        query.addCriteria(Criteria.where("payload.metadata.instance").is(fragment.getPayload().getMetadata().getInstance()));

        List<MongoFragment> mongoFragments = mongoTemplate.find(query, MongoFragment.class);
        if (mongoFragments.size() == 0) {
            mongoTemplate.insert(fragment);
        } else {
            log.info("fragment already present: uniqueFileName = {}, index = {}", fragment.getUniqueFileName(), fragment.getIndex());
        }
    }

    @Override
    public List<MongoFragment> getNotSynced(Date syncedDate, Integer limit) {
        Query query = new Query().with(PageRequest.of(0, limit));
        query.addCriteria(Criteria.where("synced").is(null));

        return mongoTemplate.find(query, MongoFragment.class);
    }
}
