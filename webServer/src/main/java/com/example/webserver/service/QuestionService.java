package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.QuestionDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Question;
import com.example.webserver.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CustomerMapper mapper;


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

  /*  public Question updateQuestion(Question d, Long id) throws ResourceNotFoundException {
        QuestionDTO dto = new QuestionDTO(d);
        dto.setId(id);
        Question question = findById(id);
        mapper.updateQuestionFromDto(dto, question);
        questionRepository.save(question);
        return question;
    }*/

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
    public Question save(Question question){
        return questionRepository.save(question);
    }
    public Question findById(Long id) throws ResourceNotFoundException {
        return questionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Question not found for id:" + id.toString() + ""));
    }
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

}
