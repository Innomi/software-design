package manager;

import com.github.jasync.sql.db.Connection;
import database.DaoConnection;
import io.reactivex.netty.protocol.http.server.HttpServer;
import manager.command.RegisterUserCommand;
import manager.command.UpdateSubscriptionCommand;
import manager.query.GetUserQuery;
import org.joda.time.LocalDateTime;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Connection connection = DaoConnection.createConnection();
        ManagerCommandDao commandDao = new ManagerCommandDaoImpl(connection);
        ManagerCommandService commandService = new ManagerCommandService(commandDao);
        ManagerQueryDao queryDao = new ManagerQueryDaoImpl(connection);
        ManagerQueryService queryService = new ManagerQueryService(queryDao);

        HttpServer
                .newServer(1234)
                .start((req, resp) -> {
                    Map<String, List<String>> params = req.getQueryParameters();
                    String path = req.getDecodedPath().substring(1);
                    String resStr;
                    switch (path) {
                        case "register":
                            String name = params.get("name").get(0);
                            resStr = commandService.process(new RegisterUserCommand(name)).join();
                            break;
                        case "update": {
                            int userId = Integer.parseInt(params.get("user_id").get(0));
                            LocalDateTime endTime = LocalDateTime.parse(params.get("end_time").get(0));
                            resStr = commandService.process(new UpdateSubscriptionCommand(userId, endTime)).join();
                            break;
                        }
                        case "get_user": {
                            int userId = Integer.parseInt(params.get("user_id").get(0));
                            resStr = queryService.process(new GetUserQuery(userId)).join();
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