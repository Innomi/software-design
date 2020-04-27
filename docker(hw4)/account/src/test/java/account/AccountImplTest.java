package account;

import client.HttpMarketClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountImplTest {
    @ClassRule
    public static GenericContainer marketContainer = new FixedHostPortGenericContainer("market:1.0-SNAPSHOT")
            .withFixedExposedPort(3131, 8080).withExposedPorts(8080);

    private Account account;

    private final static String COMPANY_NAME = "toyota";
    private final static int COMPANY_INSTRUMENT_PRICE = 999;
    private final static int USER_ID = 0;
    private final static int USER_INITIAL_MONEY = 9999;

    @Before
    public void setUp() throws Exception {
        marketContainer.start();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/add_company?name=" + COMPANY_NAME + "&instrument_count=999&instrument_price=" + COMPANY_INSTRUMENT_PRICE))
                .GET().build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        account = new AccountImpl(new HttpMarketClient());
        account.registerUser(USER_ID);
        account.addMoney(USER_ID, USER_INITIAL_MONEY);
    }

    @After
    public void tearDown() {
        marketContainer.stop();
    }

    @Test
    public void testAddGetMoney() {
        double moneyAdded = 999.;
        account.addMoney(USER_ID, moneyAdded);
        assertThat(account.getMoney(USER_ID)).isEqualTo(USER_INITIAL_MONEY + moneyAdded);
    }

    @Test
    public void testBuyInstrument() {
        assertThat(account.buyInstrument(USER_ID, COMPANY_NAME, 9).toBlocking().single())
                .isEqualTo("SUCCESS");
    }

    @Test
    public void testBuyOverInstrument() {
        assertThat(account.buyInstrument(USER_ID, COMPANY_NAME, 99999).toBlocking().single())
                .isEqualTo("Not enough instrument on market");
    }

    @Test
    public void testSellInstrument() {
        assertThat(account.sellInstrument(USER_ID, COMPANY_NAME, 9).toBlocking().single())
                .isEqualTo("SUCCESS");
    }

    @Test
    public void testNotExistingCompany() {
        assertThat(account.buyInstrument(USER_ID, "google_kappa", 9).toBlocking().single())
                .isEqualTo("Instrument of company 'google_kappa' isn't registered");
    }

    @Test
    public void testNotEnoughGold() {
        assertThat(account.buyInstrument(USER_ID, COMPANY_NAME, 99999999).toBlocking().single())
                .isEqualTo("Not enough gold");
    }
}
