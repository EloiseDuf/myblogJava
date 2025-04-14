package org.wildcodeschool.myblog.controller;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.wildcodeschool.myblog.dto.CategoryDTO;
import org.wildcodeschool.myblog.security.JwtAuthenticationFilter;
import org.wildcodeschool.myblog.security.JwtService;
import org.wildcodeschool.myblog.service.CategoryService;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)

class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testGetAllCategories() throws Exception {
        // Arrange
        // Arrange
        CategoryDTO categoryDto1 = new CategoryDTO();
        categoryDto1.setName("Category 1");

        CategoryDTO categoryDto2 = new CategoryDTO();
        categoryDto2.setName("Category 2");

        when(categoryService.getAllCategories()).thenReturn(List.of(categoryDto1, categoryDto2));

        // Act & Assert
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Category 1"))
                .andExpect(jsonPath("$[1].name").value("Category 2"));
    }


    @Test
    void testGetCategoryById_CategoryExists() throws Exception {
        // Arrange
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Category 1");

        when(categoryService.getCategoryById(1L)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Category 1"));
    }

    @Test
    void testGetCategoryById_CategoryNotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(99L)).thenThrow(new RuntimeException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/categories/99"))
                .andExpect(status().isNotFound());
    }
}