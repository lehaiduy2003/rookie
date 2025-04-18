package com.example.assignment.controller;

import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.exception.ResourceAlreadyExistException;
import com.example.assignment.service.UserService;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserRes> createUser(@Valid @RequestBody UserCreationReq userCreationReq) {
        try {
            UserRes createdUser = userService.createUser(userCreationReq);
            return ResponseEntity.status(201).body(createdUser);
        } catch (ResourceAlreadyExistException e) {
            return ResponseEntity.status(409).build(); // Conflict
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<UserRes> updateUser(@PathVariable Long id, @Valid @RequestBody UserInfoUpdatingReq userInfoUpdatingReq) {
        try {
            UserRes updatedUser = userService.updateUserById(id, userInfoUpdatingReq);
            return ResponseEntity.ok(updatedUser);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).build(); // Not Found
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).build(); // Not Found
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PagingRes<UserRes>> getPageableUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            PagingRes<UserRes> users = userService.getUsers(pageNo, pageSize, sortDir, sortBy);
            return ResponseEntity.ok(users);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).build(); // Not Found
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsRes> getUserById(@PathVariable Long id) {
        try {
            UserDetailsRes user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).build(); // Not Found
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
