package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.exception.ExistingResourceException;
import com.example.assignment.repository.BaseRepository;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.UserService;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Service implementation for managing user (CRUD) operations.
 * This class implements the UserService interface and provides methods for creating, updating, deleting, and retrieving users.
 * It uses UserRepository and CustomerRepository for database operations.
 * It also uses UserMapper and UserProfileMapper for converting between User entity and DTOs.
 * Use for user authentication and authorization.
 */
@Service
@RequiredArgsConstructor
@Logging
public class UserServiceImpl extends PagingServiceImpl<UserRes, User, Long> implements UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected BaseRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected UserRes convertToDto(User entity) {
        return userMapper.toDto(entity);
    }

    @Override
    protected PagingRes<UserRes> toPagingResult(Page<User> page, Function<User, UserRes> converter) {
        return userMapper.toPagingResult(page, converter);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    @Override
    @Transactional
    public UserDetailsRes createUser(UserCreationReq userCreationReq) {
        // check if the user already exists
        if (userRepository.existsUserByEmail(userCreationReq.getEmail())) {
            throw new ExistingResourceException("User already exists");
        }
        UserProfile userProfile = userProfileMapper.toEntity(userCreationReq);
        Customer user = userMapper.toCustomer(userCreationReq, userProfile);
        String encodedPassword = passwordEncoder.encode(userCreationReq.getPassword());
        user.setPassword(encodedPassword);// Assuming password is already encoded
        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        customerRepository.save(user);
        return userMapper.toUserDetailsDto(user);
    }

    @Override
    @Transactional
    public UserDetailsRes updateUserById(Long id, UserInfoUpdatingReq userInfoUpdatingReq) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile existingUserProfile = user.getUserProfile();
        existingUserProfile.setFirstName(userInfoUpdatingReq.getFirstName());
        existingUserProfile.setLastName(userInfoUpdatingReq.getLastName());
        existingUserProfile.setAddress(userInfoUpdatingReq.getAddress());
        existingUserProfile.setBio(userInfoUpdatingReq.getBio());
        existingUserProfile.setAvatar(userInfoUpdatingReq.getAvatar());
        user.setUserProfile(existingUserProfile);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserDetailsDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        // check if the user exists
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // check if the user is a customer
        if(user instanceof Customer customer) {
            customerRepository.delete(customer);
        } else {
            userRepository.delete(user);
        }
    }

    @Override
    public PagingRes<UserRes> getUsers(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            return getMany(null, pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            // More appropriate to return an empty result than throw an exception
            return PagingRes.<UserRes>builder()
                    .content(new ArrayList<>())
                    .totalElements(0)
                    .totalPages(0)
                    .size(pageSize)
                    .page(pageNo)
                    .empty(true)
                    .build();
        }
    }

    @Override
    public UserDetailsRes getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toUserDetailsDto(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }
}
