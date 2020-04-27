package model;

import org.bson.Document;

public class Product {
    public Product(int id, String name, Currency currency, double price) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.price = price;
    }

    public Product(Document document) {
        this(
                document.getInteger("id"),
                document.getString("name"),
                Currency.valueOf(document.getString("currency")),
                document.getDouble("price")
        );
    }

    public Document toDocument() {
        return new Document().append("id", id).append("name", name).append("currency", currency.toString()).append("price", price);
    }

    public Product convertCurrency(Currency otherCurrency) {
        return new Product(id, name, otherCurrency, price * currency.getRate(otherCurrency));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{ id=" + String.valueOf(id) + ", name=" + name + ", currency=" + currency + ", price=" + String.valueOf(price) + " }";
    }

    private final int id;
    private final String name;
    private final Currency currency;
    private final double price;
}
