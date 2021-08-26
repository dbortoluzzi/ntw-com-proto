package eu.dbortoluzzi.consumer;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MongoDbCriteriaUtils {
    public static Criteria syncedFilter(Boolean synced) {
        return Criteria.where("synced").is(true);
    }

    public static Criteria producersInFilter(List<String> producers) {
        if (producers == null) {
            return new Criteria();
        }
        return Criteria.where("payload.metadata.instance").in(producers);
    }

    public static Criteria consumersInOrNotFilter(List<String> consumers) {
        if (CollectionUtils.isEmpty(consumers)) {
            return Criteria.where("syncedFromConsumer").exists(false);
        } else {
            return Criteria.where("syncedFromConsumer").in(consumers);
        }
    }
}
