package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TableService {

    Tables findByTableName(String tableName);
    Tables saveTable(Tables table);
    Tables findById(int id);
    void deleteTable(int id);
    boolean findByUserBool(User user);
    Tables findByUser(User user);
    Page<Tables> getTables(int page,int size);
    List<Tables> getAllTables();
}
