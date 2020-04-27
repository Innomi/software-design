package reporter.command;

import org.joda.time.LocalDateTime;

public class AddVisitCommand implements ReporterCommand {
    public AddVisitCommand (int userId, LocalDateTime startTime, LocalDateTime endTime, int eventId) {
        this.endTime = endTime;
        this.eventId = eventId;
        this.startTime = startTime;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    private int userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int eventId;
}
