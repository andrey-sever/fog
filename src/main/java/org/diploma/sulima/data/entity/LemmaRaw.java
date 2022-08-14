package org.diploma.sulima.data.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "lemma_raw")
public class LemmaRaw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="site_id")
    private int siteId;

    private String path;
    private String lemma;
    @Column(name="ratio_rank")
    private float ratioRank;

    LemmaRaw() {

    }

    public LemmaRaw(int siteId, String path, String lemma, float ratioRank) {
        this.id = id;
        this.siteId = siteId;
        this.path = path;
        this.lemma = lemma;
        this.ratioRank = ratioRank;
    }
}
