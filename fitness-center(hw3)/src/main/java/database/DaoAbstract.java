package database;

import com.github.jasync.sql.db.Connection;

import com.github.jasync.sql.db.ResultSet;
import javafx.util.Pair;
import model.User;
import org.joda.time.LocalDateTime;
import sql.SqlQuery;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class DaoAbstract {
    protected CompletableFuture<Optional<Pair<User, Integer>>> getUser(Connection transaction, int userId) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet res = transaction.sendPreparedStatement(SqlQuery.getUser.getCode(), Collections.singletonList(userId)).join().getRows();
            if (res.isEmpty()) {
                return Optional.empty();
            } else {
                String name = res.get(0).getString("name");
                LocalDateTime subscribtionEnd = res.get(0).getAs("end_time");
                int eventId = res.get(0).getInt("event_id");
                return Optional.of(new Pair<>(new User(userId, name, subscribtionEnd), eventId));
            }
        });
    }
}