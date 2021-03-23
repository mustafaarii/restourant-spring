package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Food;
import com.mustafa.restourant.entity.FoodComment;
import com.mustafa.restourant.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    CommentRepository commentRepository;

    @Override
    public FoodComment saveComment(FoodComment foodComment) {
      return commentRepository.save(foodComment);
    }

    @Override
    public List<FoodComment> allComments() {
       return commentRepository.findAll();
    }

    @Override
    public Page<FoodComment> findByFood(Food food,int page,int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByFoodOrderByDateDesc(food,pageable);
    }
}
