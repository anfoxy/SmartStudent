package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class PlanController {

    @Autowired
    PlanService planService;


    @GetMapping("/plan")
    public List<Plan> getAllQuestion() {
        return planService.findAll();
    }

    @GetMapping("/plan/{id}")
    public Plan getQuestionById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return planService.findById(id);
    }

    @PostMapping("/plan")
    public Plan createQuestion(@RequestBody Plan plan){
        System.out.println("Вопрос "+ plan);
        return  planService.save(plan);
    }

    @DeleteMapping("/plan/{id}")
    public Plan deleteQuestion(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Plan q =  planService.findById(id);
        planService.delete(id);
        return q;
    }

    @PutMapping("/plan/{id}")
    public Plan putUser(@PathVariable Long id,@RequestBody Plan req) throws ResourceNotFoundException {
        System.out.println(req);
        return planService.putMet(id,req);
    }

}














