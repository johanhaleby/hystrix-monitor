package se.haleby.hystrixmonitor;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.jayway.awaitility.Awaitility.await;
import static java.nio.charset.StandardCharsets.UTF_8;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bootstrap.class)
public class HystrixMonitorTest {

    @Autowired
    private AlarmSystem alarmSystem;

    static MockWebServer mockWebServer;

    @BeforeClass public static void
    given_mock_web_server_is_started() throws IOException {
        mockWebServer = new MockWebServer();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("hystrix_circuit_breaker_opened.txt");
        String hystrixEvent = new Scanner(resourceAsStream, UTF_8.name()).useDelimiter("\\A").next();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(hystrixEvent));
        mockWebServer.play(6543);
    }

    @AfterClass public static void
    mock_web_server_is_shutdown_after_tests_are_run() throws IOException {
        mockWebServer.shutdown();
    }


    @Test public void
    alarm_is_triggered_when_circuit_breaker_switches_from_closed_to_opened()  {
        await().until(alarmSystem::isCircuitBreakerOpened);
    }
}

