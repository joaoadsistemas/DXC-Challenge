package com.customer.system.application.port.in;

public interface FindCustomerScoreUseCase {

    ScoreResult findScoreByCustomerId(Long customerId);

    record ScoreResult(String cpf, Integer score, String classification) {
    }
}
