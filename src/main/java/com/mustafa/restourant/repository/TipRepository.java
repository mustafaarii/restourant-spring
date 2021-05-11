package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Tip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipRepository extends JpaRepository<Tip,Integer> {

}
