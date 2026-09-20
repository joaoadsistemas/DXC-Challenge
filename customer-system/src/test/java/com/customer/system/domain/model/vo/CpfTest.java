package com.customer.system.domain.model.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfTest {

    @Test
    void shouldCreateValidCpf() {
        var cpf = new Cpf("123.456.789-09");
        assertEquals("12345678909", cpf.value());
        assertEquals("123.456.789-09", cpf.formatted());
    }

    @Test
    void shouldCreateValidCpfWithOnlyDigits() {
        var cpf = new Cpf("12345678909");
        assertEquals("12345678909", cpf.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"111.111.111-11", "123.456.789-00", "000.000.000-00", "invalid", "123"})
    void shouldRejectInvalidCpf(String value) {
        assertThrows(IllegalArgumentException.class, () -> new Cpf(value));
    }

    @Test
    void shouldRejectNullCpf() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
    }

    @Test
    void shouldRejectBlankCpf() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf("  "));
    }
}
