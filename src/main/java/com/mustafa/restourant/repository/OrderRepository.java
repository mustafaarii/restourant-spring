package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Order;
import com.mustafa.restourant.entity.Receipt;
import com.mustafa.restourant.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByTable(Tables table);
    @Transactional
    Long deleteByReceipt(Receipt receipt);
}
