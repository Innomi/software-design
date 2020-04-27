package controller;

import dao.MarketDao;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.Instrument;
import rx.Observable;
import util.ParameterUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class RxHttpController implements HttpController {
    public RxHttpController(MarketDao marketDao) {
        this.marketDao = marketDao;
    }

    @Override
    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        switch (path) {
            case("add_company"):
                return addCompany(request);
            case("get_instruments"):
                return getInstruments(request);
            case("add_instrument"):
                return addInstrument(request);
            case("get_instrument_price"):
                return getInstrumentPrice(request);
            case("get_instrument_count"):
                return getInstrumentCount(request);
            case("buy_instrument"):
                return buyInstrument(request);
            case("change_instrument_price"):
                return changeInstrumentPrice(request);
        }
        return Observable.just("Unknown request : " + path);
    }

    private <T> Observable<String> addCompany(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("name", "instrument_count", "instrument_price"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        String company = request.getQueryParameters().get("name").get(0);
        int instrumentCount = ParameterUtil.getIntParameter("instrument_count", request);
        double instrumentPrice = Double.parseDouble(request.getQueryParameters().get("instrument_price").get(0));
        return marketDao.addCompany(company, instrumentCount, instrumentPrice).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> getInstruments(HttpServerRequest<T> request) {
        return marketDao.getInstruments().map(Objects::toString).reduce("", (s1, s2) -> s1 + ",\n" + s2);
    }

    private <T> Observable<String> addInstrument(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("company_name", "count"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        String companyName = request.getQueryParameters().get("company_name").get(0);
        int instrumentCount = ParameterUtil.getIntParameter("count", request);
        return marketDao.addInstrument(companyName, instrumentCount).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> buyInstrument(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("company_name", "count"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        String companyName = request.getQueryParameters().get("company_name").get(0);
        int count = ParameterUtil.getIntParameter("count", request);
        return marketDao.buyInstrument(companyName, count).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> getInstrumentPrice(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Collections.singletonList("company_name"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        String companyName = request.getQueryParameters().get("company_name").get(0);
        return marketDao.getInstrumentInfo(companyName).map(Instrument::getPrice).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> getInstrumentCount(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Collections.singletonList("company_name"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        String companyName = request.getQueryParameters().get("company_name").get(0);
        return marketDao.getInstrumentInfo(companyName).map(Instrument::getCount).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> changeInstrumentPrice(HttpServerRequest<T> request) {
        Optional<String> error = ParameterUtil.checkExist(request, Arrays.asList("company_name", "new_instrument_price"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        String companyName = request.getQueryParameters().get("company_name").get(0);
        double newInstrumentPrice = Double.parseDouble(request.getQueryParameters().get("new_instrument_price").get(0));
        return marketDao.changeInstrumentPrice(companyName, newInstrumentPrice).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private final MarketDao marketDao;
}
