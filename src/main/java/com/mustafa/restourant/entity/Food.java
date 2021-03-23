package com.mustafa.restourant.entity;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "foods")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String image;

    @Column(name = "is_active",nullable = false)
    private boolean isActive=true;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<FoodComment> comments;

    public Food() { }

    public Food(String foodName, int price, String image,Category category) {
        this.foodName = foodName;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
