package com.customer.system.infrastructure.adapter.in.web;

import com.customer.system.application.dto.CustomerResult;
import com.customer.system.application.port.in.*;
import com.customer.system.infrastructure.adapter.in.web.config.MockMvcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@Import(MockMvcTestConfig.class)
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    @MockitoBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @MockitoBean
    private FindCustomerUseCase findCustomerUseCase;

    @MockitoBean
    private SearchCustomerUseCase searchCustomerUseCase;

    @MockitoBean
    private FindCustomerScoreUseCase findCustomerScoreUseCase;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCustomerWithAdminRole() throws Exception {
        var customer = buildCustomerResult(1L);
        when(createCustomerUseCase.create(any(CreateCustomerUseCase.Command.class))).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "João da Silva",
                                  "cpf": "123.456.789-09",
                                  "email": "joao@email.com",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/customers/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));
    }

    @Test
    void shouldRejectCreateCustomerWithUserRole() throws Exception {
        mockMvc.perform(post("/customers")
                        .header("Authorization", basicAuth("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "João da Silva",
                                  "cpf": "123.456.789-09",
                                  "email": "joao@email.com",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAuthenticateWithBasicAuth() throws Exception {
        when(searchCustomerUseCase.search(any())).thenReturn(List.of());

        mockMvc.perform(get("/customers")
                        .header("Authorization", basicAuth("user", "user")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldFindCustomerByIdWithUserRole() throws Exception {
        when(findCustomerUseCase.findById(1L)).thenReturn(Optional.of(buildCustomerResult(1L)));

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João da Silva"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldSearchCustomersByName() throws Exception {
        when(searchCustomerUseCase.search(any())).thenReturn(List.of(buildCustomerResult(1L)));

        mockMvc.perform(get("/customers/search").param("name", "joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnCustomerScore() throws Exception {
        when(findCustomerScoreUseCase.findScoreByCustomerId(1L))
                .thenReturn(new FindCustomerScoreUseCase.ScoreResult("12345678909", 750, "LOW_RISK"));

        mockMvc.perform(get("/customers/1/score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(750))
                .andExpect(jsonPath("$.classification").value("LOW_RISK"));
    }

    private CustomerResult buildCustomerResult(Long id) {
        return new CustomerResult(id, "João da Silva", "123.456.789-09", "joao@email.com", "ACTIVE");
    }

    private String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
