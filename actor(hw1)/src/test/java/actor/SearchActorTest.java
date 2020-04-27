package actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import client.StubSearchClient;
import model.SearchEngine;
import model.SearchRequest;
import model.SearchResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchActorTest {
    private ActorSystem system;

    @Before
    public void setUp() {
        system = ActorSystem.create("ChildActorTest");
    }

    @After
    public void tearDown() {
        system.terminate();
    }

    @Test
    public void testSearchActor() {
        ActorRef childActor = system.actorOf(Props.create(SearchActor.class, new StubSearchClient(0)));

        SearchResult response = (SearchResult) ask(childActor, new SearchRequest(SearchEngine.GOOGLE, "test"), Timeout.apply(5, TimeUnit.SECONDS))
                .toCompletableFuture().join();

        assertThat(response.getSearchResults()).containsOnlyKeys(SearchEngine.GOOGLE);
        assertThat(response.getSearchResults().get(SearchEngine.GOOGLE)).isNotEmpty();
    }
}
