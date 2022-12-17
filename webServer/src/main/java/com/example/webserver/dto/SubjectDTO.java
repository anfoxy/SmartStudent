package com.example.webserver.dto;


import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import lombok.Getter;

@Getter
public class SubjectDTO {

    private Long id;

    private String name;

    private User userId;

    private String days;
    private Boolean completed;


    public SubjectDTO(Long id, String name, User userId,String days, Boolean completed) {
        this.id = id;
        this.days = days;
        this.name = name;
        this.userId = userId;
        this.completed = completed;
    }

    public SubjectDTO(Subject c) {
        this.id = c.getId();
        this.name = c.getName();
        this.days = c.getDays();
        this.userId = c.getUserId();
        this.completed = c.getCompleted();

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subject convertToEntity() {
        Subject c = new Subject();
        c.setId(id);
        c.setName(name);
        c.setDays(days);
        c.setUserId(userId);
        c.setCompleted(completed);
        return c;
    }
}
