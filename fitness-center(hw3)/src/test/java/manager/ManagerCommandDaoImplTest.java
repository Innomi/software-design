package manager;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManagerCommandDaoImplTest {
    @Mock
    private Connection connection;

    private ManagerCommandDao commandDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(connection.sendPreparedStatement(eq(SqlQuery.addSubscription.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> null));

        when(connection.sendPreparedStatement(eq(SqlQuery.addUser.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> null));

        when(connection.sendPreparedStatement(eq(SqlQuery.updateMaxId.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> null));

        when(connection.inTransaction(any())).then(a -> {
            Function1<Connection, Object> callback = a.getArgument(0);
            return callback.invoke(connection);
        });
        commandDao = new ManagerCommandDaoImpl(connection);
    }

    @Test
    public void testRegisterUser() {
        {
            QueryResult res = mock(QueryResult.class);
            ResultSet set = mock(ResultSet.class);
            RowData data = mock(RowData.class);

            when(res.getRows()).thenReturn(set);
            when(set.isEmpty()).thenReturn(false);
            when(set.get(0)).thenReturn(data);
            when(data.getInt("max_id")).thenReturn(0);

            when(connection.sendQuery(eq(SqlQuery.getMaxId.getCode())))
                    .thenReturn(CompletableFuture.supplyAsync(() -> res));
        }

        String name = "Li";
        assertThat(commandDao.registerUser(name).join())
                .isEqualTo(1);
    }

    @Test
    public void testUpdateSubscription() {
        int userId = 1;

        CommonMockFunctions.mock_get_user(connection, "Li", LocalDateTime.now().plusMinutes(10), 0);

        try {
            assertThat(commandDao.updateSubscription(userId, LocalDateTime.now().plusMinutes(20)).get())
                    .isNull();
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("");
        }
    }
}