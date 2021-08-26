package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.commons.utils.CommonUtils;
import eu.dbortoluzzi.consumer.model.MongoFragment;
import eu.dbortoluzzi.consumer.model.StatisticsCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        query.addCriteria(Criteria.where("synced").is(false));

        return mongoTemplate.find(query, MongoFragment.class);
    }

    @Override
    public List<StatisticsCounter> countFragmentBy(Date startSearch, Long elapsedInSeconds, Criteria criteria) {
        List<StatisticsCounter> statisticsCountersRequest = new ArrayList<>();

        Calendar calendarStartSearch = Calendar.getInstance();
        calendarStartSearch.setTime(startSearch);

        Calendar startAuxSearch = Calendar.getInstance();
        startAuxSearch.setTime(startSearch);

        Calendar now = Calendar.getInstance();
        while (startAuxSearch.before(now)) {
            Calendar endDateAux = Calendar.getInstance();
            endDateAux.setTime(new Date(startAuxSearch.getTimeInMillis() + (elapsedInSeconds * 1000)));
            statisticsCountersRequest.add(
                    StatisticsCounter
                            .builder()
                            .startDate(startAuxSearch.getTime())
                            .endDate(endDateAux.getTime())
                            .build()
            );
            startAuxSearch = (Calendar) endDateAux.clone();
        }
        return CommonUtils.allOfCompletableFutures(
                statisticsCountersRequest
                .stream()
                .map(s -> execAsyncStatisticsQuery(s, criteria))
                .collect(Collectors.toList())
        ).join()
                .stream()
                .sorted(Comparator.comparing(StatisticsCounter::getStartDate))
                .collect(Collectors.toList());
    }

    private CompletableFuture<StatisticsCounter> execAsyncStatisticsQuery(StatisticsCounter statisticsCounter, Criteria criteria) {
        return CompletableFuture.supplyAsync(() -> execStatisticsQuery(statisticsCounter, criteria));
    }

    private StatisticsCounter execStatisticsQuery(StatisticsCounter statisticsCounter, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.addCriteria(Criteria.where("creationDate").gt(statisticsCounter.getStartDate()).lt(statisticsCounter.getEndDate()));
        long count = mongoTemplate.count(query, MongoFragment.class);
        return new StatisticsCounter(statisticsCounter.getStartDate(), statisticsCounter.getEndDate(), count);
    }
}
