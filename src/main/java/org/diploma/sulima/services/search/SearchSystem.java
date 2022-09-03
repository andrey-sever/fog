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

        listLemmaId = new ArrayList<>();

        Lemmatizer lemmatizer = new Lemmatizer();

        List<String> listLemma = lemmatizer.getListLemma(query);

        countQuery = listLemma.size();

        int counterId = 0;

        Lemma tempLemma;

        for (String word : listLemma) {

            counterId ++;

            tempLemma = getTempLemma(word, counterId);

            if (!lemmaCorrect(tempLemma)) continue;

            listCleared.add(tempLemma);
        }

        return lemmaSortedBuilder(listCleared);
    }

    private List<Lemma> lemmaSortedBuilder(List<Lemma> finalList) {

        Collections.sort(finalList);

        if (site == null) finalList = revealGeneralLemma(finalList);

        if (site == null) {

            correctionFullRequestBySites(finalList);

        } else {

            finalList = correctionFullRequest(finalList, siteId);
        }

        long totalPages = getTotalPages();

        finalList = frequentlyOccurring(finalList, totalPages);

        for (Lemma lemma : finalList) listLemmaId.add(lemma.getId());

        return finalList;
    }

    private long getTotalPages() {

        if (site == null) {

            return dataService.getPageRepository().count();

        } else {
            return dataService.getPageRepository().countBySiteId(siteId);
        }
    }

    private Lemma getTempLemma(String word, int counterId) {

        if (site == null) {

            return dataService.getLemmaRepository().findGeneralLemma(word, counterId);

        } else {
            return dataService.getLemmaRepository().findLemmaAndSiteId(word, siteId);
        }
    }

    private boolean lemmaCorrect(Lemma tempLemma) {

        if (tempLemma == null) {

            return false;

        } else if (site != null && tempLemma.getSiteId() != siteId) {

            return false;

        } else {
            return true;
        }
    }

    private List<Lemma> revealGeneralLemma(List<Lemma> finalList) {

        List<Lemma> listOut = new ArrayList<>();

        for (Lemma lemma : finalList) {

            List<Lemma> foundLemmas = dataService.getLemmaRepository().findByLemma(lemma.getLemma());

            listOut.addAll(foundLemmas);
        }

        return listOut;
    }

    private List<Lemma> correctionFullRequestBySites(List<Lemma> finalList) {

        for (Integer siteId : dataService.getSiteRepository().getAllId()) {

            finalList = correctionFullRequest(finalList, siteId);
        }

        return finalList;
    }

    private List<Lemma> correctionFullRequest(List<Lemma> finalList, int siteId) {

        List<Lemma> deleteElement = listDeleteElement(finalList, siteId);

        if (deleteElement.size() != countQuery) finalList.removeAll(deleteElement);

        return finalList;
    }

    private List<Lemma> listDeleteElement(List<Lemma> finalList, int siteId) {

        List<Lemma> deleteElement = new ArrayList<>();

        for (Lemma lemma : finalList) {

            if (lemma.getSiteId() == siteId) deleteElement.add(lemma);
        }

        return deleteElement;
    }

    private List<Lemma> frequentlyOccurring(List<Lemma> listCleared, long totalPages) {

        if (listCleared.size() == 0) return listCleared;

        List<Lemma> listDelete = null;

        for (Lemma element : listCleared) {

            if (element.getFrequency() < totalPages * REPETITION_RATE) {

                listDelete = listDeleteElement(listCleared, element.getSiteId());
            }
        }

        if (listCleared.size() > listDelete.size() && listDelete.size() != 0) listCleared.removeAll(listDelete);

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
