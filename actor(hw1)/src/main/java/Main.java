import actor.AggregateActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import client.StubSearchClient;

import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("search");

        for (String query : args) {
            ActorRef aggregator = system.actorOf(Props.create(
                    AggregateActor.class,
                    new StubSearchClient(100)
            ));

            Object response = ask(aggregator, query, Timeout.apply(5, TimeUnit.SECONDS)).toCompletableFuture().join();
            System.out.println(response.toString());
        }

        system.terminate();
    }
}
