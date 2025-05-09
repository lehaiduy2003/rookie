package com.example.assignment.controller;

import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<PagingRes<UserRes>> getPageableCustomers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String memberTier,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(defaultValue = "id") String sortBy
        ) {
        try {
            PagingRes<UserRes> customers = customerService.getCustomers(name, memberTier, pageNo, pageSize, sortDir, sortBy);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tier")
    public ResponseEntity<PagingRes<UserRes>> getPageableCustomersByTier(
        @RequestParam String memberTier,
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(defaultValue = "id") String sortBy) {
        PagingRes<UserRes> customers = customerService.getCustomersByTier(memberTier, pageNo, pageSize, sortDir, sortBy);
        return ResponseEntity.ok(customers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{customerId}/tier/{memberTier}")
    public ResponseEntity<Void> updateMemberTier(
        @PathVariable Long customerId,
        @PathVariable String memberTier) {
        try {
            customerService.updateMemberTier(customerId, memberTier);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
