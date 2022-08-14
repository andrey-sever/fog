package org.diploma.sulima.data;

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
public class DataService {

    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private LemmaRawRepository lemmaRawRepository;
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private FoundPageRepository foundPageRepository;
    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteConfiguration siteConfiguration;

    @Value("${userAgent}")
    private String userAgent;

    public PageRepository getPageRepository() {
        return pageRepository;
    }

    public LemmaRawRepository getLemmaRawRepository() {
        return lemmaRawRepository;
    }

    public LemmaRepository getLemmaRepository() {
        return lemmaRepository;
    }

    public IndexRepository getIndexRepository() {
        return indexRepository;
    }

    public FoundPageRepository getFoundPageRepository() {
        return foundPageRepository;
    }

    public SiteRepository getSiteRepository() {
        return siteRepository;
    }

    public SiteConfiguration getSiteConfiguration() {
        return siteConfiguration;
    }

    public String getUserAgent() {
        return userAgent;
    }

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
        if (newIndex.isReindex()) {
            newIndex.refillIndex();
        }
    }
}
