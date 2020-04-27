package dao;

import com.mongodb.rx.client.Success;
import model.Instrument;
import rx.Observable;

public interface MarketDao {
    Observable<Success> addCompany(String name, int instrumentCount, double instrumentPrice);
    Observable<Instrument> getInstruments();
    Observable<Success> addInstrument(String company, int count);
    Observable<Instrument> getInstrumentInfo(String company);
    Observable<Success> buyInstrument(String company, int count);
    Observable<Success> changeInstrumentPrice(String company, double newInstrumentPrice);
}
