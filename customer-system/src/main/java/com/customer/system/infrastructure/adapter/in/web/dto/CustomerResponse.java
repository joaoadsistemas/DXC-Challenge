package com.customer.system.infrastructure.adapter.in.web.dto;

public record CustomerResponse(
        Long id,
        String name,
        String cpf,
        String email,
        String status
) {
}
