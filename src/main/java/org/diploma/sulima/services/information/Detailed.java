package org.diploma.sulima.services.information;

import lombok.Data;

@Data
public class Detailed {

    private String url;
    private String name;
    private String status;
    private long statusTime;
    private String error;
    private int pages;
    private int lemmas;

    public Detailed(String url, String name, String status, long statusTime, String error,
                    int pages, int lemmas) {
        this.url = url;
        this.name = name;
        this.status = status;
        this.statusTime = statusTime;
        this.error = error;
        this.pages = pages;
        this.lemmas = lemmas;
    }
}
