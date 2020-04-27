package dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoCollection;
import model.Currency;
import model.Product;
import model.User;
import org.bson.Document;
import rx.Observable;

public class MongoDao implements Dao {
    public MongoDao(MongoCollection<Document> users, MongoCollection<Document> products) {
        this.users = users;
        this.products = products;
    }

    @Override
    public rx.Observable<User> getUsers() {
        return users.find().toObservable().map(User::new);
    }

    @Override
    public Observable<Product> getProductsForUser(int userId) {
        return users
                .find(Filters.eq("id", userId)).toObservable()
                .map(doc -> Currency.valueOf(doc.getString("currency")))
                .flatMap(userCurrency -> products
                        .find().toObservable()
                        .map(doc -> new Product(doc).convertCurrency(userCurrency)));
    }

    @Override
    public Observable<Boolean> registerUser(User user) {
        return insert(user.getId(), user.toDocument(), users);
    }

    @Override
    public Observable<Boolean> addProduct(Product product) {
        return insert(product.getId(), product.toDocument(), products);
    }

    private Observable<Boolean> insert(int id, Document document, MongoCollection<Document> collection) {
        return collection
                .find(Filters.eq("id", id)).toObservable()
                .singleOrDefault(null)
                .flatMap(foundDoc -> {
                    if (foundDoc == null) {
                        return collection.insertOne(document).asObservable().isEmpty().map(it -> !it);
                    } else {
                        return Observable.just(false);
                    }
                });
    }

    private final MongoCollection<Document> users;
    private final MongoCollection<Document> products;
}
