package com.mustafa.restourant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "site_comments")
public class SiteComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JsonIgnoreProperties("reservations")
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date(System.currentTimeMillis());

    public SiteComment(){ }

    public SiteComment(User user, String comment) {
        this.user = user;
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public int getId() {
        return id;
    }
}
