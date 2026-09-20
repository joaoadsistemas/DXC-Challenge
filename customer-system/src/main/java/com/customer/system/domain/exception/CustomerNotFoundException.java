package com.customer.system.domain.exception;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}
