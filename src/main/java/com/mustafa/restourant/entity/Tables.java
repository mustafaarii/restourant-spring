package com.mustafa.restourant.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tables")
public class Tables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "table_name")
    private String tableName;

    @OneToOne()
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(mappedBy = "table",cascade = CascadeType.ALL)
    private List<Order> orders;

    public Tables() { }

    public Tables(String tableName) {
        this.tableName = tableName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
