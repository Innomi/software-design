package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResult {
    public SearchResult() {
        searchResults = new HashMap<>();
    }

    public SearchResult(SearchEngine searchEngine, List<SearchResultEntry> searchResultEntries) {
        this();
        searchResults.put(searchEngine, searchResultEntries);
    }

    public void merge(SearchResult searchResult) {
        searchResults.putAll(searchResult.getSearchResults());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[\n");
        for (SearchEngine searchEngine : searchResults.keySet()) {
            StringBuilder entries = new StringBuilder();
            for (SearchResultEntry entry : searchResults.get(searchEngine)) {
                entries.append("      ").append(entry.toString()).append("\n");
            }
            result.append("  {\n")
                    .append("    search-engine : \"").append(searchEngine).append("\",\n")
                    .append("    entries : [\n")
                    .append(entries.toString())
                    .append("    ]\n")
                    .append("  }\n");
        }
        return result.append("]").toString();
    }

    public Map<SearchEngine, List<SearchResultEntry>> getSearchResults() {
        return searchResults;
    }

    private final Map<SearchEngine, List<SearchResultEntry>> searchResults;
}
