package com.example.assignment;

import com.example.assignment.dto.request.CategoryCreationReq;
import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.request.UserInfoUpdatingReq;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import com.example.assignment.enums.Role;
import com.example.assignment.mapper.CategoryMapper;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.mapper.UserProfileMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.CustomerRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.impl.CategoryServiceImpl;
import com.example.assignment.service.impl.ProductServiceImpl;
import com.example.assignment.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the audit provider functionality.
 * This class tests that the audit provider correctly sets the user who created/updated entities.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class AuditProviderTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private User adminUser;

    @Mock
    private User customerUser;

    @Mock
    private Category category;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // Setup security context
        SecurityContextHolder.setContext(securityContext);

        // Setup admin user
        when(adminUser.getRole()).thenReturn(Role.ADMIN);

        // Setup customer user
        when(customerUser.getRole()).thenReturn(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Test correct user is set when creating a category")
    void testCorrectUserIsSetWhenCreatingCategory() {
        // Setup security context with admin user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        // Setup category creation request
        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name("Test Category")
                .description("Test Description")
                .build();

        // Setup category mapper
        Category newCategory = new Category();
        when(categoryMapper.toEntity(categoryCreationReq)).thenReturn(newCategory);
        when(categoryMapper.toDtoRes(any(Category.class))).thenReturn(null); // Not used in this test

        // Setup category repository
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setCreatedBy(adminUser); // Simulate JPA auditing
            return savedCategory;
        });

        // Execute
        categoryService.createCategory(categoryCreationReq);

        // Verify
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        Category savedCategory = categoryCaptor.getValue();
        assertEquals(adminUser, savedCategory.getCreatedBy(), "Admin user should be set as the creator of the category");
    }

    @Test
    @DisplayName("Test correct user is set when creating a product")
    void testCorrectUserIsSetWhenCreatingProduct() {
        // Setup security context with admin user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        // Setup product creation request
        ProductCreationReq productCreationReq = ProductCreationReq.builder()
                .name("Test Product")
                .description("Test Description")
                .price(100.0)
                .quantity(10)
                .categoryId(1L)
                .build();

        // Setup category repository
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        // Setup product mapper
        Product newProduct = new Product();
        newProduct.setName("Test Product"); // Set required properties to pass validation
        newProduct.setPrice(100.0);
        newProduct.setQuantity(10);
        when(productMapper.toEntity(productCreationReq)).thenReturn(newProduct);
        when(productMapper.toDto(any(Product.class))).thenReturn(null); // Not used in this test

        // Setup product repository
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setCreatedBy(adminUser); // Simulate JPA auditing
            return savedProduct;
        });

        // Execute
        productService.createProduct(productCreationReq);

        // Verify
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();
        assertEquals(adminUser, savedProduct.getCreatedBy(), "Admin user should be set as the creator of the product");
    }

    @Test
    @DisplayName("Test null is set when customer registers themselves")
    void testNullIsSetWhenCustomerRegistersSelf() {
        // Set up a security context with no authenticated user (anonymous registration)
        when(securityContext.getAuthentication()).thenReturn(null);

        // Setup user creation request
        UserCreationReq userCreationReq = UserCreationReq.builder()
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .build();

        // Setup user profile
        UserProfile userProfile = new UserProfile();
        when(userProfileMapper.toEntity(any(UserCreationReq.class))).thenReturn(userProfile);

        // Setup customer
        Customer customer = new Customer();
        when(userMapper.toCustomer(any(UserCreationReq.class), any(UserProfile.class))).thenReturn(customer);
        when(userMapper.toDto(any(User.class))).thenReturn(null); // Not used in this test

        // Setup customer repository
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            // createdBy should be null for self-registration
            return invocation.<Customer>getArgument(0);
        });

        // Execute
        userService.createUser(userCreationReq);

        // Verify
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();
        assertNull(savedCustomer.getCreatedBy(), "Created by should be null when customer registers themselves");
    }

    @Test
    @DisplayName("Test correct user is set when admin creates a user")
    void testCorrectUserIsSetWhenAdminCreatesUser() {
        // Setup security context with admin user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        // Setup user creation request
        UserCreationReq userCreationReq = UserCreationReq.builder()
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .build();

        // Setup user profile
        UserProfile userProfile = new UserProfile();
        when(userProfileMapper.toEntity(any(UserCreationReq.class))).thenReturn(userProfile);

        // Setup customer
        Customer customer = new Customer();
        when(userMapper.toCustomer(any(UserCreationReq.class), any(UserProfile.class))).thenReturn(customer);
        when(userMapper.toDto(any(User.class))).thenReturn(null); // Not used in this test

        // Setup customer repository
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setCreatedBy(adminUser); // Simulate JPA auditing
            return savedCustomer;
        });

        // Execute
        userService.createUser(userCreationReq);

        // Verify
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();
        assertEquals(adminUser, savedCustomer.getCreatedBy(), "Admin user should be set as the creator of the user");
    }

    @Test
    @DisplayName("Test correct user is set when updating a category")
    void testCorrectUserIsSetWhenUpdatingCategory() {
        // Setup security context with admin user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        // Setup category update request
        CategoryCreationReq categoryUpdateReq = CategoryCreationReq.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        // Set up an existing category
        Category existingCategory = new Category();
        existingCategory.setName("Original Category");
        existingCategory.setDescription("Original Description");
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(existingCategory));

        // Setup category repository
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setUpdatedBy(adminUser); // Simulate JPA auditing
            return savedCategory;
        });

        // Execute
        categoryService.updateCategoryById(1L, categoryUpdateReq);

        // Verify
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        Category updatedCategory = categoryCaptor.getValue();
        assertEquals(adminUser, updatedCategory.getUpdatedBy(), "Admin user should be set as the updater of the category");
    }

    @Test
    @DisplayName("Test correct user is set when updating a product")
    void testCorrectUserIsSetWhenUpdatingProduct() {
        // Setup security context with admin user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        // Setup product update request
        ProductUpdatingReq productUpdateReq = ProductUpdatingReq.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(200.0)
                .quantity(20)
                .build();

        // Set up an existing product
        Product existingProduct = new Product();
        existingProduct.setName("Original Product");
        existingProduct.setDescription("Original Description");
        existingProduct.setPrice(100.0);
        existingProduct.setQuantity(10);
        existingProduct.setCategory(category);
        existingProduct.setCreatedBy(adminUser); // Admin created this product

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(existingProduct));

        // Setup product repository
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setUpdatedBy(adminUser); // Simulate JPA auditing
            return savedProduct;
        });

        // Execute
        productService.updateProductById(1L, productUpdateReq);

        // Verify
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();
        assertEquals(adminUser, updatedProduct.getUpdatedBy(), "Admin user should be set as the updater of the product");
    }

    @Test
    @DisplayName("Test correct user is set when customer updates themselves")
    void testCorrectUserIsSetWhenCustomerUpdatesSelf() {
        // Setup security context with customer user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);

        // Setup user update request
        UserInfoUpdatingReq userUpdateReq = UserInfoUpdatingReq.builder()
                .firstName("Updated First Name")
                .lastName("Updated Last Name")
                .address("Updated Address")
                .bio("Updated Bio")
                .build();

        // Set up existing user and user profile
        UserProfile existingUserProfile = new UserProfile();
        existingUserProfile.setFirstName("Original First Name");
        existingUserProfile.setLastName("Original Last Name");
        existingUserProfile.setAddress("Original Address");
        existingUserProfile.setBio("Original Bio");

        when(customerUser.getUserProfile()).thenReturn(existingUserProfile);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(customerUser));

        // Setup user repository
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setUpdatedBy(null); // Customer updating themselves should set null
            return savedUser;
        });

        // Execute
        userService.updateUserById(1L, userUpdateReq);

        // Verify
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertNull(updatedUser.getUpdatedBy(), "Updated by should be null when customer updates themselves");
    }
}
