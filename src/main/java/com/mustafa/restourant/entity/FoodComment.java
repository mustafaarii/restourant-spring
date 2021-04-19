package com.mustafa.restourant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "food_comments")
public class FoodComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("reservations")
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    @JsonIgnore
    private Food food;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String comment;

    @Column
    private Date date =new Date(System.currentTimeMillis());

    public FoodComment(){ }

    public FoodComment(User user, Food food, String comment) {
        this.user = user;
        this.food = food;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}

