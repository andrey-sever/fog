package org.diploma.sulima.services.search;

import org.diploma.sulima.model.FoundPage;
import org.diploma.sulima.data.DataService;
import org.diploma.sulima.services.Lemmatizer;
import org.diploma.sulima.model.Lemma;
import org.diploma.sulima.model.Page;
import org.jsoup.Jsoup;

import java.util.*;

public class SearchSystem {

    private static final float REPETITION_RATE = 0.8F;

    private DataService dataService;
    private String query;
    private String site;
    private int offset;
    private int limit;
    private int countQuery;

    private int siteId = 0;
    private List<Integer> listLemmaId;

    public SearchSystem(HashMap parameter) {
        this.dataService = (DataService) parameter.get("dataService");
        this.query = parameter.get("query").toString();
        this.site = parameter.get("site") == null ? null : parameter.get("site").toString();
        this.offset = (int) parameter.get("offset");
        this.limit = (int) parameter.get("limit");
        if (site != null) this.siteId = dataService.getSiteRepository().findSiteIdByUrl(site);
    }

    public List<FoundPage> getListObject() {
        if (offset == 0) createFoundPage();
        return portionFoundPage();
    }

    private List<Lemma> queryToListLemmaSorted() {

        List<Lemma> listCleared = new ArrayList<>();

        Lemmatizer lemmatizer = new Lemmatizer();
        List<String> listLemma = lemmatizer.getListLemma(query);
        countQuery = listLemma.size();
        for (String lemma : listLemma) {

            if (site == null) {
                listCleared.addAll(allLemmaByName(lemma));
            } else {
                Lemma tempLemma = oneLemmaByNameAndSiteId(lemma, siteId);
                if(tempLemma != null) listCleared.add(tempLemma);
            }
        }
        return lemmaSortedBuilder(listCleared);
    }

    private List<Lemma> lemmaSortedBuilder(List<Lemma> finalList) {

        listLemmaId = new ArrayList<>();

        if (site == null) {
            correctionFullRequestAllSites(finalList);
        } else {
            finalList = correctionFullRequest(finalList, siteId);
        }

        finalList = frequentlyOccurring(finalList);
        for (Lemma lemma : finalList) listLemmaId.add(lemma.getId());

        return finalList;
    }

    private long getTotalPages(int siteId) {
        return dataService.getPageRepository().countBySiteId(siteId);
    }


    private List<Lemma> allLemmaByName(String lemma) {
        return dataService.getLemmaRepository().findByLemma(lemma);
    }

    private Lemma oneLemmaByNameAndSiteId(String lemma, int siteId) {
        return dataService.getLemmaRepository().findLemmaAndSiteId(lemma, siteId);
    }

    private List<Lemma> correctionFullRequestAllSites(List<Lemma> finalList) {

        for (Integer siteId : dataService.getSiteRepository().getAllId()) {
            finalList = correctionFullRequest(finalList, siteId);
        }

        return finalList;
    }

    private List<Lemma> correctionFullRequest(List<Lemma> finalList, int siteId) {

        List<Lemma> deleteElement = listDeleteElement(finalList, siteId);

        if (deleteElement.size() != countQuery) finalList.removeAll(deleteElement);
        Collections.sort(finalList);

        return finalList;
    }

    private List<Lemma> listDeleteElement(List<Lemma> finalList, int siteId) {

        List<Lemma> deleteElement = new ArrayList<>();

        for (Lemma lemma : finalList) {
            if (lemma.getSiteId() == siteId) deleteElement.add(lemma);
        }

        return deleteElement;
    }

    private List<Lemma> frequentlyOccurring(List<Lemma> listCleared) {

        if (listCleared.size() == 0) return listCleared;
        if (site == null) {
            listCleared = frequentLemmaAllSite(listCleared);
        } else {
            listCleared = frequentLemmaOneSite(listCleared, siteId);
        }

        return  listCleared;
    }

    private List<Lemma> frequentLemmaOneSite(List<Lemma> listCleared, int siteId) {

        long totalPages = getTotalPages(siteId);
        List<Lemma> listDelete = new ArrayList<>();

        for (Lemma element : listCleared) {
            if (element.getSiteId() == siteId && element.getFrequency() < totalPages * REPETITION_RATE) {
                listDelete = listDeleteElement(listCleared, element.getSiteId());
                break;
            }
        }
        if (countQuery > listDelete.size() && listDelete.size() != 0) listCleared.removeAll(listDelete);

        return listCleared;
    }

    private List<Lemma> frequentLemmaAllSite(List<Lemma> listCleared) {

        for (Integer siteId : dataService.getSiteRepository().getAllId()) {
            listCleared = frequentLemmaOneSite(listCleared, siteId);
        }

        return listCleared;
    }

    private List<Integer> listPageIdByQuery(List<Lemma> sortedLemmaList) {

        List<Integer> nextListPageId;
        List<Integer> listPageId = new ArrayList<>();

        for (Lemma lemma : sortedLemmaList) {

            int currentLemmaId = lemma.getId();
            int currentSiteId = lemma.getSiteId();
            if (listPageId.isEmpty()) {
                listPageId = getListPageId(currentLemmaId, currentSiteId);
                continue;
            } else {
                nextListPageId = getListPageId(currentLemmaId, currentSiteId);
            }

            listPageId.retainAll(nextListPageId);

            if (listPageId.isEmpty()) break;
        }

        return listPageId;
    }

    private List<Integer> getListPageId(int lemmaId, int lemmaSiteId) {
        return dataService.getIndexRepository().findByLemmaIdAndSiteIdOutPageId(lemmaId, lemmaSiteId);
    }

    private void createFoundPage() {

        List<Lemma> allLemmasSorted = queryToListLemmaSorted();
        List<Integer> listPageId;

        if (site == null) {
            listPageId = getAllListPageId(allLemmasSorted);
        } else {
            listPageId = listPageIdByQuery(allLemmasSorted);
        }
        dataService.getIndexRepository().fillInTableFoundPage(listPageId, listLemmaId);
    }

    private List<Integer> getAllListPageId(List<Lemma> allLemmasSorted) {

        List<Integer> listPageId = new ArrayList<>();

        for (Integer siteId : dataService.getSiteRepository().getAllId()) {

            List<Lemma> oneSiteLemmaSorted = new ArrayList<>();
            for (Lemma lemmaSorted : allLemmasSorted) {
                if (siteId == lemmaSorted.getSiteId()) oneSiteLemmaSorted.add(lemmaSorted);
            }

            if (oneSiteLemmaSorted.size() == 0) continue;

            listPageId.addAll(listPageIdByQuery(oneSiteLemmaSorted));
        }

        return listPageId;
    }

    public List<FoundPage> portionFoundPage() {

        List<FoundPage> portionOut = dataService.getFoundPageRepository().findPortionFoundPages(limit, offset);
        for (FoundPage foundPage : portionOut) {

            Map tittleSnippet = getTitleSnippet(foundPage.getPageId());
            foundPage.setTitle(tittleSnippet.get("title").toString());
            foundPage.setSnippet(tittleSnippet.get("snippet").toString());
        }

        return portionOut;
    }

    private Map getTitleSnippet(int pageId) {

        HashMap outMap = new HashMap<>();

        Page pageCurrent = dataService.getPageRepository().getById(pageId);
        String contentCurrent = pageCurrent.getContent();
        String title = Jsoup.parse(contentCurrent).title();
        String allText = Jsoup.parse(contentCurrent).text();
        String snippet = FactorySnippet.getSnippet(query, allText);
        outMap.put("title", title);
        outMap.put("snippet", snippet);

        return outMap;
    }

    public static SearchSystem buildSearchSystem(HashMap parameter) {
        return new SearchSystem(parameter);
    }
}
