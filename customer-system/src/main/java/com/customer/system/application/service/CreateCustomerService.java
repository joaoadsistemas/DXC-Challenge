package com.customer.system.application.service;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.application.port.in.CreateCustomerUseCase;
import com.customer.system.domain.exception.CpfAlreadyExistsException;
import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.model.vo.Cpf;
import com.customer.system.domain.model.vo.Email;
import com.customer.system.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResult create(Command command) {
        var cpf = new Cpf(command.cpf());
        if (customerRepository.existsByCpf(cpf.value())) {
            throw new CpfAlreadyExistsException(cpf.value());
        }
        var email = new Email(command.email());
        var status = command.status() != null ? command.status() : CustomerStatus.ACTIVE;
        var customer = new Customer(command.name(), cpf, email, status);
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
