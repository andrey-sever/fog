package org.diploma.sulima.services;

import org.diploma.sulima.data.*;
import org.diploma.sulima.model.Page;
import org.diploma.sulima.model.Site;
import org.diploma.sulima.repository.LemmaRawRepository;
import org.diploma.sulima.repository.PageRepository;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.concurrent.*;

public class IndexingBuilder {

    private static DataService dataService;
    private static HashSet<String> listLinks;
    private static String userAgent;
    private static PagePackageRecords pagePackageRecords;
    private static ForkJoinPool forkJoinPool;

    public static <T> void start(DataService transmittedDataService) {

        long start = System.currentTimeMillis(); // TODO: 30.06.2022 ДЛЯ ТЕСТОВ. УДАЛИТЬ
        dataService = transmittedDataService;
        userAgent = dataService.getUserAgent();

        recreateTables();

        processingWithVerificationListSites();

        if (listLinks.isEmpty()) return;

        // phase №1
        processingPage();

        // phase №2
        processingLemmaRaw();

        createTablesLemmaIndex();

        // completion
        changeSiteStatuses();
        updateCountLemmaSite();

        System.out.println(Long.toString((System.currentTimeMillis() - start) / 1000));// TODO: 30.06.2022 ДЛЯ ТЕСТОВ. УДАЛИТЬ
    }

    public static void createTablesLemmaIndex() {

        dataService.getLemmaRawRepository().fillInTableLemma();
        dataService.getLemmaRawRepository().fillInTableIndex();
        dataService.getIndexRepository().createIndexPageId();
        dataService.getIndexRepository().createIndexLemmaId();
    }

    private static void recreateTables() {

        dataService.getSiteRepository().deleteAll();
        dataService.getSiteRepository().resetAutoIncrement();
        dataService.getSiteRepository().saveAll(dataService.getSiteConfiguration().getSites());
        dataService.getPageRepository().dropWorkerTables();
        dataService.getPageRepository().createTablePage();
        dataService.getLemmaRepository().createTableLemma();
        dataService.getIndexRepository().createTableIndex();
        dataService.getLemmaRawRepository().createTableLemmaRaw();
    }

    private static void processingWithVerificationListSites() {

        HashSet<String> listLinksRaw = dataService.getSiteRepository().getAllUrl();
        Document doc = null;

        for (String url : listLinksRaw) {

            int responseStatus = 0;
            ConnectUrl docStatus = ConnectUrl.buildConnectUrl().getConnectUrl(url);
            doc = docStatus.getDoc();
            responseStatus = docStatus.getResponseStatus();

            if (responseStatus == 0) responseStatus = doc.connection().response().statusCode();

            if (responseStatus != 200) {
                dataService.getSiteRepository().updateSiteStatus(url, SiteStatus.FAILED.toString());
                dataService.getSiteRepository().updateSiteLastError(url, "Главная страница сайта недоступна.");
            }
            listLinks = dataService.getSiteRepository().getAllUrl();
        }
    }

    private static void processingPage() {

        pagePackageRecords = new PagePackageRecords();

        ViewedLinks.newViewed();
        Intervals.newSiteTime();
        WaitStop.newWaitStop();

        forkJoinPool = new ForkJoinPool();
        List<SaveLinks> tasks = new ArrayList<>();
        for (String startLink : listLinks) {
            int siteId = dataService.getSiteRepository().findSiteIdByUrl(startLink);
            tasks.add(new SaveLinks(startLink, startLink, siteId));
        }
        forkJoinPool.invoke(new SaveLinks(tasks));

        //Остаток ("хвост") записать в базу данных
        if (pagePackageRecords.getCount() > 0) {
            dataService.getPageRepository().saveAll(pagePackageRecords.getRecords());
            pagePackageRecords.newRecords();
        }
    }

    public static void stopIndexing() {
        WaitStop.stop();
//        while (!WaitStop.getStopped());
    }

    private static void processingLemmaRaw() {

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int countThreads = availableProcessors > 2 ? availableProcessors - 1 : availableProcessors;
        int pageRecord = (int) dataService.getPageRepository().count();
        int pieceSize = 0;

        if (countThreads > pageRecord) {
            countThreads = pageRecord;
            pieceSize = 1;
        } else {
            pieceSize = pageRecord / countThreads;
        }

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < countThreads; i++) {

            int start = i * pieceSize + 1;
            int end = start + pieceSize - 1;
            if (i == countThreads - 1) end = pageRecord;
            threads.add(new LemmaRawBuilder(start, end));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void changeSiteStatuses() {

        String errorDescription = null;

        if (WaitStop.getStop()) {
            WaitStop.stopped();
            errorDescription = "Остановлено пользователем.";
        }

        for (String url : listLinks) {
            String siteStatus = errorDescription == null ? SiteStatus.INDEXED.toString() : SiteStatus.FAILED.toString();
            dataService.statusSiteUpdate(url, siteStatus);
            dataService.getSiteRepository().updateSiteLastError(url, errorDescription);
        }
    }

    private static void updateCountLemmaSite() {

        for (Site site : dataService.getSiteRepository().findAll()) {
            if (site.getStatus() == SiteStatus.INDEXED) {
                int id = site.getId();
                int countLemma = dataService.getLemmaRawRepository().countBySiteId(id);
                dataService.getSiteRepository().updateSiteCountLemma(countLemma, id);
            }
        }
    }

    public static PagePackageRecords getPackageRecords() {
        return pagePackageRecords;
    }

    public static void savePackageRecords(Set<Page> pageSet) {
        dataService.getPageRepository().saveAll(pageSet);
    }

    public static LemmaRawRepository getLemmaRawRepository() {
        return dataService.getLemmaRawRepository();
    }

    public static PageRepository getPageRepository() {
        return dataService.getPageRepository();
    }

    public static String getUserAgent() {
        return userAgent;
    }

}
