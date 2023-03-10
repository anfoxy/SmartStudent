package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.QuestionDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CustomerMapper mapper;


    public ArrayList<Question> findAllBySubId(Subject subject){
        return questionRepository.findAllBySubId(subject);
    }

    public Question putMet(Long id, Question q) throws ResourceNotFoundException {
        Question question = findById(id);
        question.setQuestion(q.getQuestion());
        question.setAnswer(q.getAnswer());
        question.setSubId(q.getSubId());
        question.setDate(q.getDate());
        question.setPercentKnow(q.getPercentKnow());
        question.setSizeOfView(q.getSizeOfView());
        questionRepository.save(question);
        return question;
    }

    public boolean deleteQuestion(Long userId) {
        if (questionRepository.findById(userId).isPresent()) {
            questionRepository.deleteById(userId);
            return true;
        }
        return false;
    }
    public void delete(Long id) throws ResourceNotFoundException {
        Question question = findById(id);
        questionRepository.delete(question);
    }
    public ArrayList<Question> createQuestion(ArrayList<Question> question, Subject subject){
        for (Question q : question) {
            q.setSubId(subject);
            save(q);
        }
        return question;
    }
    public Question save(Question question){
        return questionRepository.save(question);
    }
    public Question findById(Long id) throws ResourceNotFoundException {
        return questionRepository.findById(id).orElse(null);
    }
    public List<Question> findAll() {
        return questionRepository.findAll();
    }
    @Transactional
    public void deleteAllBySubId(Subject subject) {
        System.out.println("qqqqq== "+findAllBySubId(subject));
        if(!findAllBySubId(subject).isEmpty()) questionRepository.deleteAllBySubId(subject);
    }
}
