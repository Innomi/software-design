package model;

public enum Currency {
    USD,
    EUR,
    RUB;

    public double getRate(Currency currency) {
        return currency.getRate() / getRate();
    }

    private double getRate() {
        switch (this) {
            case USD:
                return 1;
            case EUR:
                return 1.08;
            case RUB:
                return 0.0001;
        }
        return 1;
    }
}
