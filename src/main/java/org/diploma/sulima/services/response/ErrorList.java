package org.diploma.sulima.services.response;

public class ErrorList {

    private static final boolean RESULT = false;

    public static ResponseError emptyRequest() {
        return new ResponseError(RESULT, "Задан пустой запрос.");
    }

    public static ResponseError noMatchesFound() {
        return new ResponseError(RESULT, "Совпадений не найдено.");
    }

    public static ResponseError notAllSitesInTheIndex() {
        return new ResponseError(RESULT, "Не все сайты проиндексированы.");
    }

    public static ResponseError currentSiteNotIndexed() {
        return new ResponseError(RESULT, "Текущий сайт не проиндексирован.");
    }

    public static ResponseError indexingStarted() {
        return new ResponseError(RESULT, "Индексация уже запущена.");
    }

    public static ResponseError indexingNotStarted() {
        return new ResponseError(RESULT, "Индексация не запущена.");
    }

    public static ResponseError pageNotFound() {
        return new ResponseError(RESULT, "Страница не найдена.");
    }

    public static ResponseError pageNotList() {
        return new ResponseError(RESULT, "Страница не из нашего списка сайтов.");
    }
}
