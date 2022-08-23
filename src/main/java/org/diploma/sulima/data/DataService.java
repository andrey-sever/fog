package org.diploma.sulima.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.diploma.sulima.data.configuration.SiteConfiguration;
import org.diploma.sulima.repository.*;
import org.diploma.sulima.services.response.ErrorList;
import org.diploma.sulima.services.IndexingBuilder;
import org.diploma.sulima.services.information.ReadyStatistics;
import org.diploma.sulima.services.information.StatisticsBuilder;
import org.diploma.sulima.services.search.FoundOutput;
import org.diploma.sulima.services.search.SearchBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Getter
public class DataService {

    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final LemmaRawRepository lemmaRawRepository;
    private final IndexRepository indexRepository;
    private final FoundPageRepository foundPageRepository;
    private final SiteRepository siteRepository;
    private final SiteConfiguration siteConfiguration;

    @Value("${userAgent}")
    private String userAgent;

    @Async
    public void startIndexing() {
        IndexingBuilder.start(this);
    }

    public int statusSiteUpdate(String url, String status) {
        return siteRepository.updateSiteStatus(url, status);
    }

    public Object getResultQuery(HashMap parameters) {

        if (parameters.get("query") == "") return ErrorList.emptyRequest();

        if (parameters.get("site") == null && siteRepository.allIndexed() == 0) return ErrorList.notAllSitesInTheIndex();

        if (parameters.get("site") != null && siteRepository.currentSiteIndexed(parameters.get("site").toString()) == 0)
            return ErrorList.currentSiteNotIndexed();

        parameters.put("dataService", this);

        FoundOutput result = SearchBuilder.processingSearch(parameters);

        if (!result.isResult()) return ErrorList.noMatchesFound();
        return result;
    }

    public ReadyStatistics getStatistics() {
        return StatisticsBuilder.processingStatistics(this);
    }

    public PageOneBuilder addPageIndexResponse(String uri) {

        PageOneBuilder newIndex = new PageOneBuilder(userAgent, uri, this);

        return newIndex;
    }

    @Async
    public void addPageIndexReindex(PageOneBuilder newIndex) {
        if (newIndex.isReindex()) newIndex.refillIndex();
    }
}
