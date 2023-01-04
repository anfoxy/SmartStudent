package com.example.webserver;

import com.example.webserver.model.User;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Contacts API",version = "1.0",description = "book shop web service"))
public class WebServerApplication {

    public static void main(String[] args) {
       SpringApplication.run(WebServerApplication.class, args);
    }
}
