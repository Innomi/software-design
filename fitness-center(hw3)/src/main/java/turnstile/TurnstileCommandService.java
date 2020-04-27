package turnstile;

import io.reactivex.netty.protocol.http.client.HttpClient;
import javafx.util.Pair;
import org.joda.time.LocalDateTime;
import turnstile.command.EnterCommand;
import turnstile.command.ExitCommand;
import turnstile.command.TurnstileCommand;

import java.util.concurrent.CompletableFuture;

public class TurnstileCommandService {
    public TurnstileCommandService(TurnstileCommandDao commandDao) {
        this.commandDao = commandDao;
    }

    CompletableFuture<String> process(TurnstileCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (command instanceof EnterCommand) {
                    commandDao.processEnter(((EnterCommand) command).getUserId(), ((EnterCommand) command).getTime());
                    return "Successful enter";
                } else if (command instanceof ExitCommand) {
                    Pair<LocalDateTime, Integer> info = commandDao.processExit(((ExitCommand) command).getUserId(), ((ExitCommand) command).getTime()).join();
                    sendVisitInfo(((ExitCommand) command).getUserId(), info.getKey(), ((ExitCommand) command).getTime(), info.getValue());
                    return "Successful exit";
                }
                return "Unknown turnstile command";
            } catch (Exception e) {
                return "Error occurred while processing: " + e.getMessage();
            }
        });
    }

    CompletableFuture<Void> sendVisitInfo(int userId, LocalDateTime startTime, LocalDateTime endTime, int eventId) {
        return CompletableFuture.supplyAsync(() -> {
            String request = "/add_visit?" +
                    "user_id=" + String.valueOf(userId) +
                    "&start_time=" + startTime.toString("yyyy-MM-dd'T'HH:mm:ss") +
                    "&end_timr=" + endTime.toString("yyyy-MM-dd'T'HH:mm:ss") +
                    "&event_id=" + String.valueOf(eventId);
            HttpClient
                    .newClient("localhost", 1235)
                    .createGet(request)
                    .subscribe();
            return null;
        });
    }

    private TurnstileCommandDao commandDao;
}