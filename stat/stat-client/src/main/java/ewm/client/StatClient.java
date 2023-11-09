package ewm.client;

import ewm.model.RequestHitDto;
import ewm.model.RequestHitInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatClient {
    private final WebClient.Builder webClientBuilder;
    private final String statServerBaseUrl;

    @Autowired
    public StatClient(WebClient.Builder webClientBuilder, @Value("${stat.server.base.url}") String statServerBaseUrl) {
        this.webClientBuilder = webClientBuilder;
        this.statServerBaseUrl = statServerBaseUrl;
    }

    public List<RequestHitInfoDto> getRequestHitInfoDto(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder uri = new StringBuilder("/stats?start=" + start.format(dateTimeFormatter) +
                "&end=" + end.format(dateTimeFormatter) + (unique != null ? "&" + unique : ""));
        if (uris != null) uris.forEach(element -> uri.append("&uris=").append(element));
        return webClientBuilder.baseUrl(statServerBaseUrl).build().get()
                .uri(uri.toString())
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }

    public RequestHitDto hitRequest(RequestHitDto requestHitDto) {
        return webClientBuilder.baseUrl(statServerBaseUrl).build()
                .post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestHitDto))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RequestHitDto>() {
                })
                .block();
    }
}
