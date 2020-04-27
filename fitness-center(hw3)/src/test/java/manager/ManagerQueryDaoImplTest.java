package manager;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import kotlin.jvm.functions.Function1;
import model.User;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sql.SqlQuery;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManagerQueryDaoImplTest {
    @Mock
    private Connection connection;

    private ManagerQueryDao queryDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(connection.inTransaction(any())).then(a -> {
            Function1<Connection, Object> callback = a.getArgument(0);
            return callback.invoke(connection);
        });
        queryDao = new ManagerQueryDaoImpl(connection);
    }

    @Test
    public void testGetExistingUser() {
        int userId = 0;
        String name = "Li";
        LocalDateTime subEnd = LocalDateTime.now().plusMinutes(10);

        CommonMockFunctions.mock_get_user(connection, name, subEnd, 0);

        assertThat(queryDao.getUser(userId).join()).isEqualTo(Optional.of(new User(userId, name, subEnd)));
    }

    @Test
    public void testGetNotExistingUser() {
        QueryResult res = mock(QueryResult.class);
        ResultSet set = mock(ResultSet.class);

        when(connection.sendPreparedStatement(eq(SqlQuery.getUser.getCode()), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));
        when(res.getRows()).thenReturn(set);
        when(set.isEmpty()).thenReturn(true);

        assertThat(queryDao.getUser(0).join()).isEqualTo(Optional.empty());
    }
}
