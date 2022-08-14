package org.diploma.sulima.services.information;

import lombok.Data;

@Data
public class ReadyStatistics {

    private boolean result;
    private Statistics statistics;

    public ReadyStatistics(boolean result, Statistics statistics) {
        this.result = result;
        this.statistics = statistics;
    }
}
