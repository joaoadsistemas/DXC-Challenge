package com.customer.system.infrastructure.adapter.out.persistence;

import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.model.vo.Cpf;
import com.customer.system.domain.model.vo.Email;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class JpaCustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    public static JpaCustomerEntity fromDomain(Customer customer) {
        var entity = new JpaCustomerEntity();
        entity.id = customer.getId();
        entity.name = customer.getName();
        entity.cpf = customer.getCpf().value();
        entity.email = customer.getEmail().value();
        entity.status = customer.getStatus();
        return entity;
    }

    public Customer toDomain() {
        var customer = new Customer(name, new Cpf(cpf), new Email(email), status);
        customer.setId(id);
        return customer;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }
}
