package com.customer.system.application.port.in;

import com.customer.system.application.dto.CustomerResult;

import java.util.List;

public interface SearchCustomerUseCase {

    List<CustomerResult> search(Query query);

    record Query(String name, String status) {
    }
}
