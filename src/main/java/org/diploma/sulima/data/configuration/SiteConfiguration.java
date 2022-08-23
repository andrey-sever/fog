package org.diploma.sulima.data.configuration;

import lombok.Data;
import org.diploma.sulima.model.Site;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "connection")
public class SiteConfiguration {
    private List<Site> sites;
}
