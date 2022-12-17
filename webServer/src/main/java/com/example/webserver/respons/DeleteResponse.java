package com.example.webserver.respons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteResponse {


    private String message;
    public DeleteResponse(String message) {
        this.message = message;
    }

}