package manager.query;

public class GetUserQuery implements ManagerQuery {
    public GetUserQuery(int userId) {
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
