package sql;

public enum SqlQuery {
    addUser(String.join("\n"
            , "insert into users (user_id, name)"
            , "values (?, ?)")),

    addSubscription(String.join("\n"
            , "insert into subscription_events (event_id, user_id, end_time)"
            , "values (?, ?, ?)")),

    updateMaxId(String.join("\n"
            , "update max_ids"
            , "set max_id = ?"
            , "where entity = 'user'"
            , "and max_id = ?")),

    addEvent(String.join("\n"
            , "insert into events (event_id, user_id, event_type, event_time)"
            , "values (?, ?, ?, ?)")),

    getUser(String.join("\n"
            , "select *"
            , "from users left join subscription_events using (user_id)"
            , "where user_id = ?"
            , "order by event_id desc")),

    getMaxId(String.join("\n"
            , "select max_id"
            , "from max_ids"
            , "where entity = 'user'")),

    getEvents(String.join("\n"
            , "select *"
            , "from users left join events using (user_id)"
            , "where user_id = ?"
            , "order by event_id desc")),

    getUserReports(String.join("\n"
            , "with ranked_events as ("
            , "    select user_id, event_type, event_time, event_id, rank()"
            , "    over (partition by (user_id, event_type) order by event_id) as num"
            , "    from events"
            , "    ),"
            , "exits as ("
            , "    select user_id, num, event_time as exit_time, event_id as exit_id"
            , "    from ranked_events"
            , "    where event_type = 'EXIT'"
            , "),"
            , "enters as ("
            , "    select user_id, num, event_time as enter_time"
            , "    from ranked_events"
            , "    where event_type = 'ENTER'"
            , ")"
            , "select user_id, count(1) as total_visits, sum(exit_time - enter_time) as total_time, max(exit_id) as max_exit_id"
            , "from exits join enters using (user_id, num)"
            , "group by user_id"));

    private String code;

    SqlQuery(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
