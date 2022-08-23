package org.diploma.sulima.data;

import org.diploma.sulima.model.Page;
import org.diploma.sulima.model.Site;
import org.diploma.sulima.services.ConnectUrl;
import org.diploma.sulima.services.response.ErrorList;
import org.diploma.sulima.services.GeneralMethods;
import org.diploma.sulima.services.response.ResultTrue;

import java.util.Optional;

public class PageOneBuilder {

    private ConnectUrl docStatus;
    private String site;
    private int siteId;
    private String urn;
    private DataService dataService;
    private boolean reindex;

    public PageOneBuilder(String userAgent, String uri, DataService dataService) {
        this.docStatus = ConnectUrl.buildConnectUrl(userAgent).getConnectUrl(uri);
        this.site = GeneralMethods.getSiteFromUri(uri);
        this.urn = GeneralMethods.getUrn(uri);
        this.dataService = dataService;
        this.reindex = false;
    }

    public void refillIndex() {

        if (dataService.getPageRepository().existsBySiteIdAndPath(siteId, urn) != 0) {
            removePageFromIndex();
        }

        WaitStop.newWaitStop();

        createRecordPage();

        fillLemmaRawBuilder();

        fillIndex();
    }

    public Object errorsFound() {

        if (docStatus.getDoc() == null) {
            return ErrorList.pageNotFound();
        }

        Optional<Site> optionalSite = dataService.getSiteRepository().findFirstByUrl(site);

        if (optionalSite.isEmpty() || optionalSite.get().getStatus() == SiteStatus.FAILED) {
            return  ErrorList.pageNotList();
        }

        this.siteId = dataService.getSiteRepository().findSiteIdByUrl(site);

        reindex = true;

        return new ResultTrue();
    }

    public boolean isReindex() {
        return reindex;
    }

    private void removePageFromIndex() {

        int idPage = getPageId();

        dataService.getPageRepository().deleteById(idPage);

        dataService.getIndexRepository().deleteAllById(idPage);

        dataService.getLemmaRawRepository().deleteBySiteIdAndPath(siteId, urn);
    }

    private int getPageId() {
        return dataService.getPageRepository().findFirstBySiteIdAndPath(siteId, urn).get().getId();
    }

    private void createRecordPage() {
        dataService.getPageRepository().save(
                new Page(siteId, urn, docStatus.getDoc().connection().response().statusCode(), docStatus.getDoc().toString()));
    }

    private void fillLemmaRawBuilder() {

        int pageId = getPageId();

        Thread thread = new Thread(new LemmaRawBuilder(pageId, pageId, dataService));

        thread.start();

        try {

            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fillIndex() {
        dataService.getLemmaRawRepository().fillInTableIndexByPath(urn);
    }
}
