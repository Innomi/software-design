package account;

import client.MarketClient;
import com.mongodb.rx.client.Success;
import model.Instrument;
import model.User;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

public class AccountImpl implements Account {
    public AccountImpl(MarketClient marketClient) {
        this.marketClient = marketClient;
    }

    @Override
    public Observable<Success> registerUser(int id) {
        if (users.containsKey(id)) {
            return Observable.error(new IllegalArgumentException("User with id = " + id + " already exists"));
        }
        users.put(id, new User(id, 0.));
        return Observable.just(Success.SUCCESS);
    }

    @Override
    public Observable<Success> addMoney(int id, double money) {
        if (!users.containsKey(id)) {
            return Observable.error(new IllegalArgumentException("User with id = " + id + " doesn't exist"));
        }
        users.get(id).addMoney(money);
        return Observable.just(Success.SUCCESS);
    }

    @Override
    public Observable<Double> getMoney(int id) {
        return null;
    }

    @Override
    public Observable<Instrument> getInstruments(int id) {
        if (!users.containsKey(id)) {
            return Observable.error(new IllegalArgumentException("User with id = " + id + " doesn't exist"));
        }
        return Observable.from(users.get(id).getInstruments()).map(this::updateInstrumentPrice);
    }

    @Override
    public Observable<Double> getAllMoney(int id) {
        if (!users.containsKey(id)) {
            return Observable.error(new IllegalArgumentException("User with id = " + id + " doesn't exist"));
        }
        User user = users.get(id);
        return Observable.from(user.getInstruments()).map(this::updateInstrumentPrice)
                .map(Instrument::getPrice).defaultIfEmpty(0.).reduce(Double::sum)
                .map(x -> x + user.getMoney());
    }

    @Override
    public Observable<Success> buyInstrument(int id, String company, int count) {
        if (!users.containsKey(id)) {
            return Observable.error(new IllegalArgumentException("User with id = " + id + " doesn't exist"));
        }
        try {
            double price = marketClient.getInstrumentPrice(company);
            int availableCount = marketClient.getInstrumentCount(company);
            if (availableCount < count) {
                return Observable.error(new IllegalArgumentException("Not enough instrument on market"));
            }
            users.get(id).buyInstrument(company, price, count);
            marketClient.buyInstrument(company, count);
            return Observable.just(Success.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<Success> sellInstrument(int id, String company, int count) {
        if (!users.containsKey(id)) {
            return Observable.error(new IllegalArgumentException("User with id = " + id + " doesn't exist"));
        }
        try {
            double price = marketClient.getInstrumentPrice(company);
            users.get(id).sellInstrument(company, price, count);
            marketClient.sellInstrument(company, count);
            return Observable.just(Success.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Observable.error(e);
        }
    }

    private Instrument updateInstrumentPrice(Instrument instrument) {
        return instrument.changePrice(marketClient.getInstrumentPrice(instrument.getCompanyName()));
    }

    private final Map<Integer, User> users = new HashMap<>();
    private final MarketClient marketClient;
}
