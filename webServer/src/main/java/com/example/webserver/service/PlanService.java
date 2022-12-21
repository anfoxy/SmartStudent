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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public void removeDatesAfterToday(ArrayList<Plan> dates) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH) + 1;
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        System.out.println("todayYear = " + todayYear + " todayMonth = " + todayMonth + " todayDay = " + todayDay);
        for (Plan plan : dates) {
            String[] parts = plan.getDate().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) + 1;
            int day = Integer.parseInt(parts[2]);
            System.out.println("todayYear = " + year + " todayMonth = " + month + " todayDay = " + day);
            if (year > todayYear) {
                System.out.println("yes");
                planRepository.delete(plan);

            } else {
                if (year == todayYear && month > todayMonth) {
                    System.out.println("yes");
                    planRepository.delete(plan);

                } else {
                    if (year == todayYear && month == todayMonth && day >= todayDay) {
                        System.out.println("yes");
                        planRepository.delete(plan);
                    }
                }
            }
        }
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
