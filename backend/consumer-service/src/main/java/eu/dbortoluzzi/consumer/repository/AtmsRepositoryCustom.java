package eu.dbortoluzzi.consumer.repository;

import eu.dbortoluzzi.consumer.model.AtmIndexable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AtmsRepositoryCustom {
    public Page<AtmIndexable> search(String querySearch, Pageable pageable);
}
