package manager;

import model.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ManagerQueryDao {
    CompletableFuture<Optional<User>> getUser(int userId);
}
