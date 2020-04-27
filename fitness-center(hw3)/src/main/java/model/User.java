package model;

import org.joda.time.LocalDateTime;

public class User {
    public User(int userId, String name, LocalDateTime subscriptionEnd) {
        this.userId = userId;
        this.name = name;
        this.subscriptionEnd = subscriptionEnd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return userId == ((User) obj).getUserId() &&
                    name.equals(((User) obj).getName());
        }
        return super.equals(obj);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubscriptionEnd(LocalDateTime subscriptionEnd) {
        this.subscriptionEnd = subscriptionEnd;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getSubscriptionEnd() {
        return subscriptionEnd;
    }

    private int userId;
    private String name;
    private LocalDateTime subscriptionEnd;
}