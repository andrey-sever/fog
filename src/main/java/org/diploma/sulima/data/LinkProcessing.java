package org.diploma.sulima.data;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public class LinkProcessing {

    private static final String[] EXTENSIONS_LIST = {"html", "php", "js"};

    public static HashSet<String> getLinks(Document doc, String siteUrl) {
        HashSet<String> listLinksCurrent = new HashSet<>();

        Elements links = doc.getElementsByTag("a");
        for (Element link : links) {
            String strHref = link.attr("href");

            if (strHref.contains("http")) {
                if (strHref.contains(siteUrl)) {
                    strHref = "/" + strHref.replace(siteUrl, "");
                } else {
                    continue;
                }
            }

            if (!uriValidity(strHref)) {
                continue;
            }
            String uriNext = link.absUrl("href");
            uriNext = uriCleaning(uriNext);

            if (!ViewedLinks.containsUrl(uriNext.toLowerCase())) {
                listLinksCurrent.add(uriNext);
            }
        }

        return listLinksCurrent;
    }

    private static String uriCleaning(String urlClean) {
        if (urlClean.contains(" ")) {
            urlClean = urlClean.replaceAll("(\s)", "");
        }
        if (urlClean.indexOf(":", 6) != -1) {
            urlClean = delPort(urlClean);
        }
        return urlClean;
    }

    private static String delPort(String str) {
        int start = str.indexOf(":", 6);
        while (start != -1) {
            int end = str.indexOf("/", start);
            if (end == -1) {
                end = str.length();
            }
            String strDel = str.substring(start, end);
            str = str.replace(strDel, "");
            start = str.indexOf(":", 6);
        }
        return str;
    }

    private static Boolean uriValidity(String curPath) {
        if (!curPath.contains("/") || banningCharacters(curPath)) {
            return false;
        } else if (curPath.contains("/") && !curPath.startsWith("/")) {
            return false;
        } else if (curPath.contains("html") && !curPath.endsWith("html")) {
            return false;
        } else {
            return correctExtension(curPath);
        }
    }

    private static Boolean banningCharacters(String curPath) {
        if (curPath.contains("PAGEN")) {
            return false;
        }
        return curPath.matches("(.*[#\\[\\]\\(\\){}].*)");
    }

    private static Boolean correctExtension(String curPath) {
        int start = curPath.lastIndexOf(".");
        if (start == -1) {
            return true;
        }
        String afterPoint = curPath.substring(start);
        for (String ext : EXTENSIONS_LIST) {
            if (afterPoint.contains(ext)) return true;
        }
        return false;
    }
}
