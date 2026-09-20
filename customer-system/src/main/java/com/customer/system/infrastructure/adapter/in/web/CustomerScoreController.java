package com.customer.system.infrastructure.adapter.in.web;

import com.customer.system.application.port.in.FindCustomerScoreUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerScoreController {

    private final FindCustomerScoreUseCase findCustomerScoreUseCase;

    public CustomerScoreController(FindCustomerScoreUseCase findCustomerScoreUseCase) {
        this.findCustomerScoreUseCase = findCustomerScoreUseCase;
    }

    @GetMapping("/{id}/score")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public FindCustomerScoreUseCase.ScoreResult getScore(@PathVariable Long id) {
        return findCustomerScoreUseCase.findScoreByCustomerId(id);
    }
}
