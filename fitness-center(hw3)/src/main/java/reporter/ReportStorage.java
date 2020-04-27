package reporter;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import javafx.util.Pair;
import model.UserReport;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import sql.SqlQuery;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ReportStorage {
    public ReportStorage(Connection connection) {
        this.connection = connection;
        this.storage = new ConcurrentHashMap<>();
    }

    public CompletableFuture<Void> init() {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet rows = connection.sendPreparedStatement(SqlQuery.getUserReports.getCode()).join().getRows();
            for (RowData row : rows) {
                int userId = row.getInt("user_id");
                int totalVisits = row.getLong("total_visits").intValue();
                Period totalTime = row.getAs("total_time");
                int lastEventId = row.getInt("max_exit_id");
                storage.put(userId, new Pair<>(new UserReport(totalVisits, totalTime), lastEventId));
            }
            return null;
        });
    }

    public void addVisit(int userId, LocalDateTime startTime, LocalDateTime endTime, int eventId) {
        Period visitPeriod = Period.fieldDifference(startTime, endTime);
        storage.compute(userId, (Integer id, Pair<UserReport, Integer> data) -> {
            if (data == null) {
                return new Pair<>(new UserReport(1, visitPeriod), eventId);
            } else {
                UserReport report = data.getKey();
                Integer lastEventId = data.getValue();
                if (eventId <= lastEventId) {
                    return data;
                } else {
                    UserReport newReport = new UserReport(report.getTotalVisits() + 1, report.getTotalTimeSpent().plus(visitPeriod));
                    return new Pair<>(newReport, eventId);
                }
            }
        });
    }

    public Optional<UserReport> getUserReport(int userId) {
        if (storage.containsKey(userId)) {
            return Optional.of(storage.get(userId).getKey());
        } else {
            return Optional.empty();
        }
    }

    private Connection connection;
    private ConcurrentHashMap<Integer, Pair<UserReport, Integer>> storage;
}