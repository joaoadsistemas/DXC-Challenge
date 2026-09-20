package com.customer.system.domain.model.vo;

import java.util.Objects;

public final class Cpf {

    private final String value;

    public Cpf(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CPF is required");
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 11) {
            throw new IllegalArgumentException("CPF must contain 11 digits");
        }
        if (hasInvalidDigits(digits)) {
            throw new IllegalArgumentException("Invalid CPF");
        }
        this.value = digits;
    }

    private static boolean hasInvalidDigits(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            return true;
        }
        int[] weightsFirst = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weightsSecond = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        int firstDigit = calculateDigit(cpf, weightsFirst);
        int secondDigit = calculateDigit(cpf, weightsSecond);
        return firstDigit != Character.getNumericValue(cpf.charAt(9))
                || secondDigit != Character.getNumericValue(cpf.charAt(10));
    }

    private static int calculateDigit(String cpf, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    public String value() {
        return value;
    }

    public String formatted() {
        return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf cpf)) return false;
        return Objects.equals(value, cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
