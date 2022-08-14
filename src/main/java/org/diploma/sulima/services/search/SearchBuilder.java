package org.diploma.sulima.services.search;

import org.diploma.sulima.data.DataService;
import org.diploma.sulima.data.entity.LemmaRaw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SearchBuilder {

    private static DataService dataService;

    public static FoundOutput processingSearch(HashMap parameter) {
        dataService = (DataService) parameter.get("dataService");
        List<FoundPage> data;

        if ((int) parameter.get("offset") == 0) createTablesForQuery();
        data = SearchSystem.buildSearchSystem(parameter).getListObject();
        long count = dataService.getFoundPageRepository().count();
        boolean result = count == 0 ? false : true;
        return new FoundOutput(result, count, data);
    }

    private static void createTablesForQuery() {
        dataService.getFoundPageRepository().createTableFoundPageIfNotExist();
        dataService.getFoundPageRepository().deleteAll();
    }

}
