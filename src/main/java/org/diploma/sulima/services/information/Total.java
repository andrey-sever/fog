package org.diploma.sulima.services.information;

import lombok.Data;

@Data
public class Total {

    private int sites;
    private int pages;
    private int lemmas;
    private boolean isIndexing;

    public Total() {
        this.sites = 0;
        this.pages = 0;
        this.lemmas = 0;
        this.isIndexing = false;
    }

    public Total(int sites, int pages, int lemmas, boolean isIndexing) {
        this.sites = sites;
        this.pages = pages;
        this.lemmas = lemmas;
        this.isIndexing = isIndexing;
    }
}
