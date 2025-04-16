package com.example.assignment.config.seed;

import com.example.assignment.config.PasswordEncoderConfig;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.mapper.UserProfileMapper;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserSeed implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    public UserSeed(UserRepository userRepository, CustomerRepository customerRepository, UserMapper userMapper, UserProfileMapper userProfileMapper) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
    }

    private UserCreationReq buildUserCreationReq(String email, String phoneNumber, String password, String firstName, String lastName, String address, Role role, @Nullable MemberTier memberTier) {
        UserCreationReq userCreationReq = new UserCreationReq();
        userCreationReq.setEmail(email);
        userCreationReq.setPhoneNumber(phoneNumber);
        userCreationReq.setPassword(PasswordEncoderConfig.passwordEncoder().encode(password));
        userCreationReq.setFirstName(firstName);
        userCreationReq.setAddress(address);
        userCreationReq.setLastName(lastName);
        userCreationReq.setRole(role);
        userCreationReq.setMemberTier(memberTier);
        return userCreationReq;
    }

    private UserProfile buildUserProfile(UserCreationReq userCreationReq) {
        return userProfileMapper.toEntity(userCreationReq);
    }

    /**
     * This method is called when the application starts.
     * It seeds the database with an admin user and a customer user if they do not already exist.
     *
     * @param args command line arguments
     */
    @Override
    @Transactional
    public void run(String... args) {
        // Seed admin user
        if (!userRepository.existsUserByEmail("admin@example.com")) {
            UserCreationReq adminReq = buildUserCreationReq(
                "admin@example.com",
                "123456789",
                "admin",
                "admin",
                "Admin",
                "Admin Address",
                Role.ADMIN,
                null
            );

            UserProfile adminProfile = buildUserProfile(adminReq);
            User adminUser = userMapper.toEntity(adminReq, adminProfile);
            adminUser.setUserProfile(adminProfile);
            adminProfile.setUser(adminUser);
            userRepository.save(adminUser);
        }

        // Seed customer user
        if (!userRepository.existsUserByEmail("customer@example.com")) {
            UserCreationReq customerReq = buildUserCreationReq(
                "customer@example.com",
                "987654321",
                "customer",
                "customer",
                "Customer",
                "Customer Address",
                Role.CUSTOMER,
                MemberTier.COMMON
            );

            UserProfile customerProfile = buildUserProfile(customerReq);
            Customer customer = userMapper.toCustomer(customerReq, customerProfile);
            customer.setUserProfile(customerProfile);
            customerProfile.setUser(customer);
            customerRepository.save(customer);
        }
    }
}
