package com.example.assignment.service.impl;

import com.example.assignment.dto.response.PagingResult;
import com.example.assignment.entity.Customer;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.UserService;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.UserDtoRes;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.mapper.UserProfileMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing user (CRUD) operations.
 * This class implements the UserService interface and provides methods for creating, updating, deleting, and retrieving users.
 * It uses UserRepository and CustomerRepository for database operations.
 * It also uses UserMapper and UserProfileMapper for converting between User entity and DTOs.
 * Use for user authentication and authorization purposes.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    public UserServiceImpl(
        UserRepository userRepository,
        CustomerRepository customerRepository,
        UserMapper userMapper,
        UserProfileMapper userProfileMapper
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserDtoRes createUser(UserCreationReq userCreationReq) {
        // check if the user already exists
        if (userRepository.existsUserByEmail(userCreationReq.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        UserProfile userProfile = userProfileMapper.toEntity(userCreationReq);
        Customer user = userMapper.toCustomer(userCreationReq, userProfile);
        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        customerRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDtoRes updateUserById(Long id, UserInfoUpdatingReq userInfoUpdatingReq) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile existingUserProfile = user.getUserProfile();
        existingUserProfile.setFirstName(userInfoUpdatingReq.getFirstName());
        existingUserProfile.setLastName(userInfoUpdatingReq.getLastName());
        existingUserProfile.setAddress(userInfoUpdatingReq.getAddress());
        existingUserProfile.setBio(userInfoUpdatingReq.getBio());
        existingUserProfile.setAvatar(userInfoUpdatingReq.getAvatar());
        user.setUserProfile(existingUserProfile);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
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
    public PagingResult<UserDtoRes> getUsers(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<User> userPages = userRepository.findAll(paging);
        if (userPages.hasContent()) {
            return userMapper.toPagingResult(userPages, userMapper::toDto);
        } else {
            throw new UsernameNotFoundException("No users found");
        }
    }

    @Override
    public UserDtoRes getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

}
