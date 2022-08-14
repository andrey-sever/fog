package org.diploma.sulima.repository;

import org.diploma.sulima.data.entity.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS `index` " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "page_id INT NOT NULL, " +
            "lemma_id INT NOT NULL, " +
            "`rank` FLOAT NOT NULL, " +
            "PRIMARY KEY (id)" +
            ")"
            , nativeQuery = true)
    void createTableIndex();

    @Query(value = "SELECT " +
                        "i.page_id " +
                    "FROM `index` i " +
                    "JOIN page AS p ON p.id = i.page_id " +
                    "WHERE i.lemma_id = ?1 AND p.site_id = ?2"
            , nativeQuery = true)
    List<Integer> findByLemmaIdAndSiteIdOutPageId(int lemmaId, int siteId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO found_page (site, site_name, uri, title, snippet, relevance, page_id)" +
            "SELECT * FROM " +
            "(SELECT " +
                "s.url, " +
                "s.name, " +
                "p.path, " +
                "'' AS title, " +
                "'' AS snippet, " +
                "(SUM(i.`rank`) / (SELECT " +
                                        "SUM(r.`rank`) " +
                                    "FROM `index` r " +
                                    "WHERE r.page_id IN (?1) AND r.lemma_id IN (?2) " +
                                    "GROUP BY r.page_id " +
                                    "ORDER BY SUM(r.`rank`) DESC " +
                                    "LIMIT 1)) AS relevance, " +
                "i.page_id " +
            "FROM " +
                "`index` i " +
            "JOIN page AS p ON p.id = i.page_id " +
            "JOIN site AS s ON s.id = p.site_id " +
            "WHERE " +
                "i.page_id IN (?1) " +
                "AND i.lemma_id IN (?2) " +
            "GROUP BY i.page_id) AS f " +
            "ORDER BY relevance DESC"
            , nativeQuery = true)
    void fillInTableFoundPage(List<Integer> listPageId, List<Integer> listLemmaId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `index` i WHERE i.page_id = ?1", nativeQuery = true)
    int deleteAllById(int id);

    @Modifying
    @Transactional
    @Query(value = "CREATE INDEX page_id ON `index` (page_id)", nativeQuery = true)
    void createIndexPageId();

    @Modifying
    @Transactional
    @Query(value = "CREATE INDEX lemma_id ON `index` (lemma_id)", nativeQuery = true)
    void createIndexLemmaId();
}
