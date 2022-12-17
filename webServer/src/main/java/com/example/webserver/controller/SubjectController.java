package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.model.SubjectQuestion;
import com.example.webserver.repository.QuestionRepository;
import com.example.webserver.repository.SubjectRepository;
import com.example.webserver.service.SubjectService;
import com.example.webserver.service.UserService;
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
    QuestionRepository questionRepository;

    @Autowired
    UserService userService;

    @GetMapping("/subjects")
    public List<Subject> getAllSubject() {
        return subjectService.findAll();
    }
    @Transactional
    @GetMapping("/subjects/{id}")
    public SubjectQuestion getSubjectById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
   /*     Subject subject = new Subject(subjectService.findById(id));
        ArrayList<Question> q = questionRepository.findAllBySubId(subject);
        for (Question question :q) {
            question = new Question(question);
            question.setSubId(null);
        }
        subject.setQuestions(q);
        return subject;*/
        Subject subject = new Subject(subjectService.findById(id));
        ArrayList<Question> q = questionRepository.findAllBySubId(subject);
        return new SubjectQuestion(subject, q);
    }
    @Transactional
    @GetMapping("subjects/byUser/{id}")
    public ArrayList<SubjectQuestion> getSubjectByUser(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        ArrayList<Subject> subjectArrayList = subjectRepository.findAllByUserId(userService.findById(id));
        ArrayList<SubjectQuestion> subjectQuestion = new ArrayList<>();
        for (int i = 0; i < subjectArrayList.size(); i++) {
            ArrayList<Question> q = questionRepository.findAllBySubId(subjectArrayList.get(i));
            subjectQuestion.add(new SubjectQuestion(subjectArrayList.get(i),q));
        }
        return subjectQuestion;
    }

    @PostMapping("/subjects")
    public Subject createSubject(@RequestBody Subject subject){

        System.out.println("Предмет" + subject);

        return  subjectService.save(subject);
    }

    @DeleteMapping("/subjects/{id}")
    public Subject deleteSubject(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Subject s =  subjectService.findById(id);
        subjectService.delete(id);
        return s;
    }

    @PatchMapping(value = "/subjects/{id}")
    public Subject patchSubject(@PathVariable Long id, @RequestBody Subject req) throws ResourceNotFoundException {
        return subjectService.updateSubject(req,id);
    }

    @PutMapping("/subjects/{id}")
    public Subject putSubject(@PathVariable Long id,@RequestBody Subject req) throws ResourceNotFoundException {
        return subjectService.putMet(id,req);
    }

}















