package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.repository.PlanRepository;
import com.example.webserver.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@RestController

public class PlanController {

    @Autowired
    PlanService planService;

    @Autowired
    PlanRepository planRepository;

    @GetMapping("/plan")
    public List<Plan> getAllQuestion() {
        return planService.findAll();
    }

    @GetMapping("/plan/{id}")
    public Plan getQuestionById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return planService.findById(id);
    }

    @PostMapping("/plan")
    public  ArrayList<Plan> createPlans(@RequestBody ArrayList<Plan> plan){
        for (Plan p : plan) planService.save(p);
        return plan;
    }

    @PostMapping("/plans")
    public ArrayList<Plan> updatePlans(@RequestBody ArrayList<Plan> plan){
        System.out.println("план изначально "+plan);
        if(plan.size()>0) {
            ArrayList<Plan> plansBD = planRepository.findAllBySubId(plan.get(0).getSubId());
            System.out.println("план bd "+plansBD);
            planService.removeDatesAfterToday(plansBD);
            for (Plan p : plan) planService.save(p);
        }
        return plan;
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














