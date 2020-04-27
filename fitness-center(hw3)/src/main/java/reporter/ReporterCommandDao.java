package reporter;

import org.joda.time.LocalDateTime;

public interface ReporterCommandDao {
    void addVisit(int userId, LocalDateTime startTime, LocalDateTime endTime, int eventId);
}