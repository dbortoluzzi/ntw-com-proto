package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.AtmIndexable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AtmsRepositoryImpl implements AtmsRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<AtmIndexable> search(String querySearch, Pageable pageable) {
        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("searchTerm").regex(querySearch,"i"));

        List<AtmIndexable> list = mongoTemplate.find(query, AtmIndexable.class);
        long count = mongoTemplate.count(query, AtmIndexable.class);
        Page<AtmIndexable> resultPage = new PageImpl<AtmIndexable>(list , pageable, count);
        return resultPage;
    }
}
