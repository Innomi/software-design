package reporter;

import model.UserReport;

import java.util.Optional;

public class ReporterQueryDaoImpl implements ReporterQueryDao {
    public ReporterQueryDaoImpl(ReportStorage storage) {
        this.storage = storage;
    }

    @Override
    public Optional<UserReport> getUserReport(int userId) {
        return storage.getUserReport(userId);
    }

    private ReportStorage storage;
}