package com.customer.system.domain.port.out;

import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByCpf(String cpf);

    List<Customer> findAll();

    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByNameContaining(String name);

    boolean existsByCpf(String cpf);

    void deleteById(Long id);
}
