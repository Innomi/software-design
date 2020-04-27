package reporter;

import com.github.jasync.sql.db.Connection;
import database.DaoConnection;
import io.reactivex.netty.protocol.http.server.HttpServer;
import org.joda.time.LocalDateTime;
import reporter.command.AddVisitCommand;
import reporter.query.GetUserReportQuery;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Connection connection = DaoConnection.createConnection();
        ReportStorage reportStorage = new ReportStorage(connection);
        ReporterCommandDao commandDao = new ReporterCommandDaoImpl(reportStorage);
        ReporterCommandService commandService = new ReporterCommandService(commandDao);
        ReporterQueryDao queryDao = new ReporterQueryDaoImpl(reportStorage);
        ReporterQueryService queryService = new ReporterQueryService(queryDao);

        HttpServer
                .newServer(1235)
                .start((req, resp) -> {
                    Map<String, List<String>> params = req.getQueryParameters();
                    String path = req.getDecodedPath().substring(1);
                    String resStr;
                    switch (path) {
                        case "get_report": {
                            int userId = Integer.parseInt(params.get("user_id").get(0));
                            resStr = queryService.process(new GetUserReportQuery(userId));
                            break;
                        }
                        case "add_visit": {
                            int userId = Integer.parseInt(params.get("user_id").get(0));
                            int eventId = Integer.parseInt(params.get("event_id").get(0));
                            LocalDateTime startTime = LocalDateTime.parse(params.get("start_time").get(0));
                            LocalDateTime endTime = LocalDateTime.parse(params.get("end_time").get(0));
                            resStr = commandService.process(new AddVisitCommand(userId, startTime, endTime, eventId));
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
