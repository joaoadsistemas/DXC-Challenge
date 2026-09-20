package com.customer.system.application.service;

import com.customer.system.domain.exception.CustomerNotFoundException;
import com.customer.system.application.port.in.DeleteCustomerUseCase;
import com.customer.system.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteCustomerService implements DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    public DeleteCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void delete(Long id) {
        if (!customerRepository.findById(id).isPresent()) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }
}
