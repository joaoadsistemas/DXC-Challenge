package com.customer.system.application.port.in;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.domain.model.CustomerStatus;

public interface CreateCustomerUseCase {

    CustomerResult create(Command command);

    record Command(String name, String cpf, String email, CustomerStatus status) {
    }
}
