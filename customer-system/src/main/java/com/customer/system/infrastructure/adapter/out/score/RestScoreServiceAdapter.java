package com.customer.system.infrastructure.adapter.out.score;

import com.customer.system.domain.exception.ScoreServiceException;
import com.customer.system.domain.model.vo.Cpf;
import com.customer.system.domain.port.out.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class RestScoreServiceAdapter implements ScoreService {

    private static final Logger log = LoggerFactory.getLogger(RestScoreServiceAdapter.class);

    private final RestClient restClient;

    public RestScoreServiceAdapter(RestClient scoreRestClient) {
        this.restClient = scoreRestClient;
    }

    @Override
    public ScoreResult findScoreByCpf(Cpf cpf) {
        try {
            var response = restClient.get()
                    .uri("/scores/{cpf}", cpf.value())
                    .retrieve()
                    .toEntity(Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("cpf") || !body.containsKey("score")) {
                throw new ScoreServiceException("Unexpected response from score service");
            }

            String responseCpf = String.valueOf(body.get("cpf"));
            Integer score = parseScore(body.get("score"));
            String classification = body.containsKey("classification") ? String.valueOf(body.get("classification")) : null;
            return new ScoreResult(responseCpf, score, classification);
        } catch (RestClientResponseException e) {
            log.warn("Score service returned HTTP error: {}", e.getStatusCode());
            throw new ScoreServiceException("Score service returned error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.warn("Score service unavailable: {}", e.getMessage());
            throw new ScoreServiceException("Score service unavailable", e);
        } catch (Exception e) {
            log.warn("Failed to communicate with score service: {}", e.getMessage());
            throw new ScoreServiceException("Failed to communicate with score service", e);
        }
    }

    private Integer parseScore(Object scoreValue) {
        if (scoreValue instanceof Number number) {
            return number.intValue();
        }
        throw new ScoreServiceException("Score value is not a number");
    }
}
