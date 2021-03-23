package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Food;
import com.mustafa.restourant.entity.FoodComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<FoodComment,Integer> {
    Page<FoodComment> findByFoodOrderByDateDesc(Food food, Pageable pageable);
}
