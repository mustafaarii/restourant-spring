package com.mustafa.restourant.entity;

import javax.persistence.*;

@Entity
@Table(name = "tips")
public class Tip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @JoinColumn(name = "employee_id")
    @ManyToOne
    private Employees employee;

    private int tip;

    public Tip(){ }

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


}
