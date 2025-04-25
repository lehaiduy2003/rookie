package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.repository.BaseRepository;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.service.CustomerService;
import com.example.assignment.specification.CustomerSpecification;
import com.example.assignment.util.SpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
@Logging
public class CustomerServiceImpl extends PagingServiceImpl<UserRes, Customer, Long> implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;

    @Override
    protected BaseRepository<Customer, Long> getRepository() {
        return customerRepository;
    }

    @Override
    protected UserRes convertToDto(Customer entity) {
        return userMapper.toDto(entity);
    }

    @Override
    protected PagingRes<UserRes> toPagingResult(Page<Customer> page, Function<Customer, UserRes> converter) {
        return userMapper.toPagingResult(page, converter);
    }

    @Override
    @Transactional
    public void updateMemberTier(Long customerId, String memberTier) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
            () -> new UsernameNotFoundException("Customer not found")
        );
        // Uppercase the memberTier to match the enum values
        customer.setMemberTier(MemberTier.valueOf(memberTier.toUpperCase()));
        customerRepository.save(customer);
    }

    @Override
    public PagingRes<UserRes> getCustomersByTier(String memberTier, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            // Must be in uppercase to match the enum values
            MemberTier tier = MemberTier.valueOf(memberTier.toUpperCase());
            // Create a specification to filter customers by member tier
            Specification<Customer> spec = new SpecificationBuilder<Customer>()
                .addIfNotNull(tier, CustomerSpecification::hasMemberTier)
                .build();
            return getMany(spec, pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new UsernameNotFoundException("No customers found");
        }
    }

    @Override
    public PagingRes<UserRes> getCustomers(String name, String memberTier, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return getMany(null, pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new UsernameNotFoundException("No customers found");
        }
    }


}
