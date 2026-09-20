package com.customer.system.application.port.in;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.domain.model.CustomerStatus;

public interface UpdateCustomerUseCase {

    CustomerResult update(Long id, Command command);

    record Command(String name, String email, CustomerStatus status) {
    }
}
