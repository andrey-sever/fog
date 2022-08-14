package org.diploma.sulima.repository;

import org.diploma.sulima.data.entity.LemmaRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LemmaRawRepository extends JpaRepository<LemmaRaw, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS lemma_raw " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "site_id INT NOT NULL, " +
            "path TEXT NOT NULL, " +
            "lemma VARCHAR(255) NOT NULL, " +
            "ratio_rank FLOAT NOT NULL, " +
            "PRIMARY KEY (id)" +
            ")"
            , nativeQuery = true)
    void createTableLemmaRaw();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO lemma (site_id, lemma, frequency)" +
            "SELECT " +
                "lg.site_id AS site_id, " +
                "lg.lemma AS lemma, " +
                "COUNT(*) AS frequency " +
            "FROM (SELECT DISTINCT l.site_id, l.path, l.lemma " +
                    "FROM lemma_raw l) AS lg " +
            "GROUP BY " +
                "lg.site_id, " +
                "lg.lemma"
            , nativeQuery = true)
    void fillInTableLemma();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO `index` (page_id, lemma_id, `rank`)" +
            "SELECT " +
                "p.id AS page_id, " +
                "l.id AS lemma_id, " +
                "s.ratio_rank AS `rank` " +
            "FROM (SELECT " +
                        "lr.path, " +
                        "lr.lemma, " +
                        "SUM(lr.ratio_rank) AS ratio_rank " +
                    "FROM lemma_raw lr " +
                    "GROUP BY " +
                        "lr.site_id, " +
                        "lr.path, " +
                        "lr.lemma) AS s " +
            "JOIN page p ON s.path = p.path " +
            "JOIN lemma l ON s.lemma = l.lemma"
            , nativeQuery = true)
    void fillInTableIndex();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO `index` (page_id, lemma_id, `rank`)" +
            "SELECT " +
                "p.id AS page_id, " +
                "l.id AS lemma_id, " +
                "s.ratio_rank AS `rank` " +
            "FROM (SELECT " +
                        "lr.path, " +
                        "lr.lemma, " +
                        "SUM(lr.ratio_rank) AS ratio_rank " +
                    "FROM lemma_raw lr " +
                    "WHERE lr.path = ?1 " +
                    "GROUP BY " +
                        "lr.site_id, " +
                        "lr.path, " +
                        "lr.lemma) AS s " +
            "JOIN page p ON s.path = p.path " +
            "JOIN lemma l ON s.lemma = l.lemma"
            , nativeQuery = true)
    void fillInTableIndexByPath(String path);

    int countBySiteId(int siteId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lemma_raw l WHERE l.site_id = ?1 AND l.path = ?2", nativeQuery = true)
    int deleteBySiteIdAndPath(int SiteId, String path);
}
