package turnstile;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ResultSet;
import database.DaoAbstract;
import javafx.util.Pair;
import model.User;
import org.joda.time.LocalDateTime;
import sql.SqlQuery;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TurnstileCommandDaoImpl extends DaoAbstract implements TurnstileCommandDao {
    public TurnstileCommandDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CompletableFuture<Void> processEnter(int userId, LocalDateTime time) {
        return connection.inTransaction((Connection transaction) -> CompletableFuture.supplyAsync(() -> {
            Optional<Pair<User, Integer>> user = getUser(transaction, userId).join();
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User with user_id = " + userId + " not found");
            }
            if (user.get().getKey().getSubscriptionEnd() == null || user.get().getKey().getSubscriptionEnd().isBefore(time)) {
                throw new IllegalArgumentException("No subscription found for user_id = " + String.valueOf(userId));
            }
            Optional<Pair<TurnstileEvent, Integer>> prevEvent = getLastEvent(transaction, userId).join().getValue();
            if (prevEvent.isPresent() && prevEvent.get().getKey().getType() == TurnstileEventType.ENTER) {
                throw new IllegalArgumentException("Prev event cannot be ENTER for user_id = " + String.valueOf(userId));
            }
            int newEventId = 0;
            if (prevEvent.isPresent() && prevEvent.get().getValue() != null) {
                newEventId = prevEvent.get().getValue() + 1;
            }
            transaction.sendPreparedStatement(SqlQuery.addEvent.getCode(), Arrays.asList(userId, newEventId, TurnstileEventType.ENTER, time));
            return null;
        }));
    }
    
    @Override
    public CompletableFuture<Pair<LocalDateTime, Integer>> processExit(int userId, LocalDateTime time) {
        return connection.inTransaction((Connection transaction) -> CompletableFuture.supplyAsync(() -> {
            Optional<Pair<TurnstileEvent, Integer>> prevEvent = getLastEvent(transaction, userId).join().getValue();
            if (!prevEvent.isPresent()) {
                throw new IllegalArgumentException("Prev event for user_id = " + String.valueOf(userId) + " not found");
            }
            if (prevEvent.get().getKey().getType() != TurnstileEventType.ENTER) {
                throw new IllegalArgumentException("Prev event must be ENTER for user_id = " + String.valueOf(userId));
            }
            if (prevEvent.get().getKey().getTime().isAfter(time)) {
                throw new IllegalArgumentException("Last event is after given for user_id = " + String.valueOf(userId));
            }
            int newEventId = prevEvent.get().getValue() + 1;
            transaction.sendPreparedStatement(SqlQuery.addEvent.getCode(), Arrays.asList(userId, newEventId, TurnstileEventType.EXIT, time));
            return new Pair<>(prevEvent.get().getKey().getTime(), newEventId);
        }));
    }

    private CompletableFuture<Pair<Optional<String>, Optional<Pair<TurnstileEvent, Integer>>>> getLastEvent(
            Connection transaction,
            int userId
    ) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet rows = transaction.sendPreparedStatement(SqlQuery.getEvents.getCode(), Collections.singletonList(userId)).join().getRows();
            if (rows.isEmpty()) {
                return new Pair<>(Optional.empty(), Optional.empty());
            }
            String name = rows.get(0).getString("name");
            Integer id = rows.get(0).getInt("event_id");
            if (id == null) {
                return new Pair<>(Optional.of(name), Optional.empty());
            }
            TurnstileEventType type = TurnstileEventType.valueOf(rows.get(0).getString("event_type"));
            LocalDateTime time = rows.get(0).getAs("event_time");
            return new Pair<>(Optional.of(name), Optional.of(new Pair<>(new TurnstileEvent(type, time), id)));
        });
    }

    private Connection connection;
}
