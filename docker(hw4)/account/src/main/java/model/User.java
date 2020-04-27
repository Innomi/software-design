package model;

import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User {
    public User(int id, double money) {
        this.id = id;
        this.money = money;
    }

    public Document toDocument() {
        Map<String, Object> document = new HashMap<>(instruments);
        document.put("id", id);
        document.put("money", money);
        return new Document(document);
    }

    public long getId() {
        return id;
    }

    public double getMoney() {
        return money;
    }

    public void addMoney(double addition) {
        money += addition;
    }

    public Collection<Instrument> getInstruments() {
        return instruments.values();
    }

    public void buyInstrument(String companyName, double price, int count) {
        if (price * count > money) {
            throw new IllegalArgumentException("Need more gold");
        }
        Instrument current = instruments.getOrDefault(companyName, new Instrument(companyName, 0, 0));
        instruments.put(companyName, current.add(count));
        money -= price * count;
    }

    public void sellInstrument(String companyName, double price, int count) {
        if (!instruments.containsKey(companyName) || instruments.get(companyName).getCount() < count) {
            throw new IllegalArgumentException("Not enough instruments");
        }
        Instrument current = instruments.get(companyName);
        instruments.put(companyName, current.sub(count));
        money += price * count;
    }

    private final int id;
    private double money;
    private final Map<String, Instrument> instruments = new HashMap<>();
}
