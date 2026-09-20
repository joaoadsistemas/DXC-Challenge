package com.customer.system.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCustomerDataRepository extends JpaRepository<JpaCustomerEntity, Long> {

    Optional<JpaCustomerEntity> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
