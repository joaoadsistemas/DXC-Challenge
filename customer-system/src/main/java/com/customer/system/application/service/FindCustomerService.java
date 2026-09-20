package com.customer.system.application.service;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.application.port.in.FindCustomerUseCase;
import com.customer.system.domain.model.Customer;
import com.customer.system.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FindCustomerService implements FindCustomerUseCase {

    private final CustomerRepository customerRepository;

    public FindCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<CustomerResult> findById(Long id) {
        return customerRepository.findById(id).map(this::toResult);
    }

    private CustomerResult toResult(Customer customer) {
        return new CustomerResult(
                customer.getId(),
                customer.getName(),
                customer.getCpf().formatted(),
                customer.getEmail().value(),
                customer.getStatus().name()
        );
    }
}
