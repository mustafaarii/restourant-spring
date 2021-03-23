package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Category;

import java.util.List;

public interface CategoryService{
    boolean isExistCategoryByName(String name);
    Category findById(int id);
    void saveCategory(Category category);
    List<Category> allCategories();
    void deleteCategory(int id);
}
