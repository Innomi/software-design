package dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import model.Instrument;
import org.bson.Document;
import rx.Observable;
import rx.functions.Func2;

public class MongoMarketDao implements MarketDao {
    public MongoMarketDao(MongoCollection<Document> instruments) {
        this.instruments = instruments;
    }

    @Override
    public Observable<Success> addCompany(String name, int instrumentCount, double instrumentPrice) {
        return instruments.find(Filters.eq("companyName", name)).toObservable().isEmpty()
                .flatMap(isEmpty -> {
                    if (isEmpty) {
                        return instruments.insertOne(new Instrument(name, instrumentCount, instrumentPrice).toDocument());
                    } else {
                        return Observable.error(new IllegalArgumentException("Instrument of company with name '" + name + "' already registered"));
                    }
                });
    }

    @Override
    public Observable<Instrument> getInstruments() {
        return instruments.find().toObservable().map(Instrument::new);
    }

    @Override
    public Observable<Success> addInstrument(String company, int count) {
        return changeInstrument(company, Instrument::add, count);
    }

    @Override
    public Observable<Instrument> getInstrumentInfo(String company) {
        return instruments.find(Filters.eq("companyName", company)).toObservable().map(Instrument::new);
    }

    @Override
    public Observable<Success> buyInstrument(String company, int count) {
        return changeInstrument(company, Instrument::sub, count);
    }

    @Override
    public Observable<Success> changeInstrumentPrice(String company, double newInstrumentPrice) {
        return changeInstrument(company, Instrument::changePrice, newInstrumentPrice);
    }

    private <T> Observable<Success> changeInstrument(String company, Func2<Instrument, T, Instrument> action, T parameter) {
        return instruments.find(Filters.eq("companyName", company)).toObservable().map(Instrument::new).defaultIfEmpty(null)
                .flatMap(instrument -> {
                    if (instrument == null) {
                        return Observable.error(new IllegalArgumentException("Instrument of company '" + company + "' isn't registered"));
                    }
                    return instruments.replaceOne(Filters.eq("companyName", company), action.call(instrument, parameter).toDocument())
                            .map(a -> Success.SUCCESS);
                });
    }

    private final MongoCollection<Document> instruments;
}
