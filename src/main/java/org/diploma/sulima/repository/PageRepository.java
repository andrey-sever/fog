package org.diploma.sulima.repository;

import org.diploma.sulima.data.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS page " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "site_id INT NOT NULL, " +
            "path TEXT NOT NULL, " +
            "code INT NOT NULL, " +
            "content MEDIUMTEXT NOT NULL, " +
            "FULLTEXT KEY path (path), " +
            "PRIMARY KEY (id)" +
            ")",
            nativeQuery = true)
    void createTablePage();

    @Modifying
    @Transactional
    @Query(value = "DROP TABLE IF EXISTS page, `index`, lemma, lemma_raw", nativeQuery = true)
    int dropWorkerTables();

    @Query(value = "SELECT " +
                        "COUNT(*) " +
                    "FROM page p " +
                    "GROUP BY p.site_id " +
                    "HAVING p.site_id = ?1"
            , nativeQuery = true)
    int getStatisticsPages(int siteId);

    @Query(value = "SELECT EXISTS(SELECT * FROM page p WHERE p.site_id = ?1 AND p.path = ?2)"
            , nativeQuery = true)
    int existsBySiteIdAndPath(int siteId, String path);

    @Query(value = "SELECT * FROM page p WHERE p.id = ?1"
            , nativeQuery = true)
    Page getById(int id);

    Optional<Page> findFirstBySiteIdAndPath(int siteId, String path);

    long countBySiteId(int siteId);

}
