package se.haleby.hystrixmonitor;

import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.nio.client.HttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.apache.http.ObservableHttp;

import javax.annotation.PostConstruct;

@Service
public class HystrixMonitor {

    private static final Logger log = LoggerFactory.getLogger(HystrixMonitor.class);

    private final HttpAsyncClient httpAsyncClient;
    private final AlarmSystem alarmSystem;

    @Autowired
    public HystrixMonitor(HttpAsyncClient httpAsyncClient, AlarmSystem alarmSystem) {
        this.httpAsyncClient = httpAsyncClient;
        this.alarmSystem = alarmSystem;
    }

    @PostConstruct
    public void subscribeToHystrixStream() {
        ObservableHttp.createGet("http://localhost:6543/hystrix.stream", httpAsyncClient).toObservable().
                flatMap(response -> response.getContent().map(String::new)).
                doOnNext(System.out::println).
                filter(hystrixPackage -> hystrixPackage.startsWith("data:")).
                filter(data -> data.contains("isCircuitBreakerOpen")).
                map(data -> data.substring("data:".length())).
                map(data -> JsonPath.from(data).getBoolean("isCircuitBreakerOpen")).
                map(isCircuitBreakerCurrentlyOpened -> Pair.of(isCircuitBreakerCurrentlyOpened, alarmSystem.isCircuitBreakerOpened())).
                filter(pair -> pair.getLeft() != pair.getRight()).
                map(Pair::getLeft).
                doOnNext(isCircuitBreakerOpened -> {
                    if (isCircuitBreakerOpened) {
                        alarmSystem.reportCircuitBreakerOpened();
                    } else {
                        alarmSystem.reportCircuitBreakerClosed();
                    }
                }).
                doOnError(throwable -> log.error("Error", throwable)).
                subscribe();
    }

}
