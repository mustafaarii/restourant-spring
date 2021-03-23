package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category,Integer> {
   Category findByName(String name);
   Category findById(int id);
}
