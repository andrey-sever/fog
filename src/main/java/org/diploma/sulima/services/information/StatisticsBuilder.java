package org.diploma.sulima.services.information;

import org.diploma.sulima.data.DataService;
import org.diploma.sulima.data.SiteStatus;
import org.diploma.sulima.model.Site;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsBuilder {

    private static DataService dataService;
    private static int allLemmas;
    private static int allPages;

    public static ReadyStatistics processingStatistics(DataService transmittedDataService) {

        dataService = transmittedDataService;
        allLemmas = 0;
        allPages = 0;
        List<Detailed> detailedList = new ArrayList<>();
        boolean result = true;

        List<Site> siteList = dataService.getSiteRepository().findAll();
        if (siteList.isEmpty()) return resultFalse();
        for (Site site : siteList) {
            detailedList.add(recordDetailed(site));
        }
        Statistics newStatistics = new Statistics(getTotal(siteList.size()), detailedList);

        return  new ReadyStatistics(result, newStatistics);
    }

    private static Detailed recordDetailed(Site site) {

        int pages = 0;
        int lemmas = 0;
        int id = site.getId ();
        String url = site.getUrl();
        String name = site.getName();
        String status = site.getStatus().toString();
        Date statusTime = site.getStatusTime();
        String error = site.getLastError();

        if (status == SiteStatus.INDEXED.toString()) {
            lemmas = dataService.getSiteRepository().getCountLemmaById(id);
            pages = dataService.getPageRepository().getStatisticsPages(id);
        }

        allLemmas += lemmas;
        allPages += pages;

        return new Detailed(url, name, status, statusTime.getTime(), error,
                pages, lemmas);
    }

    private static Total getTotal(int countSite) {
        boolean isIndexing = true;
        return new Total(countSite, allPages, allLemmas, isIndexing);
    }

    private static ReadyStatistics resultFalse() {
        Statistics statisticsFalse = new Statistics(new Total(), null);
        return new ReadyStatistics(false, statisticsFalse);
    }
}
