package ewm.client;

import ewm.model.RequestHitDto;
import ewm.model.RequestHitInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatClient {
    private final WebClient.Builder webClientBuilder;
    private final String statServerBaseUrl;

    public StatClient(WebClient.Builder webClientBuilder, @Value("${stat.server.base.url}") String statServerBaseUrl) {
        this.webClientBuilder = webClientBuilder;
        this.statServerBaseUrl = statServerBaseUrl;
    }

    public ResponseEntity<List<RequestHitInfoDto>> getRequestHitInfoDto(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder uri = new StringBuilder(statServerBaseUrl + "/stats?start=" + start + "&end=" + end + (unique != null ? unique : ""));
        if (uris != null) uris.forEach(element -> uri.append("&uris=").append(element));
        return webClientBuilder.build()
                .get()
                .uri(uri.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<List<RequestHitInfoDto>>>() {
                })
                .block();
    }

    public ResponseEntity<RequestHitDto> hitRequest(RequestHitDto requestHitDto) {
        return webClientBuilder.build()
                .post()
                .uri(statServerBaseUrl + "/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestHitDto))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<RequestHitDto>>() {
                })
                .block();
    }
}
