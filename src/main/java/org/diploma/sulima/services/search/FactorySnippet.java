package org.diploma.sulima.services.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class FactorySnippet {

    private static String text;
    private static String textLowerCase;
    private static List<String> queryStemmer;
    private static List<String> listLines;
    private static final int PIECE_SIZE = 100;
    private static final int ERROR_RITE = 20;
    private static int numberPieces;

    public static String getSnippet(String query, String textBody) {
        activatingVariables(query, textBody);
        String snippetOut = createSnippet();
        return snippetOut;
    }

    private static void activatingVariables(String query, String textBody) {
        text = textBody;
        textLowerCase = textBody.toLowerCase();
        listLines = new ArrayList<>();
        queryStemmer = getListStemmer(textCorrection(query));

        if (text.length() < PIECE_SIZE * 3) {
            numberPieces = 1;
            return;
        }

        if (queryStemmer.size() == 1) {
            queryStemmer.add(queryStemmer.get(0));
            numberPieces = 2;
        } else if (queryStemmer.size() > 2) {
            numberPieces = 3;
        } else {
            numberPieces = 2;
        }
    }

    private static String createSnippet() {
        for (String word : queryStemmer) {
            int foundPosition = textLowerCase.indexOf(word);
            if (foundPosition == -1) continue;

            listLines.add(buildLine(foundPosition));
            if (listLines.size() == numberPieces) {
                break;
            }
        }
        return addBoldText(getOneString());
    }

    private static String buildLine(int position) {
        int start = getLeftPosition(position);
        int end = getRightPosition(start, position) + 1;
        String lineOut = text.substring(start, end);
        if (lineOut.charAt(lineOut.length() - 1) == ' ') lineOut = lineOut.concat("... ");

        removeLineFromText(start, end);

        return lineOut;
    }

    private static int getLeftPosition(int end) {
        int start = end - PIECE_SIZE < 0 ? 0 : end - PIECE_SIZE;
        String textTemp = removePunctuationMarks(text.substring(start, end + 1));
        int previousPosition = textTemp.length() - 1;
        int currentPosition = -1;

        do {
            currentPosition = textTemp.lastIndexOf(" ", previousPosition);
            if (currentPosition == previousPosition) {
                previousPosition --;
                continue;
            }
            if (Character.isUpperCase(textTemp.charAt(currentPosition + 1))) {
                break;
            } else {
                previousPosition = currentPosition;
            }
        } while (currentPosition != -1);

        return currentPosition == -1 ? end : end - (textTemp.length() - 1 - (currentPosition + 1));
    }

    private static int getRightPosition(int start, int position) {
        int outEnd;

        int wordLength = wholeWord(position, text).length();
        int calculatedEndLine = start + PIECE_SIZE + wordLength + ERROR_RITE;
        int validEndLine = calculatedEndLine > text.length() ? text.length() : calculatedEndLine;
        String textTemp = text.substring(start, validEndLine);

        int endLineByFoundWord = position -1 + wordLength - start;
        int point = textTemp.lastIndexOf(".");

        if (point == -1 || point < endLineByFoundWord) {
            outEnd = textTemp.lastIndexOf(" ") + start;
        } else {
            outEnd = point + start;
        }
        return outEnd;
    }

    private static String removePunctuationMarks(String line) {
        return line.replaceAll("[.,?!;:]", " ");
    }

    private static String wholeWord(int start, String inText) {
        int end = start;
        String regex = "[а-яА-Я]";
        boolean isChar = true;
        while (isChar) {
            end ++;
            isChar = Pattern.matches(regex, String.valueOf(inText.charAt(end)));
        }
        return inText.substring(start, end);
    }

    private static List<String> getListStemmer(String query) {
        List<String> listOut = new ArrayList<>();
        String[] queryArray = query.split(" ");
        for (String word : queryArray) {
            listOut.add(StemmerPorterRU.stem(word));
        }
        return listOut;
    }

    private static void removeLineFromText(int start, int end) {
        text = text.replace(text.substring(start, end), "");
        textLowerCase = text.toLowerCase().trim();
    }

    private static String getOneString() {
        String outString = "";
        for (String line : listLines) {
            outString += line;
        }
        return outString;
    }

    private static String textCorrection(String text) {
        text = text.replaceAll("[^а-я^А-Я^ ]", " ");
        text = text.replaceAll(" [а-яА-Я]{1,2} ", " ");
        return text.replaceAll("[\\s]+", " ").trim();
    }

    private static String addBoldText(String inText) {

        for (String word : getListReplacement(inText)) {
            inText = inText.replace(word, "<b>".concat(word).concat("</b>"));
        }
        return inText;
    }

    private static Set<String> getListReplacement(String inText) {
        int start = 0;
        int nextPosition = 0;
        Set<String> replacement = new HashSet<>();
        String outTextLowerCase = inText.toLowerCase();

        for (String stem : queryStemmer) {
            do {
                start = outTextLowerCase.indexOf(stem, nextPosition);

                if (start != -1) {
                    String word = wholeWord(start, inText);
                    replacement.add(word);
                    nextPosition = start + 1;
                }
            } while (start != -1);
            start = 0;
            nextPosition = 0;
        }
        return replacement;
    }
}
