package org.diploma.sulima.services.search;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "found_page")
public class FoundPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String site;

    @Column(name="site_name")
    private String siteName;

    private String uri;
    private String title;
    private String snippet;
    private float relevance;
    @Column(name="page_id")
    private int pageId;

    public FoundPage() {

    }

    public FoundPage(String site, String siteName, String uri, String title, String snippet, float relevance) {
        this.id = id;
        this.site = site;
        this.siteName = siteName;
        this.uri = uri;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }

}
