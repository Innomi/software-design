package model;

import org.bson.Document;

public class User {
    public User(int id, String login, Currency currency) {
        this.id = id;
        this.login = login;
        this.currency = currency;
    }

    public User(Document document) {
        this(document.getInteger("id"), document.getString("login"), Currency.valueOf(document.getString("currency")));
    }

    public Document toDocument() {
        return new Document().append("id", id).append("login", login).append("currency", currency.toString());
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "User{ id=" + String.valueOf(id) + ", login=" + login + ", currency=" + currency + " }";
    }

    private final int id;
    private final String login;
    private final Currency currency;
}
