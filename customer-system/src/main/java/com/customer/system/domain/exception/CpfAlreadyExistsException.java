package com.customer.system.domain.exception;

public class CpfAlreadyExistsException extends DomainException {

    public CpfAlreadyExistsException(String cpf) {
        super("CPF already registered: " + cpf);
    }
}
