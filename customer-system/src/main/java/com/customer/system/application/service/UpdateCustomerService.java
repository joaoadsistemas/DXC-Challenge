package com.customer.system.application.service;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.application.port.in.UpdateCustomerUseCase;
import com.customer.system.domain.exception.CustomerNotFoundException;
import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.vo.Email;
import com.customer.system.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateCustomerService implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public UpdateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResult update(Long id, Command command) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.updateInformation(command.name(), new Email(command.email()), command.status());
        return toResult(customerRepository.save(customer));
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
