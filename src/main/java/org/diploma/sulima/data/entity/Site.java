package org.diploma.sulima.data.entity;

import lombok.Data;
import org.diploma.sulima.data.SiteStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private SiteStatus status;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "last_error")
    private String lastError;

    private String url;
    private String name;

    @Column(name = "count_lemma")
    private int countLemma;

    public Site() {
        this.status = SiteStatus.INDEXING;
        this.statusTime = new Date();
        this.lastError = null;
        this.countLemma = 0;
    }
}
