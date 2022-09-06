package org.diploma.sulima.controller;

import lombok.RequiredArgsConstructor;
import org.diploma.sulima.data.DataService;
import org.diploma.sulima.data.PageOneBuilder;
import org.diploma.sulima.services.response.ErrorList;
import org.diploma.sulima.services.IndexingBuilder;
import org.diploma.sulima.services.response.ResultTrue;
import org.diploma.sulima.services.information.ReadyStatistics;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("/startIndexing")
    public Object startIndex() {

        if (dataService.getSiteRepository().thereIsIndexing() == 0) {
            dataService.startIndexing();
            return new ResultTrue();
        } else {
            return ErrorList.indexingStarted();
        }
    }

    @GetMapping("/stopIndexing")
    public Object stopIndex() {

        if (dataService.getSiteRepository().thereIsIndexing() != 0) {
            IndexingBuilder.stopIndexing();
            return new ResultTrue();
        } else {
            return ErrorList.indexingNotStarted();
        }
    }

    @RequestMapping("/indexPage")
    public Object addPageIndex(@RequestParam(name = "url", required = false) String url) {

        PageOneBuilder newIndex = dataService.addPageIndexResponse(url);
        Object response = newIndex.errorsFound();
        dataService.addPageIndexReindex(newIndex);

        return response;
    }

    @RequestMapping("/search")
    public Object searchBar(@RequestParam(name = "query", required = false) String query,
                            @RequestParam(name = "site", required = false) String site,
                            @RequestParam(name = "offset", required = false) int offset,
                            @RequestParam(name = "limit", required = false) int limit)  {

        HashMap parameters = new HashMap<>();
        parameters.put("query", query);
        parameters.put("site", site);
        parameters.put("offset", offset);
        parameters.put("limit", limit);

        return dataService.getResultQuery(parameters);
    }

    @GetMapping("/statistics")
    public ReadyStatistics statistics() {
        return dataService.getStatistics();
    }

}
