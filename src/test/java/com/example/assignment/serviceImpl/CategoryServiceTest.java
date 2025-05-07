package com.example.assignment.serviceImpl;

import com.example.assignment.dto.request.CategoryCreationReq;
import com.example.assignment.dto.request.CategoryFilterReq;
import com.example.assignment.dto.response.CategoryRes;
import com.example.assignment.dto.response.CategoryTreeRes;
import com.example.assignment.entity.Category;
import com.example.assignment.exception.ResourceConflictException;
import com.example.assignment.exception.ResourceNotFoundException;
import com.example.assignment.mapper.CategoryMapper;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    @DisplayName("test get top level categories")
    void testGetTopLevelCategories() {
        // setup mock data
        List<Category> categoryList = new ArrayList<>();
        List<CategoryRes> categoryResList = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            Category category = new Category();
            category.setId((long) i);
            category.setName("Category " + i);
            categoryList.add(category);
            CategoryRes categoryRes = CategoryRes.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();
            categoryResList.add(categoryRes);
        }

        // setup stub behavior
        when(categoryRepository.findCategoriesByParentIsNull()).thenReturn(categoryList);
        when(categoryMapper.toDtos(categoryList)).thenReturn(categoryResList);
        // call the method under test
        List<CategoryRes> result = categoryService.getTopLevelCategories();
        // verify the result

        assertNotNull(result);
        assertEquals(categoryList.size(), result.size());

        for (int i = 0; i < categoryList.size(); i++) {
            assertEquals(categoryList.get(i).getId(), result.get(i).getId());
            assertEquals(categoryList.get(i).getName(), result.get(i).getName());
        }
    }

    @Test
    @DisplayName("test create category")
    void testCreateCategory() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build();

        CategoryRes categoryRes = CategoryRes.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();

        // setup stub behavior
        when(categoryMapper.toEntity(categoryCreationReq)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryRes);

        // call the method under test
        CategoryRes result = categoryService.createCategory(categoryCreationReq);

        // verify the result
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
    }

    @Test
    @DisplayName("Test create category with parentId")
    void testCreateCategoryWithParentId() {
        // setup mock data
        Category parentCategory = new Category();
        parentCategory.setId(2L);
        parentCategory.setName("Parent Category");
        parentCategory.setDescription("Parent Description");

        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");
        category.setParent(parentCategory);

        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name(category.getName())
                .description(category.getDescription())
                .parentId(2L)
                .build();

        CategoryRes categoryRes = CategoryRes.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(parentCategory.getId())
                .build();

        // setup stub behavior
        when(categoryMapper.toEntity(categoryCreationReq)).thenReturn(category);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryRes);

        // call the method under test
        CategoryRes result = categoryService.createCategory(categoryCreationReq);

        // verify the result
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
        assertEquals(category.getParent().getId(), result.getParentId());
    }

    @Test
    @DisplayName("Test create category with invalid parentId")
    void testCreateCategoryWithInvalidParentId() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name(category.getName())
                .description(category.getDescription())
                .parentId(999L)
                .build();

        // setup stub behavior
        when(categoryMapper.toEntity(categoryCreationReq)).thenReturn(category);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // call the method under test
        assertThrows(ResourceNotFoundException.class, () -> categoryService.createCategory(categoryCreationReq));

        // verify the result
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Test update category")
    void testUpdateCategory() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        CategoryRes categoryRes = CategoryRes.builder()
                .id(category.getId())
                .name(categoryCreationReq.getName())
                .description(categoryCreationReq.getDescription())
                .build();

        // setup stub behavior
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryRes);

        // call the method under test
        CategoryRes result = categoryService.updateCategoryById(1L, categoryCreationReq);

        // verify the result
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals("Updated Category", result.getName());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    @DisplayName("Test update category with parentId")
    void testUpdateCategoryWithParentId() {
        // setup mock data
        Category parentCategory = new Category();
        parentCategory.setId(2L);
        parentCategory.setName("Parent Category");
        parentCategory.setDescription("Parent Description");

        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");
        category.setParent(parentCategory);

        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name(category.getName())
                .description(category.getDescription())
                .parentId(2L)
                .build();

        CategoryRes categoryRes = CategoryRes.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(parentCategory.getId())
                .build();

        // setup stub behavior
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryRes);

        // call the method under test
        CategoryRes result = categoryService.updateCategoryById(1L, categoryCreationReq);

        // verify the result
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
        assertEquals(category.getParent().getId(), result.getParentId());
    }

    @Test
    @DisplayName("Test update category with parentId equal to categoryId")
    void testUpdateCategoryWithInvalidParentId() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name(category.getName())
                .description(category.getDescription())
                .parentId(1L)
                .build();

        // setup stub behavior

        // call the method under test
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategoryById(1L, categoryCreationReq));

        // verify the result
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("test update category with non-exist categoryId")
    void testUpdateCategoryWithNonExistCategoryId()  {
        // setup mock data
        CategoryCreationReq categoryCreationReq = CategoryCreationReq.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        // setup stub behavior
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // call the method under test
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategoryById(1L, categoryCreationReq));

        // verify the result
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("test delete category")
    void testDeleteCategory() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findCategoriesByParent_Id(1L)).thenReturn(List.of());

        // call the method under test
        categoryService.deleteCategoryById(1L);

        // verify the result
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("test delete category with non-exist categoryId")
    void testDeleteCategoryWithNonExistCategoryId() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // call the method under test
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(1L));

        // verify the result
        verify(categoryRepository, never()).findCategoriesByParent_Id(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("test delete category with child categories")
    void testDeleteCategoryWithChildCategories() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findCategoriesByParent_Id(1L)).thenReturn(List.of(new Category()));

        // call the method under test
        assertThrows(ResourceConflictException.class, () -> categoryService.deleteCategoryById(1L));

        // verify the result
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("test force delete category")
    void testForceDeleteCategory() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // call the method under test
        categoryService.forceDeleteCategoryById(1L);

        // verify the result
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("test force delete category with non-exist categoryId")
    void testForceDeleteCategoryWithNonExistCategoryId() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // call the method under test
        assertThrows(ResourceNotFoundException.class, () -> categoryService.forceDeleteCategoryById(1L));

        // verify the result
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("test get category by id")
    void testGetCategoryById() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // call the method under test
        CategoryRes result = categoryService.getCategoryById(1L);

        // verify the result
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
    }

    @Test
    @DisplayName("test get category by id with non-exist categoryId")
    void testGetCategoryByIdWithNonExistCategoryId() {
        // setup mock data
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");

        // setup stub behavior
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // call the method under test
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));

        // verify the result
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test getAllCategories with no filters")
    void testGetAllCategoriesNoFilters() {
        // Setup mock data
        List<Category> categoryList = List.of(
            createCategory(1L, "Category 1", "Description 1", null),
            createCategory(2L, "Category 2", "Description 2", null)
        );
        List<CategoryRes> categoryResList = List.of(
            createCategoryRes(1L, "Category 1", "Description 1", null),
            createCategoryRes(2L, "Category 2", "Description 2", null)
        );

        CategoryFilterReq filterReq = CategoryFilterReq.builder()
            .name(null)
            .description(null)
            .parentId(null)
            .build();

        // Setup stubs
        when(categoryRepository.findAll(any(Specification.class))).thenReturn(categoryList);
        when(categoryMapper.toDtos(categoryList)).thenReturn(categoryResList);

        // Execute
        List<CategoryRes> result = categoryService.getAllCategories(filterReq);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(categoryResList, result);
    }

    @Test
    @DisplayName("Test getAllCategories with name filter")
    void testGetAllCategoriesWithNameFilter() {
        // Setup mock data
        List<Category> categoryList = List.of(
            createCategory(1L, "Test Category", "Description 1", null)
        );
        List<CategoryRes> categoryResList = List.of(
            createCategoryRes(1L, "Test Category", "Description 1", null)
        );

        CategoryFilterReq filterReq = CategoryFilterReq.builder()
            .name("Test")
            .build();

        // Setup stubs with specification capture
        ArgumentCaptor<Specification<Category>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(categoryRepository.findAll(specCaptor.capture())).thenReturn(categoryList);
        when(categoryMapper.toDtos(categoryList)).thenReturn(categoryResList);

        // Execute
        List<CategoryRes> result = categoryService.getAllCategories(filterReq);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());

        // The specification itself is difficult to verify due to its functional nature,
        // but we can verify it was called with some specification
        verify(categoryRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Test getAllCategories with parentId filter")
    void testGetAllCategoriesWithParentIdFilter() {
        // Setup mock data
        Category parent = createCategory(1L, "Parent", "Parent Desc", null);
        List<Category> categoryList = List.of(
            createCategory(2L, "Child 1", "Child Desc 1", parent),
            createCategory(3L, "Child 2", "Child Desc 2", parent)
        );
        List<CategoryRes> categoryResList = List.of(
            createCategoryRes(2L, "Child 1", "Child Desc 1", 1L),
            createCategoryRes(3L, "Child 2", "Child Desc 2", 1L)
        );

        CategoryFilterReq filterReq = CategoryFilterReq.builder()
            .parentId(1L)
                .build();

        // Setup stubs
        when(categoryRepository.findAll(any(Specification.class))).thenReturn(categoryList);
        when(categoryMapper.toDtos(categoryList)).thenReturn(categoryResList);

        // Execute
        List<CategoryRes> result = categoryService.getAllCategories(filterReq);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getParentId());
        assertEquals(1L, result.get(1).getParentId());
    }

    @Test
    @DisplayName("Test getAllCategories with multiple filters")
    void testGetAllCategoriesWithMultipleFilters() {
        // Setup mock data
        Category parent = createCategory(1L, "Parent", "Parent Desc", null);
        List<Category> categoryList = List.of(
            createCategory(2L, "Test Child", "Test Description", parent)
        );
        List<CategoryRes> categoryResList = List.of(
            createCategoryRes(2L, "Test Child", "Test Description", 1L)
        );

        CategoryFilterReq filterReq = CategoryFilterReq.builder()
            .name("Test")
            .parentId(1L)
            .description("Test")
            .build();

        // Setup stubs
        when(categoryRepository.findAll(any(Specification.class))).thenReturn(categoryList);
        when(categoryMapper.toDtos(categoryList)).thenReturn(categoryResList);

        // Execute
        List<CategoryRes> result = categoryService.getAllCategories(filterReq);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Child", result.getFirst().getName());
        assertEquals("Test Description", result.getFirst().getDescription());
        assertEquals(1L, result.getFirst().getParentId());
    }

    @Test
    @DisplayName("Test getCategoryTree")
    void testGetCategoryTree() {
        // Setup mock data
        Category parent1 = createCategory(1L, "Parent1", "Parent 1 Desc", null);
        Category parent2 = createCategory(2L, "Parent2", "Parent 2 Desc", null);
        Category half1 = createCategory(3L, "half 1", "half Desc 1", parent1);
        Category half2 = createCategory(4L, "half 2", "half Desc 2", parent2);
        Category child1 = createCategory(5L, "Child 1", "Child Desc 1", parent1);
        Category child2 = createCategory(6L, "Child 2", "Child Desc 2", parent2);
        Category child3 = createCategory(7L, "Child 3", "Child Desc 3", half1);
        Category child4 = createCategory(8L, "Child 4", "Child Desc 4", half2);
        parent1.setSubCategories(List.of(half1, child1));
        parent2.setSubCategories(List.of(half2, child2));
        half1.setSubCategories(List.of(child3));
        half2.setSubCategories(List.of(child4));

        // Setup stubs
        when(categoryRepository.findCategoriesByParent_Id(null)).thenReturn(List.of(parent1, parent2));

        // Execute
        List<CategoryTreeRes> result = categoryService.getCategoryTree();

        // Verify
        List<CategoryTreeRes> half1Result = result.getFirst().getSubCategories();
        List<CategoryTreeRes> child1Result = half1Result.get(1).getSubCategories();
        assertNotNull(result);
        assertEquals("Parent1", result.getFirst().getName());
        assertEquals(2, half1Result.size());
        assertEquals(List.of(), child1Result);
    }

    @Test
    @DisplayName("Test getCategoryById")
    void testGetCategoryByParentId() {
        Category half1 = createCategory(3L, "half 1", "half Desc 1", null);
        Category child3 = createCategory(7L, "Child 3", "Child Desc 3", half1);
        half1.setSubCategories(List.of(child3));

        CategoryTreeRes childCategoryTreeRes = CategoryTreeRes.builder()
            .id(child3.getId())
            .name(child3.getName())
            .description(child3.getDescription())
            .subCategories(List.of())
            .build();

        CategoryTreeRes categoryTreeRes = CategoryTreeRes.builder()
            .id(half1.getId())
            .name(half1.getName())
            .description(half1.getDescription())
            .subCategories(List.of(childCategoryTreeRes))
            .build();

        // Setup stubs
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(half1));

        // Execute
        List<CategoryTreeRes> result = categoryService.getCategoryTreeById(3L);

        // Verify
        assertNotNull(result);
        assertEquals("half 1", result.getFirst().getName());
        assertEquals(categoryTreeRes.getSubCategories(), result.getFirst().getSubCategories());
    }

    @Test
    @DisplayName("Test getCategoryTreeById with invalid id")
    void testGetCategoryTreeByIdWithInvalidId() {
        // Setup stubs
        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryTreeById(3L));

        
    }

    // Helper methods
    private Category createCategory(Long id, String name, String description, Category parent) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        category.setSubCategories(new ArrayList<>());  // Initialize with empty list
        return category;
    }

    private CategoryRes createCategoryRes(Long id, String name, String description, Long parentId) {
        return CategoryRes.builder()
            .id(id)
            .name(name)
            .description(description)
            .parentId(parentId)
            .build();
    }
}
