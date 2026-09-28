package repository.api;

import java.net.http.HttpClient;
import java.time.Duration;

public class AbstractApiRepository {

    protected final String BASE_URL = "http://localhost:8080/api";

    protected final HttpClient httpClient;

    public AbstractApiRepository() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }
}
