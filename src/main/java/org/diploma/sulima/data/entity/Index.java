package org.diploma.sulima.data.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "`index`")
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="page_id")
    private int pageId;

    @Column(name="lemma_id")
    private int lemmaId;

    private float rank;

    Index() {

    }

    public Index(int id, int pageId, int lemmaId, float rank) {
        this.id = id;
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rank = rank;
    }
}
