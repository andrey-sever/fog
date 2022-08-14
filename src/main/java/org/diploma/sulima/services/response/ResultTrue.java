package org.diploma.sulima.services.response;

import lombok.Data;

@Data
public class ResultTrue {

    private boolean result;

    public ResultTrue() {
        this.result = true;
    }
}
