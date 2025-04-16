package com.example.assignment.service.impl;

import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.dto.response.UserDtoRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserMapper userMapper) {
        this.customerRepository = customerRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void updateMemberTier(Long customerId, String memberTier) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
            () -> new UsernameNotFoundException("Customer not found")
        );
        customer.setMemberTier(MemberTier.valueOf(memberTier));
        customerRepository.save(customer);
    }

    @Override
    public PagingResult<UserDtoRes> getPageableCustomersByTier(String memberTier, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Customer> customerPage = customerRepository.findByMemberTier(MemberTier.valueOf(memberTier), paging);
        return userMapper.toPagingResult(customerPage, userMapper::toDto);
    }

    @Override
    public PagingResult<UserDtoRes> getPageableCustomers(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Customer> customerPage = customerRepository.findAll(paging);
        return userMapper.toPagingResult(customerPage, userMapper::toDto);
    }


}
