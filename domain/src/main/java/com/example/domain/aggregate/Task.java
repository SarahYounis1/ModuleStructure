package com.example.domain.aggregate;

import java.util.Date;

public class Task {

    Long id;
    Date startDate;
    Date endDate;
    String description;
    Boolean completed;

    public Task(String description, boolean completed, Date starter, Date endDate) {
        this.description =description;
        this.completed=completed;
        this.startDate=starter;
        this.endDate=endDate;
    }
    public Task(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {return startDate;}

    public void setStartDate(Date startDate) {this.startDate = startDate;}

    public Date getEndDate() {return endDate;}

    public void setEndDate(Date endDate) {this.endDate = endDate;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getCompleted() {
        return completed;
    }

}
