package com.example.assignment.controller;

import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.service.UserService;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.UserDtoRes;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDtoRes> createUser(@Valid @RequestBody UserCreationReq userCreationReq) {
        try {
            UserDtoRes createdUser = userService.createUser(userCreationReq);
            return ResponseEntity.status(201).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDtoRes> updateUser(@PathVariable Long id, @Valid @RequestBody UserInfoUpdatingReq userInfoUpdatingReq) {
        try {
            UserDtoRes updatedUser = userService.updateUserById(id, userInfoUpdatingReq);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<PagingResult<UserDtoRes>> getPageableUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            PagingResult<UserDtoRes> users = userService.getUsers(pageNo, pageSize, sortBy);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDtoRes> getUserById(@PathVariable Long id) {
        try {
            UserDtoRes user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
