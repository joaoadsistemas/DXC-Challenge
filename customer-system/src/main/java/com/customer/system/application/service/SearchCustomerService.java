package com.customer.system.application.service;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.application.port.in.SearchCustomerUseCase;
import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchCustomerService implements SearchCustomerUseCase {

    private final CustomerRepository customerRepository;

    public SearchCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResult> search(Query query) {
        List<Customer> customers;
        if (query.name() != null && !query.name().isBlank()) {
            customers = customerRepository.findByNameContaining(query.name());
        } else if (query.status() != null && !query.status().isBlank()) {
            customers = customerRepository.findByStatus(CustomerStatus.valueOf(query.status()));
        } else {
            customers = customerRepository.findAll();
        }
        return customers.stream().map(this::toResult).toList();
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
