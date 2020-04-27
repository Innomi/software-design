package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpMarketClient implements MarketClient {
    @Override
    public void buyInstrument(String companyName, int count) {
        String response = sendRequest("buy_instrument", new HashMap<String, Object>() {{
                put("company_name", companyName);
                put("count", count);
        }});
        if (!response.equals("SUCCESS")) {
            throw new IllegalArgumentException(response);
        }
    }

    @Override
    public void sellInstrument(String companyName, int count) {
        String response = sendRequest("add_instrument", new HashMap<>() {{
                put("company_name", companyName);
                put("count", count);
            }});
        if (!response.equals("SUCCESS")) {
            throw new IllegalArgumentException(response);
        }
    }

    @Override
    public double getInstrumentPrice(String companyName) {
        String response = sendRequest("get_instrument_price", new HashMap<>() {{
            put("company_name", companyName);
        }});
        try {
            return Double.parseDouble(response);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(response);
        }
    }

    @Override
    public int getInstrumentCount(String companyName) {
        String response = sendRequest("get_instrument_count", new HashMap<>() {{
            put("company_name", companyName);
        }});
        try {
            return Integer.parseInt(response);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(response);
        }
    }

    private String sendRequest(String path, Map<String, Object> parameters) {
        String requestString = "http://localhost:8080/" + path + "?" + parameters.keySet().stream()
                .map(param -> param + "=" + parameters.get(param)).reduce((s1, s2) -> s1 + "&" + s2).get();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(requestString)).GET().build();
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body().trim();
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
