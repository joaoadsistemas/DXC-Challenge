package com.customer.system.application.service;

import com.customer.system.domain.exception.CustomerNotFoundException;
import com.customer.system.domain.exception.ScoreServiceException;
import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.model.vo.Cpf;
import com.customer.system.domain.model.vo.Email;
import com.customer.system.domain.port.out.CustomerRepository;
import com.customer.system.domain.port.out.ScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCustomerScoreServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ScoreService scoreService;

    @InjectMocks
    private FindCustomerScoreService findCustomerScoreService;

    @Test
    void shouldReturnScoreForExistingCustomer() {
        var customer = new Customer("João", new Cpf("123.456.789-09"), new Email("joao@email.com"), CustomerStatus.ACTIVE);
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(scoreService.findScoreByCpf(customer.getCpf()))
                .thenReturn(new ScoreService.ScoreResult("12345678909", 750, "LOW_RISK"));

        var result = findCustomerScoreService.findScoreByCustomerId(1L);

        assertEquals("12345678909", result.cpf());
        assertEquals(750, result.score());
        assertEquals("LOW_RISK", result.classification());
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> findCustomerScoreService.findScoreByCustomerId(1L));

        verify(scoreService, never()).findScoreByCpf(any());
    }

    @Test
    void shouldThrowScoreServiceExceptionWhenCommunicationFails() {
        var customer = new Customer("João", new Cpf("123.456.789-09"), new Email("joao@email.com"), CustomerStatus.ACTIVE);
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(scoreService.findScoreByCpf(customer.getCpf()))
                .thenThrow(new ScoreServiceException("Unavailable"));

        assertThrows(ScoreServiceException.class,
                () -> findCustomerScoreService.findScoreByCustomerId(1L));
    }
}
