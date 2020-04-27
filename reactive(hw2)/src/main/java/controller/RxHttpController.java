package controller;

import dao.Dao;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.Currency;
import model.Product;
import model.User;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class RxHttpController implements HttpController {
    public RxHttpController(Dao dao) {
        this.dao = dao;
    }

    @Override
    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        switch (path) {
            case ("get_users"):
                return getUsers();
            case ("get_products_for_user"):
                return getProductsForUser(request);
            case ("register_user"):
                return registerUser(request);
            case ("add_product"):
                return addProduct(request);
        }
        return Observable.just("Unknown command: " + path);
    }

    private <T> Observable<String> getUsers() {
        return dao.getUsers().map(Object::toString);
    }

    private <T> Observable<String> getProductsForUser(HttpServerRequest<T> request) {
        int id = Integer.parseInt(request.getQueryParameters().get("id").get(0));
        return dao.getProductsForUser(id).map(Object::toString);
    }

    private <T> Observable<String> registerUser(HttpServerRequest<T> request) {
        Map<String, List<String>> params = request.getQueryParameters();

        int id = Integer.parseInt(params.get("id").get(0));
        String login = params.get("login").get(0);
        Currency currency = Currency.valueOf(params.get("currency").get(0));

        return dao.registerUser(new User(id, login, currency)).map(Object::toString);
    }

    private <T> Observable<String> addProduct(HttpServerRequest<T> request) {
        Map<String, List<String>> params = request.getQueryParameters();

        int id = Integer.parseInt(params.get("id").get(0));
        String name = params.get("name").get(0);
        Currency currency = Currency.valueOf(params.get("currency").get(0));
        double price = Double.parseDouble(params.get("price").get(0));

        return dao.addProduct(new Product(id, name, currency, price)).map(Object::toString);
    }

    private final Dao dao;
}
