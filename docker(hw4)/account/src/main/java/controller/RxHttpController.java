package controller;

import account.Account;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;
import util.ParameterUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class RxHttpController implements HttpController {
    public RxHttpController(Account account) {
        this.account = account;
    }

    @Override
    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        switch (path) {
            case("register_user"):
                return registerUser(request);
            case("add_money"):
                return addMoney(request);
            case("get_instruments_info"):
                return getInstrumentsInfo(request);
            case("get_all_money"):
                return getAllMoney(request);
            case("buy_instrument"):
                return buyInstrument(request);
            case("sell_instrument"):
                return sellInstrument(request);
        }
        return Observable.just("Unknown request : " + path);
    }

    private <T> Observable<String> registerUser(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Collections.singletonList("id"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        int id = ParameterUtil.getIntParameter("id", request);
        return account.registerUser(id).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> addMoney(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("id", "money"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        int id = ParameterUtil.getIntParameter("id", request);
        double money = Double.parseDouble(request.getQueryParameters().get("money").get(0));
        return account.addMoney(id, money).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> getInstrumentsInfo(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Collections.singletonList("id"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        int id = ParameterUtil.getIntParameter("id", request);
        return account.getInstruments(id).map(Objects::toString).reduce("", (s1, s2) -> s1 + ",\n" + s2);
    }

    private <T> Observable<String> getAllMoney(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Collections.singletonList("id"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        int id = ParameterUtil.getIntParameter("id", request);
        return account.getAllMoney(id).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> buyInstrument(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("id", "company_name", "count"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        int id = ParameterUtil.getIntParameter("id", request);
        String companyName = request.getQueryParameters().get("company_name").get(0);
        int count = ParameterUtil.getIntParameter("count", request);
        return account.buyInstrument(id, companyName, count).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> sellInstrument(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("id", "company_name", "count"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        int id = ParameterUtil.getIntParameter("id", request);
        String companyName = request.getQueryParameters().get("company_name").get(0);
        int count = ParameterUtil.getIntParameter("count", request);
        return account.sellInstrument(id, companyName, count).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private final Account account;
}
