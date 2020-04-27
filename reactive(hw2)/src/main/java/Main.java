import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;
import controller.HttpController;
import controller.RxHttpController;
import dao.MongoDao;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class Main {
    public static void main(String[] args) {
        HttpController controller = new RxHttpController(createMongoDao());

        HttpServer.newServer(8080)
                .start((req, resp) -> {
                    Observable<String> response = controller.getResponse(req);
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }

    private static MongoDao createMongoDao() {
         MongoDatabase db = MongoClients.create("mongodb://localhost:27017").getDatabase("catalog");
         return new MongoDao(db.getCollection("users"), db.getCollection("products"));
    }
}
