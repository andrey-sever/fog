package org.diploma.sulima.data;

import org.diploma.sulima.model.Page;

import org.diploma.sulima.services.ConnectUrl;
import org.diploma.sulima.services.GeneralMethods;
import org.diploma.sulima.services.IndexingBuilder;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.concurrent.*;

public class SaveLinks extends RecursiveAction {

    private String uri;
    private String siteUrl = "";
    private int siteId = 0;
    private final int PACKAGE_SIZE = 500;
    private PagePackageRecords currentPagePackageRecords;

    public SaveLinks(List<SaveLinks> startList) {
        this.currentPagePackageRecords = IndexingBuilder.getPackageRecords();
        start(startList);
    }

    public SaveLinks(String uri, String siteUrl, int siteId) {
        this.uri = uri;
        this.siteUrl = siteUrl;
        this.siteId = siteId;
        this.currentPagePackageRecords = IndexingBuilder.getPackageRecords();
    }

    @Override
    protected void compute() {

        // TODO: 20.07.2022 Для теста. Потом удалить
        System.out.printf("%s -- sent( %d )%n", Thread.currentThread().getName(),
                ForkJoinTask.getPool() == null ? 0 : ForkJoinTask.getPool().getQueuedSubmissionCount());

        if (WaitStop.getStop()) {
            ForkJoinPool pool = ForkJoinTask.getPool();
            if (pool != null) {
                pool.shutdownNow();
            }
        } else {
            if (uri != null) {
                if (!ViewedLinks.containsUrl(uri.toLowerCase())) {
                    getDataFromLink();
                }
            }
        }
    }

    private void start(List<SaveLinks> startList) {

        List<SaveLinks> tasks = new ArrayList<>();
        for (SaveLinks saveLinks : startList) {
            tasks.add(new SaveLinks(saveLinks.uri, saveLinks.siteUrl, saveLinks.siteId));
        }
        ForkJoinTask.invokeAll(tasks);
    }

    private List<SaveLinks> createSubtask(HashSet<String> listLink) {

        List<SaveLinks> tasks = new ArrayList<>();
        for (String link : listLink) {
            tasks.add(new SaveLinks(link, siteUrl, siteId));
        }

        return tasks;
    }

    private void getDataFromLink() {

        Document doc = null;
        int responseStatus = 0;
        String content = "";

        GeneralMethods.sleep(Intervals.getSleep(uri));
        ConnectUrl docStatus = ConnectUrl.buildConnectUrl().getConnectUrl(uri);
        doc = docStatus.getDoc();
        responseStatus = docStatus.getResponseStatus();

        if (responseStatus == 0) {
            responseStatus = doc.connection().response().statusCode();
            content = doc.toString();
        }

        writeLink(responseStatus, content);

        if (content != "") {
            HashSet<String> listCurrent = LinkProcessing.getLinks(doc, siteUrl);
            if (!listCurrent.isEmpty()) {
                ForkJoinTask.invokeAll(createSubtask(listCurrent));
            }
        }
    }

    private void writeLink(int code, String content) {

        String curPath = GeneralMethods.getUrn(uri);

        synchronized (currentPagePackageRecords) {

            if (currentPagePackageRecords.getCount() >= PACKAGE_SIZE) {
                IndexingBuilder.savePackageRecords(currentPagePackageRecords.getRecords());
                currentPagePackageRecords.newRecords();
            }

            if (!ViewedLinks.containsUrl(uri.toLowerCase())) {
                Page newPage = new Page(siteId, curPath, code, content);
                currentPagePackageRecords.addPage(newPage);
                ViewedLinks.addUrl(uri.toLowerCase());
            }
        }
    }
}
