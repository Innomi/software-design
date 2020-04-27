package manager.command;

import org.joda.time.LocalDateTime;

public class UpdateSubscriptionCommand implements ManagerCommand {
    public UpdateSubscriptionCommand(int userId, LocalDateTime endTime) {
        this.userId = userId;
        this.endTime = endTime;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private int userId;
    private LocalDateTime endTime;
}
