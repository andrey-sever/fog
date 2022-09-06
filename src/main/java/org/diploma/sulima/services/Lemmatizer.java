package org.diploma.sulima.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lemmatizer {

    private final String[] PARTS_SPEECH_EXCEPTIONS = {"МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ"};
    private LuceneMorphology luceneMorphology;
    private List<String> listLemma;

    public Lemmatizer() {
        this.listLemma = new ArrayList<>();
        try {
            this.luceneMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getListLemma(String text) {

        text = textCorrection(text);
        if (text.isEmpty()) return listLemma;
        String[] temp = text.split(" ");

        for (String word : temp) {

            word = word.toLowerCase();
            if (!wordIsValid(word) || word.length() == 1) continue;
            List<String> variantsLemma = luceneMorphology.getNormalForms(word);
            addWordToList(variantsLemma, word);
        }

        return listLemma;
    }

    private String textCorrection(String text) {

        String textCorr = text.replaceAll("[-]", " ");
        textCorr = textCorr.replaceAll("[^а-я^А-Я^ ]", " "); //Пока без английского/
        textCorr = textCorr.replaceAll("[\\s]+", " ").trim();

        return textCorr;
    }

    private Boolean wordIsValid(String word) {

        String wordExceptions = luceneMorphology.getMorphInfo(word).get(0);

        for (String part : PARTS_SPEECH_EXCEPTIONS) {
            if (wordExceptions.contains(part)) return false;
        }

        return true;
    }

    private void addWordToList(List<String> variantsLemma, String word) {

        String wordLemma;

        if (variantsLemma.size() == 1) {
            wordLemma = variantsLemma.get(0);
        } else {
            if (variantsLemma.contains(word)) {
                wordLemma = word;
            } else {
                wordLemma = variantsLemma.get(0);
            }
        }

        listLemma.add(wordLemma);
    }
}
