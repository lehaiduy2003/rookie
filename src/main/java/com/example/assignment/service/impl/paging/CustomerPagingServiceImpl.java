package com.example.assignment.service.impl.paging;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Logging
public class CustomerPagingServiceImpl extends PagingServiceImpl<UserRes, Customer, Long> {
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;

    public CustomerPagingServiceImpl(CustomerRepository customerRepository, UserMapper userMapper) {
        this.customerRepository = customerRepository;
        this.userMapper = userMapper;
    }

    @Override
    protected JpaRepository<Customer, Long> getRepository() {
        return customerRepository;
    }

    @Override
    protected PagingRes<UserRes> toPagingResult(Page<Customer> page, Function<Customer, UserRes> converter) {
        return userMapper.toPagingResult(page, converter);
    }

    @Override
    protected Page<Customer> findByRelatedId(Long id, Pageable pageable) {
        // Not implemented for customers
        return Page.empty(pageable);
    }

    @Override
    protected Page<Customer> findByCriteria(String criteria, Pageable pageable) {
        // This method handles search by member tier
        try {
            MemberTier memberTier = MemberTier.valueOf(criteria);
            return customerRepository.findByMemberTier(memberTier, pageable);
        } catch (IllegalArgumentException e) {
            // If the criteria is not a valid MemberTier, return an empty page
            return Page.empty(pageable);
        }
    }

    @Override
    protected UserRes convertToDto(Customer entity) {
        return userMapper.toDto(entity);
    }
}
