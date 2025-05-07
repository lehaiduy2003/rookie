package com.example.assignment.serviceImpl;

import com.example.assignment.dto.request.ProductCreationReq;
import com.example.assignment.dto.request.ProductFilterReq;
import com.example.assignment.dto.request.ProductUpdatingReq;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.ProductDetailRes;
import com.example.assignment.dto.response.ProductRes;
import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.mapper.ProductMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    @DisplayName("Test createProduct with valid data")
    void testCreateProduct() {
        ProductCreationReq productCreationReq = ProductCreationReq.builder()
                .name("Test Product")
                .description("Test Description")
                .price(100.0)
                .categoryId(1L)
                .build();

        // Mock the behavior of the category repository
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setCategory(category);
        product.setId(1L);

        ProductRes productRes = new ProductRes();
        productRes.setId(1L);
        productRes.setName("Test Product");

        // Mock the behavior of the product repository
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(productCreationReq)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productRes);

        // Call the method under test
        ProductRes result = productServiceImpl.createProduct(productCreationReq);

        // Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidProductCreationReqProvider")
    void testCreateProductWithInvalidData(String testName, ProductCreationReq req, String expectedMessage) {
        // Configure necessary mocks for valid category when needed
        if (req.getCategoryId() != null) {
            Category category = new Category();
            category.setId(req.getCategoryId());
            when(categoryRepository.findById(req.getCategoryId())).thenReturn(Optional.of(category));
        }

        // Mock the product mapper to return a product with the same properties
        Product product = new Product();
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());

        // for the sake of this test, we assume categoryId is not null
        lenient().when(productMapper.toEntity(req)).thenReturn(product);

        // Call the method and expect an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> productServiceImpl.createProduct(req));

        assertEquals(expectedMessage, exception.getMessage());
    }


    static Stream<Arguments> invalidProductCreationReqProvider() {
        return Stream.of(
            Arguments.of("Negative price",
                ProductCreationReq.builder().name("Test").description("Test").price(-10.0).categoryId(1L).build(),
                "Product price cannot be negative"),
            Arguments.of("Null name",
                ProductCreationReq.builder().name(null).description("Test").price(10.0).categoryId(1L).build(),
                "Product name cannot be null or empty"),
            Arguments.of("Empty name",
                ProductCreationReq.builder().name("  ").description("Test").price(10.0).categoryId(1L).build(),
                "Product name cannot be null or empty"),
            Arguments.of("Null categoryId",
                ProductCreationReq.builder().name("Test").description("Test").price(10.0).categoryId(null).build(),
                "Category ID cannot be null")
        );
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("updateProductSuccessCasesProvider")
    void testUpdateProductSuccess(String testName, ProductUpdatingReq req,
                                  Long productId, Product initialProduct,
                                  Category newCategory) {
        // Mock existing product
        when(productRepository.findById(productId)).thenReturn(Optional.of(initialProduct));

        // Mock the save and dto conversion
        ProductRes productRes = new ProductRes();
        productRes.setId(productId);
        productRes.setName(initialProduct.getName());

        when(productRepository.save(initialProduct)).thenReturn(initialProduct);
        when(productMapper.toDto(initialProduct)).thenReturn(productRes);

        // Mock category repository response when categoryId is provided
        if (req.getCategoryId() != null) {
            when(categoryRepository.findById(req.getCategoryId())).thenReturn(Optional.of(newCategory));
        }

        // Test success cases
        ProductRes result = productServiceImpl.updateProductById(productId, req);

        // Verify results
        assertNotNull(result);
        assertEquals(productId, result.getId());

        // Verify specific field updates
        if (req.getName() != null) {
            assertEquals(req.getName(), initialProduct.getName());
        }
        if (req.getDescription() != null) {
            assertEquals(req.getDescription(), initialProduct.getDescription());
        }
        if (req.getPrice() != null) {
            assertEquals(req.getPrice(), initialProduct.getPrice());
        }
        if (req.getQuantity() != null) {
            assertEquals(req.getQuantity(), initialProduct.getQuantity());
        }
        if (req.getImageUrl() != null) {
            assertEquals(req.getImageUrl(), initialProduct.getImageUrl());
        }
        if (req.getIsActive() != null) {
            assertEquals(req.getIsActive(), initialProduct.getIsActive());
        }
        if (req.getFeatured() != null) {
            assertEquals(req.getFeatured(), initialProduct.isFeatured());
        }
        if (req.getCategoryId() != null) {
            assertEquals(newCategory, initialProduct.getCategory());
        }
    }

    static Stream<Arguments> updateProductSuccessCasesProvider() {
        Long validProductId = 1L;

        Category category1 = new Category();
        category1.setId(1L);

        Category category2 = new Category();
        category2.setId(2L);

        // Success case: Update all fields
        Product validProduct1 = createBaseProduct(validProductId, category1);
        ProductUpdatingReq allFieldsReq = ProductUpdatingReq.builder()
            .name("Updated Product")
            .description("Updated Description")
            .price(150.0)
            .quantity(20)
            .imageUrl("new-image.jpg")
            .isActive(true)
            .featured(true)
            .categoryId(2L)
            .build();

        // Success case: Update partial fields
        Product validProduct2 = createBaseProduct(validProductId, category1);
        ProductUpdatingReq partialReq = ProductUpdatingReq.builder()
            .name("Partial Update")
            .price(200.0)
            .build();

        return Stream.of(
            Arguments.of("Update all fields", allFieldsReq, validProductId, validProduct1, category2),
            Arguments.of("Update some fields", partialReq, validProductId, validProduct2, null)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateProductErrorCasesProvider")
    void testUpdateProductErrors(String testName, ProductUpdatingReq req,
                                 Long productId, Product initialProduct,
                                    Class<? extends Exception> expectedExceptionClass,
                                 String expectedExceptionMessage) {
        // Mock existing product if not testing 'product not found'
        if (initialProduct != null) {
            when(productRepository.findById(productId)).thenReturn(Optional.of(initialProduct));
        } else {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());
        }

        // Mock category repository response for invalid category ID
        if (req.getCategoryId() != null && req.getCategoryId() == 999L) {
            when(categoryRepository.findById(req.getCategoryId())).thenReturn(Optional.empty());
        }

        // Test error cases
        var exception = assertThrows(expectedExceptionClass,
            () -> productServiceImpl.updateProductById(productId, req));

        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    static Stream<Arguments> updateProductErrorCasesProvider() {
        Long validProductId = 1L;
        Long nonExistentProductId = 999L;

        Category category1 = new Category();
        category1.setId(1L);

        // Error case: Product not found
        ProductUpdatingReq validReq = ProductUpdatingReq.builder()
            .name("Test Product")
            .build();

        // Error case: Negative price
        Product validProduct1 = createBaseProduct(validProductId, category1);
        ProductUpdatingReq invalidPriceReq = ProductUpdatingReq.builder()
            .price(-10.0)
            .build();

        // Error case: Invalid category
        Product validProduct2 = createBaseProduct(validProductId, category1);
        ProductUpdatingReq invalidCategoryReq = ProductUpdatingReq.builder()
            .categoryId(999L)
            .build();

        // Error case: Empty name
        Product validProduct3 = createBaseProduct(validProductId, category1);
        ProductUpdatingReq emptyNameReq = ProductUpdatingReq.builder()
            .name("  ")
            .build();

        return Stream.of(
            Arguments.of("Product not found", validReq, nonExistentProductId, null,
                ResourceNotFoundException.class, "Product not found"),
            Arguments.of("Negative price", invalidPriceReq, validProductId, validProduct1,
                IllegalArgumentException.class, "Product price cannot be negative"),
            Arguments.of("Invalid category ID", invalidCategoryReq, validProductId, validProduct2,
                ResourceNotFoundException.class, "Category not found"),
            Arguments.of("Empty name", emptyNameReq, validProductId, validProduct3,
                IllegalArgumentException.class, "Product name cannot be null or empty"
            )
        );
    }
    private static Product createBaseProduct(Long id, Category category) {
        Product product = new Product();
        product.setId(id);
        product.setName("Original Product");
        product.setDescription("Original Description");
        product.setPrice(100.0);
        product.setQuantity(10);
        product.setImageUrl("original-image.jpg");
        product.setIsActive(false);
        product.setFeatured(false);
        product.setCategory(category);
        return product;
    }

    @Test
    @DisplayName("Test deleteProductById with valid ID")
    void testDeleteProductById() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        // Mock the behavior of the product repository
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Call the method under test
        productServiceImpl.deleteProductById(productId);

        // Verify that the product was deleted
        assertFalse(productRepository.existsById(productId));
    }

    @Test
    @DisplayName("Test deleteProductById with non-existent ID")
    void testDeleteProductByIdNotFound() {
        Long productId = 999L;

        // Mock the behavior of the product repository
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Call the method and expect an exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> productServiceImpl.deleteProductById(productId));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test deleteProductById with null ID")
    void testDeleteProductByIdNull() {

        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.deleteProductById(null));
    }

    @Test
    @DisplayName("Test getProductById with valid ID")
    void testGetProductById() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        // Mock the behavior of the mapper
        ProductDetailRes productRes = ProductDetailRes.builder()
                .id(productId)
                .name("Test Product")
                .build();

        // Mock the behavior of the product repository
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDetailsDto(product)).thenReturn(productRes);

        // Call the method under test
        ProductRes result = productServiceImpl.getProductById(productId);

        // Verify the result
        assertNotNull(result);
        assertEquals(productId, result.getId());
    }

    @Test
    @DisplayName("Test getProductById with non-existent ID")
    void testGetProductByIdNotFound() {
        Long productId = 999L;

        // Mock the behavior of the product repository
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Call the method and expect an exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> productServiceImpl.getProductById(productId));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test getProductById with null ID")
    void testGetProductByIdNull() {
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.getProductById(null));
    }

    @Test
    @DisplayName("Test getProductById with invalid ID")
    void testGetProductByIdInvalid() {
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.getProductById(-1L));
    }

    @Test
    @DisplayName("Test getProductById with empty ID")
    void testGetProductByIdEmpty() {
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.getProductById(0L));
    }

    @Test
    @DisplayName("Test getProducts success with filters")
    void testGetProductsSuccess() {
        // Setup filter request
        ProductFilterReq filterReq = ProductFilterReq.builder()
            .name("test")
            .isActive(true)
            .featured(true)
            .categoryId(1L)
            .minPrice(10.0)
            .maxPrice(100.0)
            .build();

        // Mock data
        List<Product> productList = List.of(
            createBaseProduct(1L, new Category()),
            createBaseProduct(2L, new Category())
        );

        Page<Product> page = new PageImpl<>(productList);
        PagingRes<ProductRes> expectedResult = new PagingRes<>(
            List.of(new ProductRes(), new ProductRes()),
            1, 2, 10, 0, false);

        // Setup mocks
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(productMapper.toPagingResult(eq(page), any(Function.class))).thenReturn(expectedResult);

        // Execute
        PagingRes<ProductRes> result = productServiceImpl.getProducts(
            filterReq, 0, 10, "asc", "name");

        // Verify
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        // Verify that the repository was called with the correct parameters
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @ParameterizedTest(name = "Test sorting with {0}, {1}")
    @MethodSource("sortingTestCases")
    void testGetProductsWithDifferentSorting(String sortBy, String sortDir, String expectedSortField) {
        // Setup filter request
        ProductFilterReq filterReq = ProductFilterReq.builder().build();

        // Capture pageable argument
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Mock data
        Page<Product> page = new PageImpl<>(List.of());
        PagingRes<ProductRes> expectedResult = new PagingRes<>();

        // Setup mocks
        when(productRepository.findAll(any(Specification.class), pageableCaptor.capture()))
            .thenReturn(page);
        when(productMapper.toPagingResult(eq(page), any(Function.class))).thenReturn(expectedResult);

        // Execute
        productServiceImpl.getProducts(filterReq, 0, 10, sortDir, sortBy);

        // Verify sort order
        Pageable capturedPageable = pageableCaptor.getValue();
        Sort.Order order = capturedPageable.getSort().getOrderFor(expectedSortField);
        assertNotNull(order);
        assertEquals(
            sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
            order.getDirection());
    }

    static Stream<Arguments> sortingTestCases() {
        return Stream.of(
            Arguments.of("name", "asc", "name"),
            Arguments.of("price", "desc", "price"),
            Arguments.of("id", "asc", "id"),
            Arguments.of("", "asc", "id"),    // Default sort field
            Arguments.of(null, "desc", "id")  // Default sort field
        );
    }

    @ParameterizedTest(name = "Test paging with page {0}, size {1}")
    @MethodSource("pagingTestCases")
    void testGetProductsWithDifferentPaging(Integer pageNo, Integer pageSize,
                                            Integer expectedPageNo, Integer expectedPageSize) {
        // Setup filter request
        ProductFilterReq filterReq = ProductFilterReq.builder().build();

        // Capture pageable argument
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Mock data
        Page<Product> page = new PageImpl<>(List.of());
        PagingRes<ProductRes> expectedResult = new PagingRes<>();

        // Setup mocks
        when(productRepository.findAll(any(Specification.class), pageableCaptor.capture()))
            .thenReturn(page);
        when(productMapper.toPagingResult(eq(page), any(Function.class))).thenReturn(expectedResult);

        // Execute
        productServiceImpl.getProducts(filterReq, pageNo, pageSize, "asc", "id");

        // Verify pagination
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(expectedPageNo, capturedPageable.getPageNumber());
        assertEquals(expectedPageSize, capturedPageable.getPageSize());
    }

    static Stream<Arguments> pagingTestCases() {
        return Stream.of(
            Arguments.of(0, 10, 0, 10),     // Normal case
            Arguments.of(2, 20, 2, 20),     // Larger page size
            Arguments.of(-1, 10, 0, 10),    // Negative page number (should default to 0)
            Arguments.of(0, 0, 0, 10),      // Zero page size (should default to 10)
            Arguments.of(0, 150, 0, 100)    // Page size exceeds max (should cap at 100)
        );
    }
}
