package ru.practicum.explore_with_me.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore_with_me.hit.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public RequestClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        return post("/hit", hitDto);
    }


    public ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        StringBuilder urisBuilder = new StringBuilder();

        Map<String, Object> urlVariables = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urisBuilder.append("&uris=").append(uri);
            }
        }
        String uri = "/stats?start={start}&end={end}" + urisBuilder + "&unique={unique}";
        return get(uri, urlVariables);
    }

}
