package model;

import org.bson.Document;

public class Instrument {
    public Instrument(String companyName, int count, double price) {
        this.companyName = companyName;
        this.count = count;
        this.price = price;
    }

    public Instrument(Document document) {
        this(document.getString("companyName"), document.getInteger("count"), document.getDouble("price"));
    }

    public Document toDocument() {
        return new Document().append("companyName", companyName).append("count", count).append("price", price);
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getCount() {
        return count;
    }

    public double getPrice() {
        return price;
    }

    public Instrument changePrice(double newPrice) {
        return new Instrument(companyName, count, newPrice);
    }

    public Instrument add(int count) {
        return new Instrument(companyName, this.count + count, price);
    }

    public Instrument sub(int count) {
        if (this.count < count) {
            return this;
        }
        return new Instrument(companyName, this.count - count, price);
    }

    @Override
    public String toString() {
        return "Stocks {\n" +
                "  companyName : " + companyName + ",\n" +
                "  count : " + String.valueOf(count) + ",\n" +
                "  price : " + String.valueOf(price) + "\n" +
                "}";
    }

    private final String companyName;
    private final int count;
    private final double price;
}
