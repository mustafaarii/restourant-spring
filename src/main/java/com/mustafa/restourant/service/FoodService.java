package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Food;
import org.springframework.data.domain.Page;

public interface FoodService {
    Food findByFoodName(String foodName);
    Food findById(int id);
    void saveFood(Food food);
    Page<Food> allFoods(int page, int size);
    void deleteFood(int id);
    Page<Food> findByCategory(int id,int page,int size);
}
