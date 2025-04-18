package com.example.assignment.controller;

import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.service.UserService;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserRes> updateUser(@PathVariable Long id, @Valid @RequestBody UserInfoUpdatingReq userInfoUpdatingReq) {
        try {
            UserRes updatedUser = userService.updateUserById(id, userInfoUpdatingReq);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagingRes<UserRes>> getPageableUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            PagingRes<UserRes> users = userService.getUsers(pageNo, pageSize, sortDir, sortBy);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsRes> getUserById(@PathVariable Long id) {
        try {
            UserDetailsRes user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
