package com.example.assignment.serviceImpl;

import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.enums.MemberTier;
import com.example.assignment.enums.Role;
import com.example.assignment.exception.ResourceAlreadyExistException;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.mapper.UserProfileMapper;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.impl.UserServiceImpl;
import com.example.assignment.service.impl.paging.UserPagingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for UserService to ensure:
 * - User creation works correctly
 * - User update works correctly
 * - User deletion works correctly
 * - User retrieval works correctly
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private UserPagingServiceImpl userPagingService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreationReq userCreationReq;
    private UserInfoUpdatingReq userInfoUpdatingReq;
    private User user;
    private Customer customer;
    private UserProfile userProfile;
    private UserRes userRes;
    private UserDetailsRes userDetailsRes;

    @BeforeEach
    void setUp() {
        // Set up common test data
        userCreationReq = UserCreationReq.builder()
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .memberTier(MemberTier.COMMON)
                .build();

        userInfoUpdatingReq = UserInfoUpdatingReq.builder()
                .firstName("Updated")
                .lastName("User")
                .address("123 Test St")
                .bio("Test bio")
                .avatar("avatar.jpg")
                .build();

        userProfile = new UserProfile();
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");
        userProfile.setAddress("123 Test St");
        userProfile.setBio("Test bio");
        userProfile.setAvatar("avatar.jpg");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);
        user.setUserProfile(userProfile);
        userProfile.setUser(user);

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setPassword("encodedPassword");
        customer.setRole(Role.CUSTOMER);
        customer.setMemberTier(MemberTier.COMMON);
        customer.setUserProfile(userProfile);
        userProfile.setUser(customer);

        userRes = UserRes.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .role("CUSTOMER")
                .memberTier("COMMON")
                .build();

        userDetailsRes = UserDetailsRes.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role("CUSTOMER")
                .memberTier("COMMON")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Test loadUserByUsername returns user when found")
    void testLoadUserByUsernameReturnsUserWhenFound() {
        // Setup
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // Test
        User result = (User) userService.loadUserByUsername("test@example.com");

        // Verify
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Test loadUserByUsername throws exception when user not found")
    void testLoadUserByUsernameThrowsExceptionWhenUserNotFound() {
        // Setup
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Test & Verify
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent@example.com"));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Test createUser creates user successfully")
    void testCreateUserCreatesUserSuccessfully() {
        // Setup
        when(userRepository.existsUserByEmail(anyString())).thenReturn(false);
        when(userProfileMapper.toEntity(any(UserCreationReq.class))).thenReturn(userProfile);
        when(userMapper.toCustomer(any(UserCreationReq.class), any(UserProfile.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(userMapper.toDto(any(User.class))).thenReturn(userRes);

        // Test
        UserDetailsRes result = userService.createUser(userCreationReq);

        // Verify
        assertNotNull(result, "The result should not be null");
        assertEquals(userDetailsRes, result, "The result should match the expected DTO");
        verify(userRepository).existsUserByEmail("test@example.com");
        verify(userProfileMapper).toEntity(userCreationReq);
        verify(userMapper).toCustomer(userCreationReq, userProfile);
        verify(customerRepository).save(customer);
        verify(userMapper).toDto(customer);
    }

    @Test
    @DisplayName("Test createUser throws exception when user already exists")
    void testCreateUserThrowsExceptionWhenUserAlreadyExists() {
        // Setup
        when(userRepository.existsUserByEmail(anyString())).thenReturn(true);

        // Test & Verify
        assertThrows(ResourceAlreadyExistException.class, () -> userService.createUser(userCreationReq));
        verify(userRepository).existsUserByEmail("test@example.com");
        verify(userProfileMapper, never()).toEntity(any(UserCreationReq.class));
        verify(userMapper, never()).toCustomer(any(UserCreationReq.class), any(UserProfile.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Test updateUserById updates user successfully")
    void testUpdateUserByIdUpdatesUserSuccessfully() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userRes);

        // Test
        UserDetailsRes result = userService.updateUserById(1L, userInfoUpdatingReq);

        // Verify
        assertNotNull(result);
        assertEquals(userDetailsRes, result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);

        // Verify the user profile was updated
        assertEquals("Updated", user.getUserProfile().getFirstName());
        assertEquals("User", user.getUserProfile().getLastName());
        assertEquals("123 Test St", user.getUserProfile().getAddress());
        assertEquals("Test bio", user.getUserProfile().getBio());
        assertEquals("avatar.jpg", user.getUserProfile().getAvatar());
    }

    @Test
    @DisplayName("Test updateUserById throws exception when user not found")
    void testUpdateUserByIdThrowsExceptionWhenUserNotFound() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Test & Verify
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUserById(1L, userInfoUpdatingReq));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test deleteUserById deletes customer successfully")
    void testDeleteUserByIdDeletesCustomerSuccessfully() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(any(Customer.class));

        // Test
        userService.deleteUserById(1L);

        // Verify
        verify(userRepository).findById(1L);
        verify(customerRepository).delete(customer);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Test deleteUserById deletes user successfully")
    void testDeleteUserByIdDeletesUserSuccessfully() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        // Test
        userService.deleteUserById(1L);

        // Verify
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("Test deleteUserById throws exception when user not found")
    void testDeleteUserByIdThrowsExceptionWhenUserNotFound() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Test & Verify
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUserById(1L));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("Test getUsers returns paginated users successfully")
    void testGetUsersReturnsPaginatedUsersSuccessfully() {
        // Setup
        List<UserRes> userList = new ArrayList<>();
        userList.add(userRes);
        PagingRes<UserRes> pagingRes = PagingRes.<UserRes>builder()
                .content(userList)
                .totalElements(1)
                .totalPages(1)
                .size(10)
                .page(0)
                .empty(false)
                .build();

        when(userPagingService.getMany(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pagingRes);

        // Test
        PagingRes<UserRes> result = userService.getUsers(0, 10, "asc", "id");

        // Verify
        assertNotNull(result);
        assertEquals(pagingRes, result);
        verify(userPagingService).getMany(0, 10, "asc", "id");
    }

    @Test
    @DisplayName("Test getUserById returns user successfully")
    void testGetUserByIdReturnsUserSuccessfully() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(userDetailsRes);

        // Test
        UserDetailsRes result = userService.getUserById(1L);

        // Verify
        assertNotNull(result);
        assertEquals(userDetailsRes, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserDetailsDto(user);
    }

    @Test
    @DisplayName("Test getUserById throws exception when user not found")
    void testGetUserByIdThrowsExceptionWhenUserNotFound() {
        // Setup
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Test & Verify
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
        verify(userMapper, never()).toUserDetailsDto(any(User.class));
    }
}
