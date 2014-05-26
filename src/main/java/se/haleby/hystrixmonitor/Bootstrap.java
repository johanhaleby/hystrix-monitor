package se.haleby.hystrixmonitor;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("se.haleby.hystrixmonitor")
public class Bootstrap {

    @Bean(destroyMethod = "close")
    public CloseableHttpAsyncClient closeableHttpAsyncClient() {
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        httpClient.start();
        return httpClient;
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Bootstrap.class, args);
    }
}
