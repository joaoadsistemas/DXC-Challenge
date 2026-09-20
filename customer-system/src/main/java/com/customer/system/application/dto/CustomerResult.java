package com.customer.system.application.dto;

public record CustomerResult(
        Long id,
        String name,
        String cpf,
        String email,
        String status
) {
}
