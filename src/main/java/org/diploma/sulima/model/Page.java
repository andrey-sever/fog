package org.diploma.sulima.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="site_id")
    private int siteId;

    private String path;
    private int code;
    private String content;

    Page() {

    }

    public Page(int siteId, String path, int code, String content) {
        this.id = id;
        this.siteId = siteId;
        this.path = path;
        this.code = code;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return siteId == page.siteId && Objects.equals(path, page.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId, path);
    }
}
