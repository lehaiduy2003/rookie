package com.example.assignment.config.seed;

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
import com.example.assignment.util.PasswordUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(1)
public class UserSeed implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordUtil passwordUtil;

    private UserCreationReq buildUserCreationReq(String email, String phoneNumber, String password, String firstName, String lastName, String address, Role role, @Nullable MemberTier memberTier) {
        return UserCreationReq.builder()
            .email(email)
            .phoneNumber(phoneNumber)
            .password(password)
            .firstName(firstName)
            .lastName(lastName)
            .address(address)
            .role(role)
            .memberTier(memberTier)
            .build();
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
                "admin123",
                "admin",
                "Admin",
                "Admin Address",
                Role.ADMIN,
                null
            );

            UserProfile adminProfile = buildUserProfile(adminReq);
            User adminUser = userMapper.toEntity(adminReq, adminProfile);
            adminUser.setPassword(passwordUtil.encode(adminReq.getPassword()));
            adminUser.setUserProfile(adminProfile);
            adminProfile.setUser(adminUser);
            userRepository.save(adminUser);
        }

        // Seed customer user
        if (!userRepository.existsUserByEmail("customer@example.com")) {
            UserCreationReq customerReq = buildUserCreationReq(
                "customer@example.com",
                "987654321",
                "customer123",
                "customer",
                "Customer",
                "Customer Address",
                Role.CUSTOMER,
                MemberTier.COMMON
            );

            UserProfile customerProfile = buildUserProfile(customerReq);
            Customer customer = userMapper.toCustomer(customerReq, customerProfile);
            customer.setPassword(passwordUtil.encode(customerReq.getPassword()));
            customer.setUserProfile(customerProfile);
            customerProfile.setUser(customer);
            customerRepository.save(customer);

        }
    }
}
