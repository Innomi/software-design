package reporter.query;

public class GetUserReportQuery implements ReporterQuery {
    public GetUserReportQuery(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int userId;
}
