package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.model.SubjectQuestion;
import com.example.webserver.repository.PlanRepository;
import com.example.webserver.repository.QuestionRepository;
import com.example.webserver.repository.SubjectRepository;
import com.example.webserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SubjectController {

    @Autowired
    SubjectService subjectService;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    PlanService planService;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    UserService userService;
    @Autowired
    DeleteSercice deleteSercice;
    @GetMapping("/subjects")
    public List<Subject> getAllSubject() {
        return subjectService.findAll();
    }
    @Transactional
    @GetMapping("/subjects_question/{id}")
    public SubjectQuestion getSubjectByIdQuestion(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {

        Subject subject = new Subject(subjectService.findById(id));
        ArrayList<Question> q = questionRepository.findAllBySubId(subject);
        ArrayList<Plan> p = planRepository.findAllBySubId(subject);
        return new SubjectQuestion(subject, q,p);
    }
    @GetMapping("/subjects/{id}")
    public SubjectQuestion getSubjectAndPlanById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Subject subject = new Subject(subjectService.findById(id));
        ArrayList<Plan> p = planRepository.findAllBySubId(subject);
        return new SubjectQuestion(subject,null,p);
    }
    @Transactional
    @GetMapping("subjects/byUser/{id}")
    public ArrayList<SubjectQuestion> getSubjectByUser(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        ArrayList<Subject> subjectArrayList = subjectRepository.findAllByUserId(userService.findById(id));
        ArrayList<SubjectQuestion> subjectQuestion = new ArrayList<>();
        for (Subject subject : subjectArrayList) {
            ArrayList<Question> q = questionRepository.findAllBySubId(subject);
            ArrayList<Plan> p = planRepository.findAllBySubId(subject);
            subjectQuestion.add(new SubjectQuestion(subject, q, p));
        }
        System.out.println(subjectQuestion);
        return subjectQuestion;
    }

    @PostMapping("/subjects")
    public Subject createSubject(@RequestBody Subject subject){
       Subject s = subjectService.save(subject);
       planService.createPlans((ArrayList<Plan>) s.getPlans(),s);
       questionService.createQuestion((ArrayList<Question>) s.getQuestions(),s);
       return s;
    }
    @Transactional
    @DeleteMapping("/subjects/{id}")
    public Subject deleteSubject(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Subject s =  subjectService.findById(id);
        deleteSercice.deleteSub(id);
        return s;
    }

    @PutMapping("/subjects/{id}")
    public Subject putSubject(@PathVariable Long id,@RequestBody Subject req) throws ResourceNotFoundException {
        return subjectService.putMet(id,req);
    }

}















