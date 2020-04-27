package reporter;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import model.UserReport;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sql.SqlQuery;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportStorageTest {
    @Mock
    private Connection connection;

    private ReportStorage storage;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        storage = new ReportStorage(connection);
    }

    private void mock_init_empty() {
        QueryResult res = mock(QueryResult.class);
        ResultSet rows = mock(ResultSet.class);
        Iterator<RowData> iterator = (Iterator<RowData>) mock(Iterator.class);

        when(iterator.hasNext()).thenReturn(false);
        when(rows.iterator()).thenReturn(iterator);
        when(res.getRows()).thenReturn(rows);

        when(connection.sendPreparedStatement(eq(SqlQuery.getUserReports.getCode())))
                .thenReturn(CompletableFuture.supplyAsync( () -> res ));
    }

    private void mock_init_not_empty() {
        QueryResult res = mock(QueryResult.class);
        ResultSet rows = mock(ResultSet.class);
        Iterator<RowData> iterator = (Iterator<RowData>) mock(Iterator.class);
        RowData row = mock(RowData.class);

        when(row.getInt("user_id")).thenReturn(0);
        when(row.getLong("total_visits")).thenReturn(10L);
        when(row.getAs("total_time")).thenReturn(Period.months(3));
        when(row.getInt("max_exit_id")).thenReturn(1);

        when(iterator.next()).thenReturn(row);
        when(iterator.hasNext()).thenReturn(true, false);
        when(rows.iterator()).thenReturn(iterator);
        when(res.getRows()).thenReturn(rows);

        when(connection.sendPreparedStatement(eq(SqlQuery.getUserReports.getCode())))
                .thenReturn(CompletableFuture.supplyAsync( () -> res ));
    }

    @Test
    public void testGetEmptyReport() {
        mock_init_empty();

        int userId = 0;

        storage.init().join();
        assertThat(storage.getUserReport(userId)).isEqualTo(Optional.empty());
    }

    @Test
    public void testGetReport() {
        mock_init_empty();

        int userId = 0;
        int eventId = 1;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(10);

        storage.init().join();
        storage.addVisit(userId, startTime, endTime, eventId);

        assertThat(storage.getUserReport(userId)).isEqualTo(Optional.of(new UserReport(1, Period.minutes(10))));
    }

    @Test
    public void testGetEmptyReportWithInit() {
        mock_init_not_empty();

        int userId = 1;

        storage.init().join();
        assertThat(storage.getUserReport(userId)).isEqualTo(Optional.empty());
    }

    @Test
    public void testGetReportWithInit() {
        mock_init_not_empty();

        int userId = 0;
        int eventId = 2;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(10);

        storage.init().join();
        storage.addVisit(userId, startTime, endTime, eventId);

        assertThat(storage.getUserReport(userId)).isEqualTo(Optional.of(new UserReport(11, Period.minutes(10).plusMonths(3))));
    }
}
