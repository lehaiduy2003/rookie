package com.example.assignment.service.impl;

import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.service.CustomerService;
import com.example.assignment.service.impl.paging.CustomerPagingServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerPagingServiceImpl customerPagingService;

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
            return customerPagingService.getByCriteria(memberTier.toUpperCase(), pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new UsernameNotFoundException("No customers found");
        }
    }

    @Override
    public PagingRes<UserRes> getCustomers(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return customerPagingService.getMany(pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            throw new UsernameNotFoundException("No customers found");
        }
    }


}
