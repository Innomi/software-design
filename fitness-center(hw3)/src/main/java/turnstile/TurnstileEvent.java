package turnstile;

import org.joda.time.LocalDateTime;

public class TurnstileEvent {
    public TurnstileEvent(TurnstileEventType type, LocalDateTime time) {
        this.time = time;
        this.type = type;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public TurnstileEventType getType() {
        return type;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setType(TurnstileEventType type) {
        this.type = type;
    }

    private TurnstileEventType type;
    private LocalDateTime time;
}