package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Order;
import com.mustafa.restourant.entity.Receipt;
import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    OrderRepository orderRepository;

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> findByTable(Tables table) {
        return orderRepository.findByTable(table);
    }


    @Override
    public Long deleteByReceipt(Receipt receipt) {
      return orderRepository.deleteByReceipt(receipt);
    }

}
