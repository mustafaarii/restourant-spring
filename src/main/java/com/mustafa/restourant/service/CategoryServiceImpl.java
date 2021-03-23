package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Category;
import com.mustafa.restourant.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public boolean isExistCategoryByName(String name) {
        if (categoryRepository.findByName(name)!=null) return true; else return false;
    }

    @Override
    public Category findById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void saveCategory(Category category) { categoryRepository.save(category);}

    @Override
    public List<Category> allCategories() { return categoryRepository.findAll(); }

    @Override
    public void deleteCategory(int id) { categoryRepository.deleteById(id); }
}
