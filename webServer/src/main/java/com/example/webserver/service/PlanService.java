package com.example.webserver.service;

import com.example.webserver.dto.QuestionDTO;
import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.repository.PlanRepository;
import com.example.webserver.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {
    @Autowired
    PlanRepository planRepository;

    @Autowired
    CustomerMapper mapper;


    public Plan putMet(Long id, Plan res) throws ResourceNotFoundException {
        Plan plan = findById(id);
        plan.setDate(res.getDate());
        plan.setSubId(res.getSubId());
        plan.setNumberOfQuestions(res.getNumberOfQuestions());
        planRepository.save(plan);
        return plan;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        Plan plan = findById(id);
        planRepository.delete(plan);
    }
    public Plan save(Plan plan){
        return planRepository.save(plan);
    }
    public Plan findById(Long id) throws ResourceNotFoundException {
        return planRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Plan not found for id:" + id.toString() + ""));
    }
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

}
