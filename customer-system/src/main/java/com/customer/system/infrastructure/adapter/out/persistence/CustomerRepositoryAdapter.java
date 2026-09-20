package com.customer.system.infrastructure.adapter.out.persistence;

import com.customer.system.domain.model.Customer;
import com.customer.system.domain.model.CustomerStatus;
import com.customer.system.domain.port.out.CustomerRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final JpaCustomerDataRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public CustomerRepositoryAdapter(JpaCustomerDataRepository jpaRepository, JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer save(Customer customer) {
        var entity = JpaCustomerEntity.fromDomain(customer);
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findById(id).map(JpaCustomerEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf).map(JpaCustomerEntity::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll().stream()
                .map(JpaCustomerEntity::toDomain)
                .toList();
    }

    @Override
    public List<Customer> findByStatus(CustomerStatus status) {
        String sql = "SELECT id, name, cpf, email, status FROM customers WHERE status = ?";
        return jdbcTemplate.query(sql, new CustomerRowMapper(), status.name());
    }

    @Override
    public List<Customer> findByNameContaining(String name) {
        String sql = "SELECT id, name, cpf, email, status FROM customers WHERE LOWER(name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, new CustomerRowMapper(), "%" + name + "%");
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            var customer = new Customer(
                    rs.getString("name"),
                    new com.customer.system.domain.model.vo.Cpf(rs.getString("cpf")),
                    new com.customer.system.domain.model.vo.Email(rs.getString("email")),
                    CustomerStatus.valueOf(rs.getString("status"))
            );
            customer.setId(rs.getLong("id"));
            return customer;
        }
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
