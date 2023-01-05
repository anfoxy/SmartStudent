package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.repository.PlanRepository;
import com.example.webserver.repository.SubjectRepository;
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

    @Autowired
    SubjectRepository subjectRepository;

    @GetMapping("/plan")
    public List<Plan> getAllPlan() {
        return planService.findAll();
    }

    @GetMapping("/plan/{id}")
    public Plan getPlanById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return planService.findById(id);
    }

    @PostMapping("/plan")
    public  ArrayList<Plan> createPlans(@RequestBody ArrayList<Plan> plan){
        return !plan.isEmpty() ? planService.createPlans(plan, plan.get(0).getSubId()) : null;
    }

    @PostMapping("/plans/{id}")
    public ArrayList<Plan> updatePlans(@PathVariable(value = "id") Long id,@RequestBody ArrayList<Plan> plan) throws ResourceNotFoundException {
        return planService.updatePlans(plan,id);
    }
    @DeleteMapping("/plan/{id}")
    public Plan deletePlan(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Plan q =  planService.findById(id);
        planService.delete(id);
        return q;
    }

    @PutMapping("/plan/{id}")
    public Plan putPlan(@PathVariable Long id,@RequestBody Plan req) throws ResourceNotFoundException {
        System.out.println(req);
        return planService.putMet(id,req);
    }

}














