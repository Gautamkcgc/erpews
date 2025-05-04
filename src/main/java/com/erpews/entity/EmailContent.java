package com.erpews.entity;

import jakarta.persistence.*;

@Entity
@Table(name= "EmailContent")
public class EmailContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Explicitly mapping id column, though it's often inferred
    private int id;


    private String title;

    // Use double quotes for reserved SQL keyword
    private String from;


    private String cc;

    // Use double quotes for reserved SQL keyword
    private String to;

    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
