package actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import client.StubSearchClient;
import model.SearchEngine;
import model.SearchResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static org.assertj.core.api.Assertions.assertThat;

public class AggregateActorTest {
    private ActorSystem system;

    @Before
    public void setUp() {
        system = ActorSystem.create("MasterActorTest");
    }

    @After
    public void tearDown() {
        system.terminate();
    }

    @Test
    public void testAggregateActor() {
        ActorRef aggregateActor = system.actorOf(Props.create(AggregateActor.class, new StubSearchClient(0)));

        SearchResult response = (SearchResult) ask(
                aggregateActor,
                "test",
                Timeout.apply(5, TimeUnit.SECONDS)
        ).toCompletableFuture().join();

        assertThat(response.getSearchResults().keySet()).isEqualTo(EnumSet.allOf(SearchEngine.class));
        assertThat(response.getSearchResults().get(SearchEngine.GOOGLE)).isNotEmpty();
        assertThat(response.getSearchResults().get(SearchEngine.YANDEX)).isNotEmpty();
        assertThat(response.getSearchResults().get(SearchEngine.BING)).isNotEmpty();
    }

    @Test
    public void testAggregateActorTimeout() {
        ActorRef masterActor = system.actorOf(Props.create(AggregateActor.class, new StubSearchClient(5000)));

        SearchResult response = (SearchResult) ask(masterActor, "query", Timeout.apply(2, TimeUnit.SECONDS))
                .toCompletableFuture().join();

        assertThat(response.getSearchResults().keySet()).isEmpty();
    }
}
