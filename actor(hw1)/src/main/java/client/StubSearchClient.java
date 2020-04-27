package client;

import model.SearchRequest;
import model.SearchResult;
import model.SearchResultEntry;

import java.util.ArrayList;
import java.util.List;

public class StubSearchClient implements SearchClient {
    public StubSearchClient(long responseDelayInMilliseconds) {
        this.responseDelayInMilliseconds = responseDelayInMilliseconds;
    }

    @Override
    public SearchResult query(SearchRequest searchRequest) {
        List<SearchResultEntry> searchResultEntries = new ArrayList<>();
        for (int i = 0; i < SEARCH_RESULT_SIZE; ++i) {
            searchResultEntries.add(new SearchResultEntry(
                    genText(i, searchRequest.getQueryText()),
                    genUrl(i, searchRequest.getQueryText())
            ));
        }
        try {
            Thread.sleep(responseDelayInMilliseconds);
            return new SearchResult(searchRequest.getSearchEngine(), searchResultEntries);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new SearchResult(searchRequest.getSearchEngine(), searchResultEntries);
        }
    }

    private static String genText(int i, String queryText) {
        return queryText + " #" + String.valueOf(i);
    }

    private static String genUrl(int i, String queryText) {
        return "https://www.link.org/" + queryText + String.valueOf(i);
    }

    private final static int SEARCH_RESULT_SIZE = 5;

    private final long responseDelayInMilliseconds;
}
