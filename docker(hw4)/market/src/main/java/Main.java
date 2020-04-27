import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;
import controller.RxHttpController;
import dao.MarketDao;
import dao.MongoMarketDao;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class Main {
    public static void main(String[] args) {
        RxHttpController controller = new RxHttpController(createMarketDao());

        HttpServer.newServer(3131)
                .start((req, resp) -> {
                    Observable<String> response = controller.getResponse(req);
                    return resp.writeString(response.map(r -> r + System.lineSeparator()));
                })
                .awaitShutdown();
    }

    private static MarketDao createMarketDao() {
        MongoDatabase database = MongoClients.create("mongodb://localhost:27017").getDatabase("market");
        return new MongoMarketDao(database.getCollection("instruments"));
    }
}
