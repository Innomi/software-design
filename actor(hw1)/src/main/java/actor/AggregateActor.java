package actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import client.SearchClient;
import model.SearchEngine;
import model.SearchRequest;
import model.SearchResult;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AggregateActor extends UntypedActor {
    public AggregateActor(SearchClient searchClient) {
        this.response = new SearchResult();
        this.searchClient = searchClient;
        this.resultsGotten = 0;
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof SearchResult) {
            SearchResult searchResult = (SearchResult) o;
            response.merge(searchResult);
            resultsGotten += searchResult.getSearchResults().size();

            if (SEARCH_ENGINES.size() == resultsGotten) {
                requestSender.tell(response, self());
                getContext().stop(self());
            }
        } else if (o instanceof String) {
            String query = (String) o;
            requestSender = getSender();

            for (SearchEngine searchEngine : SEARCH_ENGINES) {
                getContext().actorOf(Props.create(SearchActor.class, searchClient)).tell(new SearchRequest(searchEngine, query), self());
            }

            getContext().setReceiveTimeout(Duration.create(1, TimeUnit.SECONDS));
        } else if (o instanceof ReceiveTimeout) {
            requestSender.tell(response, self());
            getContext().stop(self());
        }
    }

    private static final Set<SearchEngine> SEARCH_ENGINES = EnumSet.allOf(SearchEngine.class);

    private int resultsGotten;
    private final SearchClient searchClient;
    private ActorRef requestSender;
    private final SearchResult response;
}
