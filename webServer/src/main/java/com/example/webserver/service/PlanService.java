package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Subject;
import com.example.webserver.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PlanService {
    @Autowired
    PlanRepository planRepository;
    @Autowired
    SubjectService subjectService;

    @Autowired
    CustomerMapper mapper;


    public Plan putMet(Long id, Plan res) throws ResourceNotFoundException {
        Plan plan = findById(id);
        plan.setDate(res.getDate());
        plan.setSubId(res.getSubId());
        plan.setNumberOfQuestions(res.getNumberOfQuestions());
        plan.setBoolDate(res.isBoolDate());
        planRepository.save(plan);
        return plan;
    }

    public ArrayList<Plan> findAllBySubId(Subject subject){
        return planRepository.findAllBySubId(subject);
    }

    private void removeDatesAfterToday(ArrayList<Plan> dates) {
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

    public ArrayList<Plan> createPlans(ArrayList<Plan> plan, Subject subject) {
        for (Plan p : plan) {
            p.setSubId(subject);
            save(p);
        }
        return plan;
    }

   public ArrayList<Plan>  updatePlans(ArrayList<Plan> plan, Long id) throws ResourceNotFoundException {


       Subject subject = subjectService.findById(id);

       if(plan.size()>0) {
           ArrayList<Plan> plansBD = planRepository.findAllBySubId(subject);
           removeDatesAfterToday(plansBD);
           for (Plan p : plan) {
               p.setSubId(subject);
               save(p);
           }
       }
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
    @Transactional
    public void deleteAllBySubId(Subject subject) {
        if(!findAllBySubId(subject).isEmpty())  planRepository.deleteAllBySubId(subject);
    }
}
