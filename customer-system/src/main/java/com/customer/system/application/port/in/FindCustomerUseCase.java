package com.customer.system.application.port.in;

import com.customer.system.application.dto.CustomerResult;

import java.util.Optional;

public interface FindCustomerUseCase {

    Optional<CustomerResult> findById(Long id);
}
