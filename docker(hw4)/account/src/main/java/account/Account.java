package account;

import com.mongodb.rx.client.Success;
import model.Instrument;
import rx.Observable;

public interface Account {
    Observable<Success> registerUser(int id);
    Observable<Success> addMoney(int id, double money);
    Observable<Double> getMoney(int id);
    Observable<Instrument> getInstruments(int id);
    Observable<Double> getAllMoney(int id);
    Observable<Success> buyInstrument(int id, String company, int count);
    Observable<Success> sellInstrument(int id, String company, int count);
}