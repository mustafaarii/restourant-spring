package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Food;
import com.mustafa.restourant.repository.FoodRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FoodServiceImpl implements FoodService{
    @Autowired
    FoodRepository foodRepository;

    @Override
    public Food findByFoodName(String foodName) {
        return foodRepository.findByFoodName(foodName);
    }

    @Override
    public Food findById(int id) {
        return foodRepository.findById(id).get();
    }

    @Override
    public void saveFood(Food food) {
        foodRepository.save(food);
    }

    @Override
    public Page<Food> allFoods(int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
       Page<Food> foods = foodRepository.findAll(pageable);
       return foods;
    }

    @Override
    public void deleteFood(int id) {
        foodRepository.deleteById(id);
    }

    @Override
    public Page<Food> findByCategory(int id,int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Food> foods = foodRepository.findByCategory(id,pageable);
        return foods;
    }
}
