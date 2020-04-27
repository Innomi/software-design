package turnstile;

import javafx.util.Pair;
import org.joda.time.LocalDateTime;

import java.util.concurrent.CompletableFuture;

public interface TurnstileCommandDao {
    CompletableFuture<Void> processEnter(int userId, LocalDateTime time);
    CompletableFuture<Pair<LocalDateTime, Integer>> processExit(int userId, LocalDateTime time);
}