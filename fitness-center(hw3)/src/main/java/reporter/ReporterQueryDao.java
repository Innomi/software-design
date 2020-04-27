package reporter;

import model.UserReport;

import java.util.Optional;

public interface ReporterQueryDao {
    Optional<UserReport> getUserReport(int userId);
}