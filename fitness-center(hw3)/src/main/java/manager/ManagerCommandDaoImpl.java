package manager;

import com.github.jasync.sql.db.Connection;
import database.DaoAbstract;
import javafx.util.Pair;
import model.User;
import org.joda.time.LocalDateTime;
import sql.SqlQuery;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ManagerCommandDaoImpl extends DaoAbstract implements ManagerCommandDao {
    public ManagerCommandDaoImpl(Connection connection) {
        this.connection = connection;
        this.maxIdInfoRef = new AtomicReference<>(new MaxIdInfo(-1, -1));
    }

    private class MaxIdInfo {
        MaxIdInfo(int maxUsedId, int maxId) {
            this.maxId = maxId;
            this.maxUsedId = maxUsedId;
        }

        private int maxUsedId;
        private int maxId;
    }

    @Override
    public CompletableFuture<Integer> registerUser(String name) {
        return connection.inTransaction((Connection transaction) -> CompletableFuture.supplyAsync(() -> {
            int newId = getId(transaction).join();
            transaction.sendPreparedStatement(SqlQuery.addUser.getCode(), Arrays.asList(newId, name)).join();
            return newId;
        }));
    }

    @Override
    public CompletableFuture<Void> updateSubscription(int userId, LocalDateTime endTime) {
        return connection.inTransaction((Connection transaction) -> CompletableFuture.supplyAsync(() -> {
            Optional<Pair<User, Integer>> subInfo = getUser(transaction, userId).join();

            if (subInfo.isPresent()) {
                int newEventId = 0;
                if (subInfo.get().getValue() != null) {
                    newEventId = subInfo.get().getValue() + 1;
                }
                transaction.sendPreparedStatement(SqlQuery.addSubscription.getCode(), Arrays.asList(userId, newEventId, endTime)).join();
            }
            return null;
        }));
    }

    private CompletableFuture<Integer> getId(Connection transaction) {
        return CompletableFuture.supplyAsync(() -> {
            while (true) {
                MaxIdInfo maxIdInfo = maxIdInfoRef.get();
                if (maxIdInfo.maxUsedId == maxIdInfo.maxId) {
                    int curMaxUsedId;
                    if (maxIdInfo.maxUsedId == -1) {
                        curMaxUsedId = transaction.sendQuery(SqlQuery.getMaxId.getCode()).join().getRows().get(0).getInt("max_id");
                    } else {
                        curMaxUsedId = maxIdInfo.maxId;
                    }
                    int nextMaxId = curMaxUsedId + 10;
                    transaction.sendPreparedStatement(SqlQuery.updateMaxId.getCode(), Arrays.asList(nextMaxId, curMaxUsedId)).join();
                    return curMaxUsedId + 1;
                } else {
                    int resultId = maxIdInfo.maxUsedId + 1;
                    if (maxIdInfoRef.compareAndSet(maxIdInfo, new MaxIdInfo(resultId, maxIdInfo.maxId))) {
                        return resultId;
                    }
                }
            }
        });
    }

    private Connection connection;
    private AtomicReference<MaxIdInfo> maxIdInfoRef;
}
