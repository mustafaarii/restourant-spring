package com.mustafa.restourant.entity;

import javax.persistence.*;

@Entity
@Table(name = "tips")
public class Tip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "employee_id",nullable = false)
    @ManyToOne
    private Employees employee;

    @Column(nullable = false)
    private int tip;

    public Tip(){ }

    public Tip(int tip) {
        this.tip = tip;
    }

    public Tip(User user, Employees employee, int tip) {
        this.user = user;
        this.employee = employee;
        this.tip = tip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public Employees getEmployee() {
        return employee;
    }

    public void setEmployee(Employees employee) {
        this.employee = employee;
    }
}
