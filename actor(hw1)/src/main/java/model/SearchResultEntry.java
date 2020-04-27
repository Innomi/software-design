package model;

public class SearchResultEntry {
    public SearchResultEntry(String text, String url) {
        this.url = url;
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "{ text : \"" + text + "\", url : \"" + url + "\" }";
    }

    private final String url;
    private final String text;
}
