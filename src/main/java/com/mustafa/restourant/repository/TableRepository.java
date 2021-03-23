package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<Tables,Integer> {
    Tables findByTableName(String tableName);
    Tables findByUser(User user);
}
