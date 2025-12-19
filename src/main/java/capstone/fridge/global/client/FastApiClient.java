package capstone.fridge.global.client;

import capstone.fridge.global.client.dto.FastApiPlaceDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
public class FastApiClient {

    private final RestClient restClient;

    public FastApiClient(@Value("${app.fastapi.base-url}") String baseUrl) {

        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();

        // ✅ 여기: FastAPI로 나가는 요청을 콘솔에 찍는 인터셉터
        ClientHttpRequestInterceptor logInterceptor = (request, body, execution) -> {
            System.out.println("[FASTAPI OUT] " + request.getMethod() + " " + request.getURI());
            System.out.println("[FASTAPI OUT] headers=" + request.getHeaders());
            System.out.println("[FASTAPI OUT] body=" + new String(body, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        };

        // ✅ 여기: RestClient 만들 때 .requestInterceptor(...)로 붙임
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)                // ex) http://localhost:8001
                .requestFactory(rf)              // HTTP/1.1 강제
                .requestInterceptor(logInterceptor)
                .build();
    }

    public FastApiPlaceDtos.PlaceRes place(FastApiPlaceDtos.PlaceReq req) {
        return restClient.post()
                .uri("/place")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(FastApiPlaceDtos.PlaceRes.class);
    }
}
