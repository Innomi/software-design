package client;

public interface MarketClient {
    void buyInstrument(String companyName, int count);
    void sellInstrument(String companyName, int count);
    double getInstrumentPrice(String companyName);
    int getInstrumentCount(String companyName);
}
