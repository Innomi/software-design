import account.AccountImpl;
import client.HttpMarketClient;
import controller.RxHttpController;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class Main {
    public static void main(String[] args) {
        RxHttpController controller = new RxHttpController(new AccountImpl(new HttpMarketClient()));

        HttpServer.newServer(1313)
                .start((req, resp) -> {
                    Observable<String> response = controller.getResponse(req);
                    return resp.writeString(response.map(r -> r + System.lineSeparator()));
                })
                .awaitShutdown();
    }
}
