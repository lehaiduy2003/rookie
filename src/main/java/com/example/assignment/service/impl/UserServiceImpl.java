package com.example.assignment.service.impl;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.request.*;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.exception.ExistingResourceException;
import com.example.assignment.repository.BaseRepository;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.UserService;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.mapper.UserProfileMapper;
import com.example.assignment.specification.UserSpecification;
import com.example.assignment.util.PasswordUtil;
import com.example.assignment.util.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class UserServiceImpl extends PagingServiceImpl<UserDetailsRes, User, Long> implements UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordUtil passwordUtil;

    @Override
    protected BaseRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected UserDetailsRes convertToDto(User entity) {
        return userMapper.toUserDetailsDto(entity);
    }

    @Override
    protected PagingRes<UserDetailsRes> toPagingResult(Page<User> page, Function<User, UserDetailsRes> converter) {
        return userMapper.toPagingResult(page, converter);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
        String encodedPassword = passwordUtil.encode(userCreationReq.getPassword());
        user.setPassword(encodedPassword);
        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        customerRepository.save(user);
        return userMapper.toUserDetailsDto(user);
    }

    @Override
    @Transactional
    public UserDetailsRes updateUserById(Long id, UserInfoUpdatingReq userInfoUpdatingReq) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updateUserProfile(user, userInfoUpdatingReq);
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
    public PagingRes<UserDetailsRes> getUsers(UserFilterReq filter, Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
        try {
            Specification<Customer> customerSpec = new SpecificationBuilder<Customer>()
                .addIfNotNull(filter.getFirstName(), UserSpecification::hasFirstName)
                .addIfNotNull(filter.getLastName(), UserSpecification::hasLastName)
                .addIfNotNull(filter.getEmail(), UserSpecification::hasEmail)
                .addIfNotNull(filter.getIsActive(), UserSpecification::hasActiveStatus)
                .addIfNotNull(filter.getMemberTier(), UserSpecification::hasMemberTier)
                .addIfNotNull(filter.getRole(), UserSpecification::hasRole)
                .addIfNotNull(filter.getCreatedOn(), UserSpecification::hasCreatedOn)
                .addIfNotNull(filter.getUpdatedOn(), UserSpecification::hasUpdatedOn)
                .build();
            // Cast to Specification<User> using an intermediate wildcard type
            @SuppressWarnings("unchecked")
            Specification<User> userSpec = (Specification<User>)(Specification<?>) customerSpec;
            return getMany(userSpec, pageNo, pageSize, sortDir, sortBy);
        } catch (Exception e) {
            // More appropriate to return an empty result than throw an exception
            return PagingRes.<UserDetailsRes>builder()
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

    @Override
    @Transactional
    public void bulkDeleteCustomers(List<Long> ids) {
        List<Customer> customers = customerRepository.findAllById(ids);
        customerRepository.deleteAll(customers);
    }

    @Override
    @Transactional
    public UserDetailsRes updateUserById(Long id, UserUpdatingReq userUpdatingReq) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updateUserProfile(user, userUpdatingReq);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserDetailsDto(updatedUser);
    }

    private void updateUserProfile(User user, UserUpdatingReq userUpdatingReq) {
        setUserInfo(
            user,
            userUpdatingReq.getFirstName(),
            userUpdatingReq.getLastName(),
            userUpdatingReq.getAddress(),
            userUpdatingReq.getPhoneNumber(),
            userUpdatingReq.getEmail(),
            null,
            null,
            null,
            userUpdatingReq.getIsActive());
    }

    private void updateUserProfile(User user, UserInfoUpdatingReq userInfoUpdatingReq) {
        setUserInfo(
            user,
            userInfoUpdatingReq.getFirstName(),
            userInfoUpdatingReq.getLastName(),
            userInfoUpdatingReq.getAddress(),
            null,
            null,
            userInfoUpdatingReq.getBio(),
            userInfoUpdatingReq.getAvatar(),
            userInfoUpdatingReq.getDob(),
            null
        );
    }

    private void setUserInfo(
        User user,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String email,
        String bio,
        String avatar,
        Date dob,
        Boolean isActive
    ) {
        UserProfile existingUserProfile = user.getUserProfile();

        if(firstName != null) {
            existingUserProfile.setFirstName(firstName);
        }
        if(lastName != null) {
            existingUserProfile.setLastName(lastName);
        }
        if(address != null) {
            existingUserProfile.setAddress(address);
        }
        if(phoneNumber != null) {
            existingUserProfile.setPhoneNumber(phoneNumber);
        }
        if(email != null) {
            user.setEmail(email);
        }
        if(bio != null) {
            existingUserProfile.setBio(bio);
        }
        if(avatar != null) {
            existingUserProfile.setAvatar(avatar);
        }
        if(dob != null) {
            existingUserProfile.setDob(dob);
        }
        if(isActive != null) {
            user.setIsActive(isActive);
        }
        user.setUserProfile(existingUserProfile);
    }
}
