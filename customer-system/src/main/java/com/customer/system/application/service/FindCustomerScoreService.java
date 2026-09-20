package com.customer.system.application.service;

import com.customer.system.domain.exception.CustomerNotFoundException;
import com.customer.system.domain.exception.ScoreServiceException;
import com.customer.system.application.port.in.FindCustomerScoreUseCase;
import com.customer.system.domain.port.out.CustomerRepository;
import com.customer.system.domain.port.out.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FindCustomerScoreService implements FindCustomerScoreUseCase {

    private final CustomerRepository customerRepository;
    private final ScoreService scoreService;

    public FindCustomerScoreService(CustomerRepository customerRepository, ScoreService scoreService) {
        this.customerRepository = customerRepository;
        this.scoreService = scoreService;
    }

    @Override
    public ScoreResult findScoreByCustomerId(Long customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        try {
            var score = scoreService.findScoreByCpf(customer.getCpf());
            return new ScoreResult(score.cpf(), score.score(), score.classification());
        } catch (ScoreServiceException e) {
            throw new ScoreServiceException("Unable to retrieve score for customer " + customerId, e);
        }
    }
}
