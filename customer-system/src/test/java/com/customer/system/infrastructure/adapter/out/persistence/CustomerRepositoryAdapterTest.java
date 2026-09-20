package com.customer.system.infrastructure.adapter.out.persistence;

import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.model.vo.Cpf;
import com.customer.system.domain.model.vo.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@ActiveProfiles("test")
@Transactional
class CustomerRepositoryAdapterTest {

    @Autowired
    private CustomerRepositoryAdapter repository;

    @Test
    void shouldSaveAndFindCustomer() {
        var customer = new Customer("Maria", new Cpf("529.982.247-25"),
                new Email("maria@email.com"), CustomerStatus.ACTIVE);

        var saved = repository.save(customer);
        assertNotNull(saved.getId());

        var found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Maria", found.get().getName());
        assertEquals(new Cpf("529.982.247-25"), found.get().getCpf());
    }

    @Test
    void shouldFindByNameUsingNativeQuery() {
        var customer = new Customer("Carlos Silva", new Cpf("529.982.247-25"),
                new Email("carlos@email.com"), CustomerStatus.ACTIVE);
        repository.save(customer);

        var found = repository.findByNameContaining("carlos");

        assertEquals(1, found.size());
        assertEquals("Carlos Silva", found.get(0).getName());
    }

    @Test
    void shouldFindByStatusUsingNativeQuery() {
        var active = new Customer("Active User", new Cpf("529.982.247-25"),
                new Email("active@email.com"), CustomerStatus.ACTIVE);
        var inactive = new Customer("Inactive User", new Cpf("987.654.321-00"),
                new Email("inactive@email.com"), CustomerStatus.INACTIVE);
        repository.save(active);
        repository.save(inactive);

        var found = repository.findByStatus(CustomerStatus.ACTIVE);

        assertEquals(1, found.size());
        assertEquals("Active User", found.get(0).getName());
    }
}
