package model;

import org.joda.time.Period;

public class UserReport {
    public UserReport(int totalVisits, Period totalTimeSpent) {
        this.totalVisits = totalVisits;
        this.totalTimeSpent = totalTimeSpent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserReport) {
            return totalVisits == ((UserReport) obj).getTotalVisits() &&
                    totalTimeSpent.normalizedStandard().equals(((UserReport) obj).getTotalTimeSpent().normalizedStandard());
        }
        return super.equals(obj);
    }

    public void setTotalTimeSpent(Period totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public void setTotalVisits(int totalVisits) {
        this.totalVisits = totalVisits;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    public Period getTotalTimeSpent() {
        return totalTimeSpent;
    }

    private int totalVisits;
    private Period totalTimeSpent;
}