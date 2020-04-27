package client;

import model.SearchRequest;
import model.SearchResult;

public interface SearchClient {
    SearchResult query(SearchRequest searchRequest);
}
