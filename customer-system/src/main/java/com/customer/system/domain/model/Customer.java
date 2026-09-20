package com.customer.system.domain.model;

import com.customer.system.domain.model.vo.Cpf;
import com.customer.system.domain.model.vo.Email;

import java.util.Objects;

public class Customer {

    private Long id;
    private String name;
    private final Cpf cpf;
    private Email email;
    private CustomerStatus status;

    public Customer(String name, Cpf cpf, Email email, CustomerStatus status) {
        this.name = Objects.requireNonNull(name, "Name is required");
        this.cpf = Objects.requireNonNull(cpf, "CPF is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.status = Objects.requireNonNull(status, "Status is required");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void updateInformation(String name, Email email, CustomerStatus status) {
        this.name = Objects.requireNonNull(name, "Name is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.status = Objects.requireNonNull(status, "Status is required");
    }

    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }
}
