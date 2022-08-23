package org.diploma.sulima.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Lemma implements Comparable<Lemma> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="site_id")
    private int siteId;

    private String lemma;
    private int frequency;

    public Lemma() {

    }

    public Lemma(int siteId, String lemma, int frequency) {
        this.id = id;
        this.siteId = siteId;
        this.lemma = lemma;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Lemma l) {
        return (this.frequency - l.frequency);
    }
}
