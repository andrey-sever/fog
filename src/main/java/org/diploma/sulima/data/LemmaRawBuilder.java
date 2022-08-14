package org.diploma.sulima.data;

import org.diploma.sulima.data.entity.LemmaRaw;
import org.diploma.sulima.data.entity.Page;
import org.diploma.sulima.repository.LemmaRawRepository;
import org.diploma.sulima.repository.PageRepository;
import org.diploma.sulima.services.IndexingBuilder;
import org.diploma.sulima.services.Lemmatizer;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class LemmaRawBuilder extends Thread {

    private final float WEIGHT_TITLE = 1.0F;
    private final float WEIGHT_BODY = 0.8F;
    private final int PACKAGE_SIZE = 500;
    private List<LemmaRaw> localListLemmaRaw = new ArrayList<>();
    private LemmaRawRepository lemmaRawRepository;
    private PageRepository pageRepository;
    private float ratioRank;
    private int siteId;
    private String path;

    private int start;
    private int end;

    public LemmaRawBuilder(int start, int end) {
        this.start = start;
        this.end = end;
        this.lemmaRawRepository = IndexingBuilder.getLemmaRawRepository();
        this.pageRepository = IndexingBuilder.getPageRepository();
    }

    public LemmaRawBuilder(int start, int end, DataService dataService) {
        this.start = start;
        this.end = end;
        this.lemmaRawRepository = dataService.getLemmaRawRepository();
        this.pageRepository = dataService.getPageRepository();
    }

    @Override
    public void run() {

        for (int i = start; i <= end; i++) {

            if (WaitStop.getStop()) {
                break;
            }
            System.out.printf("%s%n", Thread.currentThread().getName()); // TODO: 20.07.2022 Для проверки. Потом удалить

            Page page = pageRepository.getById(i);

            siteId = page.getSiteId();
            path = page.getPath();
            String content = page.getContent();

            ratioRank = WEIGHT_TITLE;
            processText(Jsoup.parse(content).title());

            ratioRank = WEIGHT_BODY;
            processText(Jsoup.parse(content).body().text());

            if (localListLemmaRaw.size() >= PACKAGE_SIZE) recordingPackage();

        }
        if (localListLemmaRaw.size() != 0) recordingPackage();
    }

    private void processText(String text) {
        List<String> lemmaList;

        if (text.isEmpty()) return;
        Lemmatizer lemmatizer = new Lemmatizer();
        lemmaList = lemmatizer.getListLemma(text);

        for (String word : lemmaList) {
            localListLemmaRaw.add(new LemmaRaw(siteId, path, word, ratioRank));
        }
    }

    private void recordingPackage() {
        try {
            lemmaRawRepository.saveAll(localListLemmaRaw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        localListLemmaRaw = new ArrayList<>();
    }
}
