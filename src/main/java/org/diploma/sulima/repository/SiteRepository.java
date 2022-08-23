package org.diploma.sulima.repository;

import org.diploma.sulima.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE site AUTO_INCREMENT = 1"
            , nativeQuery = true)
    int resetAutoIncrement();

    @Query(value = "SELECT s.id FROM site s WHERE s.url = ?1", nativeQuery = true)
    int findSiteIdByUrl(String url);

    @Query(value = "SELECT s.url FROM site s WHERE status != 'FAILED'", nativeQuery = true)
    HashSet<String> getAllUrl();

    @Query(value = "SELECT s.id FROM site s WHERE status != 'FAILED'", nativeQuery = true)
    HashSet<Integer> getAllId();

    @Modifying
    @Transactional
    @Query(value = "UPDATE site SET status = ?2 WHERE url = ?1", nativeQuery = true)
    int updateSiteStatus(String url, String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE site SET last_error = ?2 WHERE url = ?1", nativeQuery = true)
    int updateSiteLastError(String url, String lastError);

    @Modifying
    @Transactional
    @Query(value = "UPDATE site SET count_lemma = ?1 WHERE id = ?2", nativeQuery = true)
    int updateSiteCountLemma(int count, int id);

    @Query(value = "SELECT (SELECT COUNT(*) " +
                            "FROM site " +
                            "WHERE COUNT(*) = (SELECT COUNT(*) " +
                                                "FROM site sp " +
                                                "WHERE sp.status = 'INDEXED')) " +
                    "FROM site"
            , nativeQuery = true)
    int allIndexed();

    @Query(value = "SELECT COUNT(*) FROM site WHERE url = ?1 AND status = 'INDEXED'"
            , nativeQuery = true)
    int currentSiteIndexed(String site);

    @Query(value = "SELECT COUNT(*) FROM site s WHERE s.status = 'INDEXING'"
            , nativeQuery = true)
    int thereIsIndexing();

    Optional<Site> findFirstByUrl(String url);

    @Query(value = "SELECT count_lemma FROM site WHERE id = ?1", nativeQuery = true)
    int getCountLemmaById(int id);
}
