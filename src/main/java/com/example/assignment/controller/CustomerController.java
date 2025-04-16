package com.example.assignment.controller;

import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.dto.response.UserDtoRes;
import com.example.assignment.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<PagingResult<UserDtoRes>> getPageableCustomers(
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy) {
        try {
            PagingResult<UserDtoRes> customers = customerService.getPageableCustomers(pageNo, pageSize, sortBy);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tier")
    public ResponseEntity<PagingResult<UserDtoRes>> getPageableCustomersByTier(
        @RequestParam String memberTier,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy) {
        try {
            PagingResult<UserDtoRes> customers = customerService.getPageableCustomersByTier(memberTier, pageNo, pageSize, sortBy);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{customerId}/tier")
    public ResponseEntity<Void> updateMemberTier(
        @PathVariable Long customerId,
        @RequestParam String memberTier) {
        try {
            customerService.updateMemberTier(customerId, memberTier);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
