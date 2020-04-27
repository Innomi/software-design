package manager;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import org.joda.time.LocalDateTime;
import sql.SqlQuery;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonMockFunctions {
    public static void mock_get_user(Connection connection, String name, LocalDateTime endTime, int eventId) {
        QueryResult res = mock(QueryResult.class);
        ResultSet set = mock(ResultSet.class);
        RowData data = mock(RowData.class);

        when(res.getRows()).thenReturn(set);
        when(set.isEmpty()).thenReturn(false);
        when(set.get(0)).thenReturn(data);
        when(data.getString("name")).thenReturn(name);
        when(data.getAs("end_time")).thenReturn(endTime);
        when(data.getInt("event_id")).thenReturn(eventId);

        when(connection.sendPreparedStatement(eq(SqlQuery.getUser.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));
    }
}
