package reporter;

import org.joda.time.LocalDateTime;

public class ReporterCommandDaoImpl implements ReporterCommandDao {
    public ReporterCommandDaoImpl(ReportStorage storage) {
        this.storage = storage;
    }

    @Override
    public void addVisit(int userId, LocalDateTime startTime, LocalDateTime endTime, int eventId) {
        storage.addVisit(userId, startTime, endTime, eventId);
    }

    private ReportStorage storage;
}