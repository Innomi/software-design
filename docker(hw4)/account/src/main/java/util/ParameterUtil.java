package util;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import java.util.List;
import java.util.Optional;

public class ParameterUtil {
    public static <T> int getIntParameter(String paramName, HttpServerRequest<T> request) {
        return Integer.parseInt(request.getQueryParameters().get(paramName).get(0));
    }

    public static <T> Optional<String> checkExist(HttpServerRequest<T> request, List<String> params) {
        return params.stream().filter(key -> !request.getQueryParameters().containsKey(key))
                .reduce((s1, s2) -> s1 + ", " + s2)
                .map(s -> s + " parameters not found");
    }
}
