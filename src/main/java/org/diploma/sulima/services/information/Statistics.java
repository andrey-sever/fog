package org.diploma.sulima.services.information;

import lombok.Data;

import java.util.List;

@Data
public class Statistics {

    private Total total;
    private List<Detailed> detailed;

    public Statistics(Total total, List<Detailed> detailed) {
        this.total = total;
        this.detailed = detailed;
    }
}
