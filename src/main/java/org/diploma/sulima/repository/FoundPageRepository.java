package org.diploma.sulima.repository;

import org.diploma.sulima.model.FoundPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FoundPageRepository extends JpaRepository<FoundPage, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CREATE TEMPORARY TABLE IF NOT EXISTS found_page " +
            "(" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "site TEXT NOT NULL, " +
                "site_name TEXT NOT NULL, " +
                "uri TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "snippet TEXT NOT NULL, " +
                "relevance FLOAT NOT NULL, " +
                "page_id INT NOT NULL, " +
                "PRIMARY KEY (id)" +
            ");",
            nativeQuery = true)
    void createTableFoundPageIfNotExist();

    @Query(value = "SELECT * FROM found_page LIMIT ?1 OFFSET ?2"
            , nativeQuery = true)
    List<FoundPage> findPortionFoundPages(int limit, int offset);
}
