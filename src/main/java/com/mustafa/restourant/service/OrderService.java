package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Order;
import com.mustafa.restourant.entity.Receipt;
import com.mustafa.restourant.entity.Tables;

import java.util.List;


public interface OrderService {
    void saveOrder(Order order);
    List<Order> findByTable(Tables table);
    Long deleteByReceipt(Receipt receipt);
}
