package manager;

import com.github.jasync.sql.db.Connection;
import database.DaoAbstract;
import javafx.util.Pair;
import model.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ManagerQueryDaoImpl extends DaoAbstract implements ManagerQueryDao {
    public ManagerQueryDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CompletableFuture<Optional<User>> getUser(int userId) {
        return connection.inTransaction((Connection transaction) -> CompletableFuture.supplyAsync(() -> {
            Optional<Pair<User, Integer>> info = getUser(transaction, userId).join();
            if (info.isPresent()) {
                return Optional.of(info.get().getKey());
            } else {
                return Optional.empty();
            }
        } ));
    }

    private Connection connection;
}