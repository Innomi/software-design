package actor;

import akka.actor.UntypedActor;
import client.SearchClient;
import model.SearchRequest;

public class SearchActor extends UntypedActor {
    public SearchActor(SearchClient searchClient) {
        this.searchClient = searchClient;
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof SearchRequest) {
            SearchRequest searchRequest = (SearchRequest) o;
            getSender().tell(searchClient.query(searchRequest), self());
            getContext().stop(self());
        }
    }

    private final SearchClient searchClient;
}
