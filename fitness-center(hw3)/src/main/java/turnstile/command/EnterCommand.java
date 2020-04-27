package turnstile.command;

import org.joda.time.LocalDateTime;

public class EnterCommand implements TurnstileCommand {
    public EnterCommand(int userId, LocalDateTime time) {
        this.time = time;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    private int userId;
    private LocalDateTime time;
}
