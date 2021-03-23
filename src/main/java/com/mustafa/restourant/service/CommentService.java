package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Food;
import com.mustafa.restourant.entity.FoodComment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    FoodComment saveComment(FoodComment foodComment);
    List<FoodComment> allComments();
    Page<FoodComment> findByFood(Food food,int page,int size);
}
