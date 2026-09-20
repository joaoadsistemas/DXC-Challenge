package com.customer.system.infrastructure.adapter.in.web.dto;

import com.customer.system.domain.model.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be up to 120 characters")
        String name,

        @NotBlank(message = "CPF is required")
        @Size(min = 11, max = 14, message = "CPF must contain 11 digits")
        String cpf,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 120, message = "Email must be up to 120 characters")
        String email,

        @NotNull(message = "Status is required")
        CustomerStatus status
) {
}
