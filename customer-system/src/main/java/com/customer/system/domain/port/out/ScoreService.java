package com.customer.system.domain.port.out;

import com.customer.system.domain.model.vo.Cpf;

public interface ScoreService {

    ScoreResult findScoreByCpf(Cpf cpf);

    record ScoreResult(String cpf, Integer score, String classification) {
    }
}
