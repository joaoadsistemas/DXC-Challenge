package com.customer.system.infrastructure.adapter.in.web;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.application.port.in.*;
import com.customer.system.infrastructure.adapter.in.web.dto.CreateCustomerRequest;
import com.customer.system.infrastructure.adapter.in.web.dto.CustomerResponse;
import com.customer.system.infrastructure.adapter.in.web.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final FindCustomerUseCase findCustomerUseCase;
    private final SearchCustomerUseCase searchCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              DeleteCustomerUseCase deleteCustomerUseCase,
                              FindCustomerUseCase findCustomerUseCase,
                              SearchCustomerUseCase searchCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
        this.findCustomerUseCase = findCustomerUseCase;
        this.searchCustomerUseCase = searchCustomerUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> create(@RequestBody @Valid CreateCustomerRequest request) {
        var result = createCustomerUseCase.create(
                new CreateCustomerUseCase.Command(request.name(), request.cpf(), request.email(), request.status()));
        return ResponseEntity.created(URI.create("/customers/" + result.id()))
                .body(toResponse(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @RequestBody @Valid UpdateCustomerRequest request) {
        var result = updateCustomerUseCase.update(id,
                new UpdateCustomerUseCase.Command(request.name(), request.email(), request.status()));
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteCustomerUseCase.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        return findCustomerUseCase.findById(id)
                .map(result -> ResponseEntity.ok(toResponse(result)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<CustomerResponse> search(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String status) {
        var customers = searchCustomerUseCase.search(new SearchCustomerUseCase.Query(
                name != null && !name.isBlank() ? name : null,
                status));
        return customers.stream().map(this::toResponse).toList();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<CustomerResponse> searchByName(@RequestParam String name) {
        var customers = searchCustomerUseCase.search(new SearchCustomerUseCase.Query(name, null));
        return customers.stream().map(this::toResponse).toList();
    }

    private CustomerResponse toResponse(CustomerResult result) {
        return new CustomerResponse(
                result.id(),
                result.name(),
                result.cpf(),
                result.email(),
                result.status()
        );
    }
}
