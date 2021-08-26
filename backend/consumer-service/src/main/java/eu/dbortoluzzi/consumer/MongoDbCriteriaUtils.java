package eu.dbortoluzzi.consumer;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class MongoDbCriteriaUtils {
    public static Criteria syncedFilter(Boolean synced) {
        return Criteria.where("synced").is(true);
    }

    public static Criteria producersFilter(List<String> producers) {
        if (producers == null) {
            return new Criteria();
        }
        return Criteria.where("payload.metadata.instance").in(producers);
    }
}
