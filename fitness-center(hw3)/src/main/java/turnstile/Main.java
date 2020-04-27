package turnstile;

import com.github.jasync.sql.db.Connection;
import database.DaoConnection;
import io.reactivex.netty.protocol.http.server.HttpServer;
import org.joda.time.LocalDateTime;
import rx.Observable;
import turnstile.command.EnterCommand;
import turnstile.command.ExitCommand;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Connection connection = DaoConnection.createConnection();
        TurnstileCommandDao commandDao = new TurnstileCommandDaoImpl(connection);
        TurnstileCommandService commandService = new TurnstileCommandService(commandDao);;

        HttpServer
                .newServer(1236)
                .start((req, resp) -> {
                    Map<String, List<String>> params = req.getQueryParameters();
                    String path = req.getDecodedPath().substring(1);
                    String resStr;
                    switch (path) {
                        case "enter": {
                            int userId = Integer.parseInt(params.get("user_id").get(0));
                            resStr = commandService.process(new EnterCommand(userId, LocalDateTime.now())).join();
                            break;
                        }
                        case "exit": {
                            int userId = Integer.parseInt(params.get("user_id").get(0));
                            resStr = commandService.process(new ExitCommand(userId, LocalDateTime.now())).join();
                            break;
                        }
                        default:
                            resStr = "unknown command";
                            break;
                    }
                    return resp.writeString(Observable.just(resStr));
                })
                .awaitShutdown();
    }
}