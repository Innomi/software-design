package model;

public class SearchRequest {
    public SearchRequest(SearchEngine searchEngine, String query) {
        this.searchEngine = searchEngine;
        this.query = query;
    }

    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public String getQueryText() {
        return query;
    }

    private final SearchEngine searchEngine;
    private final String query;
}
