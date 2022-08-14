package org.diploma.sulima.services.search;

import lombok.Data;

import java.util.List;

@Data
public class FoundOutput {

    private boolean result;
    private long count;
    private List<FoundPage> data;

    public FoundOutput() {

    }

    public FoundOutput(boolean result, long count, List<FoundPage> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }
}
