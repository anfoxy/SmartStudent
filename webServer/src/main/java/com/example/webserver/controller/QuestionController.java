package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Question;
import com.example.webserver.respons.DeleteResponse;
import com.example.webserver.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class QuestionController {

    @Autowired
    QuestionService questionService;


    @GetMapping("/questions")
    public List<Question> getAllQuestion() {
        return questionService.findAll();
    }

    @GetMapping("/questions/{id}")
    public Question getQuestionById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return questionService.findById(id);
    }

    @PostMapping("/questions")
    public Question createQuestion(@RequestBody Question question){
        System.out.println("Вопрос "+ question);
        return  questionService.save(question);
    }

    @DeleteMapping("/questions/{id}")
    public Question deleteQuestion(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Question q =  questionService.findById(id);
        questionService.delete(id);
        return q;
    }
  /*  @PatchMapping(value = "/questions/{id}")
    public Question patchUser(@PathVariable Long id, @RequestBody Question question) throws ResourceNotFoundException {
        return questionService.updateQuestion(question,id);
    }
*/
    @PutMapping("/questions/{id}")
    public Question putQuestion(@PathVariable Long id,@RequestBody Question req) throws ResourceNotFoundException {
        System.out.println(req);
        return questionService.putMet(id,req);
    }

}














