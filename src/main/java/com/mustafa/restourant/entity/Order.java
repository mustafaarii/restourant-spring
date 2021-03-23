package com.mustafa.restourant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "table_id",nullable = false)
    @JsonIgnore
    private Tables table;

    @ManyToOne
    @JoinColumn(name = "food_id",nullable = false)
    private Food food;

    @Column(nullable = false)
    private int count;

    @ManyToOne
    @JoinColumn(name = "receipt_id",nullable = false)
    private Receipt receipt;

    public Order(){}

    public Order(Tables table, Food food, int count,Receipt receipt) {
        this.table = table;
        this.food = food;
        this.count = count;
        this.receipt = receipt;
    }

    public Tables getTable() {
        return table;
    }

    public void setTable(Tables table) {
        this.table = table;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }
}
