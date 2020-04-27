package turnstile;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import javafx.util.Pair;
import kotlin.jvm.functions.Function1;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sql.SqlQuery;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class TurnstileCommandDaoImplTest {
    @Mock
    private Connection connection;

    private TurnstileCommandDao commandDao;
    private LocalDateTime now;

    @Before
    public void setUp() {
        now = LocalDateTime.now();

        MockitoAnnotations.initMocks(this);
        QueryResult res = mock(QueryResult.class);
        ResultSet set = mock(ResultSet.class);
        RowData data = mock(RowData.class);

        when(res.getRows()).thenReturn(set);
        when(set.isEmpty()).thenReturn(false);
        when(set.get(0)).thenReturn(data);
        when(data.getString("name")).thenReturn("Li");
        when(data.getAs("end_time")).thenReturn(now.plusMinutes(10));
        when(data.getInt("event_id")).thenReturn(0);

        when(connection.sendPreparedStatement(eq(SqlQuery.getUser.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        when(connection.sendPreparedStatement(eq(SqlQuery.addEvent.getCode()), any()))
                .thenReturn(null);

        when(connection.inTransaction(any())).then(a -> {
            Function1<Connection, Object> callback = a.getArgument(0);
            return callback.invoke(connection);
        });
        commandDao = new TurnstileCommandDaoImpl(connection);
    }

    private QueryResult create_mock_event(String name, int eventId, TurnstileEventType eventType, LocalDateTime eventTime) {
        QueryResult res = mock(QueryResult.class);
        ResultSet set = mock(ResultSet.class);
        RowData data = mock(RowData.class);

        when(res.getRows()).thenReturn(set);
        when(set.isEmpty()).thenReturn(false);
        when(set.get(eq(0))).thenReturn(data);
        when(data.getString(eq("name"))).thenReturn(name);
        when(data.getInt(eq("event_id"))).thenReturn(eventId);
        when(data.getString(eq("event_type"))).thenReturn(String.valueOf(eventType));
        when(data.getAs(eq("event_time"))).thenReturn(eventTime);
        return res;
    }

    private QueryResult create_empty_query_mock() {
        QueryResult res = mock(QueryResult.class);
        ResultSet set = mock(ResultSet.class);
        when(res.getRows()).thenReturn(set);
        when(set.isEmpty()).thenReturn(true);
        return res;
    }

    @Test
    public void testEnter() {
        when(connection.sendPreparedStatement(eq(SqlQuery.getEvents.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(this::create_empty_query_mock));

        assertThat(commandDao.processEnter(0, LocalDateTime.now()).join())
                .isNull();
    }

    @Test
    public void testExit() {
        when(connection.sendPreparedStatement(eq(SqlQuery.getEvents.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() ->
                        create_mock_event("Li", 0, TurnstileEventType.ENTER, now.minusMinutes(10))));

        assertThat(commandDao.processExit(0, now).join())
                .isEqualTo(new Pair<LocalDateTime, Integer>(now.minusMinutes(10), 1));
    }

    @Test
    public void testDoubleEnter() {
        when(connection.sendPreparedStatement(eq(SqlQuery.getEvents.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() ->
                    create_mock_event("Li", 0, TurnstileEventType.ENTER, now.minusMinutes(1))));

        String msg = "";
        try {
            commandDao.processEnter(0, now.plusMinutes(1)).get();
        } catch (Exception e) {
            msg = e.getMessage();
        }
        assertThat(msg).isEqualTo("java.lang.IllegalArgumentException: Prev event cannot be ENTER for user_id = 0");
    }

    @Test
    public void testExitWithoutEnter() {
        when(connection.sendPreparedStatement(eq(SqlQuery.getEvents.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(this::create_empty_query_mock));

        String msg = "";
        try {
            commandDao.processExit(0, now).get();
        } catch (Exception e) {
            msg = e.getMessage();
        }
        assertThat(msg).isEqualTo("java.lang.IllegalArgumentException: Prev event for user_id = 0 not found");
    }

    @Test
    public void testExitBeforeEnter() {
        when(connection.sendPreparedStatement(eq(SqlQuery.getEvents.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() ->
                        create_mock_event("Li", 0, TurnstileEventType.ENTER, now.plusMinutes(2))));

        String msg = "";
        try {
            commandDao.processExit(0, now).get();
        } catch (Exception e) {
            msg = e.getMessage();
        }
        assertThat(msg).isEqualTo("java.lang.IllegalArgumentException: Last event is after given for user_id = 0");
    }

}
