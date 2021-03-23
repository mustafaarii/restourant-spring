package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.entity.User;
import com.mustafa.restourant.repository.TableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    public final TableRepository tableRepository;

    public TableServiceImpl(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public Tables findByTableName(String tableName) {
       return tableRepository.findByTableName(tableName);
    }

    @Override
    public Tables saveTable(Tables table) {
        return tableRepository.save(table);
    }

    @Override
    public void deleteTable(int id) {
        tableRepository.deleteById(id);
    }

    @Override
    public boolean findByUserBool(User user) {
       if (tableRepository.findByUser(user)!=null) return true; else return false;
    }

    @Override
    public Tables findByUser(User user) {
        return tableRepository.findByUser(user);
    }

    @Override
    public Page<Tables> getTables(int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Tables> tables= tableRepository.findAll(pageable);
        return tables;
    }

    @Override
    public List<Tables> getAllTables() {
        return tableRepository.findAll();
    }
}
