package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface FoodRepository extends JpaRepository<Food,Integer> {
    Food findByFoodName(String foodName);

    @Query(
            value = "SELECT * FROM foods f WHERE f.category_id = ?1",
            nativeQuery = true)
    Page<Food> findByCategory(int id, Pageable pageable);
    Page<Food> findByFoodNameContaining(String name,Pageable pageable);
}
