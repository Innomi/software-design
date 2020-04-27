package manager;

import org.joda.time.LocalDateTime;

import java.util.concurrent.CompletableFuture;

public interface ManagerCommandDao {
    CompletableFuture<Integer> registerUser(String name);
    CompletableFuture<Void> updateSubscription(int userId, LocalDateTime endTime);
}
