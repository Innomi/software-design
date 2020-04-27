package dao;

import model.Product;
import model.User;
import rx.Observable;

public interface Dao {
    Observable<User> getUsers();
    Observable<Product> getProductsForUser(int userId);
    Observable<Boolean> registerUser(User user);
    Observable<Boolean> addProduct(Product product);
}
