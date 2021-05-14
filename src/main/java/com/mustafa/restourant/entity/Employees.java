package com.mustafa.restourant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int totalTip=0;
    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Tip> tips;

    public Employees(){}

    public Employees(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTip() {
        return totalTip;
    }

    public void setTotalTip(int totalTip) {
        this.totalTip = totalTip;
    }

    public List<Tip> getTips() {
        return tips;
    }

    public void setTips(Tip tip) {
        this.tips.add(tip);
        this.setTotalTip(this.getTotalTip()+tip.getTip());
    }
}
