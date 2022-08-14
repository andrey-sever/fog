package org.diploma.sulima.data;

import org.diploma.sulima.data.entity.Page;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PagePackageRecords {

    private Set<Page> records;

    public PagePackageRecords() {
        newRecords();
    }

    public void addPage(Page page) {
        records.add(page);
    }

    public Set<Page> getRecords() {
        return records;
    }

    public int getCount() {
        return records.size();
    }

    public void newRecords() {
        records = ConcurrentHashMap.newKeySet();
    }
}
