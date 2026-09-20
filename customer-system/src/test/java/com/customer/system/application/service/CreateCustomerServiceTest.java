package com.customer.system.application.service;

import com.customer.system.application.port.in.CreateCustomerUseCase;
import com.customer.system.domain.exception.CpfAlreadyExistsException;
import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.port.out.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreateCustomerService createCustomerService;

    @Test
    void shouldCreateCustomerWhenCpfIsNotRegistered() {
        var command = new CreateCustomerUseCase.Command(
                "João da Silva", "123.456.789-09", "joao@email.com", CustomerStatus.ACTIVE);

        when(customerRepository.existsByCpf("12345678909")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        var result = createCustomerService.create(command);

        assertNotNull(result.id());
        assertEquals("João da Silva", result.name());
        assertEquals("123.456.789-09", result.cpf());
        assertEquals("joao@email.com", result.email());
        assertEquals("ACTIVE", result.status());
    }

    @Test
    void shouldThrowWhenCpfAlreadyExists() {
        var command = new CreateCustomerUseCase.Command(
                "João da Silva", "123.456.789-09", "joao@email.com", CustomerStatus.ACTIVE);

        when(customerRepository.existsByCpf("12345678909")).thenReturn(true);

        assertThrows(CpfAlreadyExistsException.class, () -> createCustomerService.create(command));
        verify(customerRepository, never()).save(any());
    }
}
