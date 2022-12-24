package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.SubjectDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Subject;
import com.example.webserver.repository.PlanRepository;
import com.example.webserver.repository.QuestionRepository;
import com.example.webserver.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    CustomerMapper mapper;

    public Subject putMet(Long id, Subject s) throws ResourceNotFoundException {
        Subject subject = findById(id);
        subject.setName(s.getName());
        subject.setUserId(s.getUserId());
        subject.setTodayLearned(s.getTodayLearned());
        subject.setDays(s.getDays());
        subjectRepository.save(subject);
        return subject;
    }
/*

    public Subject updateSubject(Subject s, Long id) throws ResourceNotFoundException {
        SubjectDTO dto = new SubjectDTO(s);
        dto.setId(id);
        Subject subject = findById(id);
        mapper.updateSubjectFromDto(dto, subject);
        subjectRepository.save(subject);
        return subject;
    }
*/


    public boolean deleteUser(Long subjectId) {
        if (subjectRepository.findById(subjectId).isPresent()) {
            subjectRepository.deleteById(subjectId);
            return true;
        }
        return false;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        Subject subject = findById(id);
        questionRepository.deleteAllBySubId(subject);
        planRepository.deleteAllBySubId(subject);
        subjectRepository.delete(subject);
    }
    public Subject save(Subject subject){
        return subjectRepository.save(subject);
    }
    public Subject findById(Long id) throws ResourceNotFoundException {
        return subjectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Subject not found for id:" + id + ""));
    }
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

}
