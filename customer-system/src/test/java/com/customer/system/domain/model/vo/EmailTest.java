package com.customer.system.domain.model.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        var email = new Email("Joao@Email.COM");
        assertEquals("joao@email.com", email.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "@email.com", "joao@", "joao@.com"})
    void shouldRejectInvalidEmail(String value) {
        assertThrows(IllegalArgumentException.class, () -> new Email(value));
    }

    @Test
    void shouldRejectBlankEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("  "));
    }
}
