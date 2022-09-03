package org.diploma.sulima.repository;

import org.diploma.sulima.model.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS lemma " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "site_id INT NOT NULL, " +
            "lemma VARCHAR(255) NOT NULL, " +
            "frequency INT NOT NULL, " +
            "PRIMARY KEY (id)" +
            ")"
            , nativeQuery = true)
    void createTableLemma();

    @Query(value = "SELECT * FROM lemma WHERE lemma = ?1 AND site_id = ?2"
            , nativeQuery = true)
    Lemma findLemmaAndSiteId(String lemma, int siteID);

    List<Lemma> findByLemma(String lemma);
}
