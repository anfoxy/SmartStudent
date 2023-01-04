package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.SubjectDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import com.example.webserver.repository.PlanRepository;
import com.example.webserver.repository.QuestionRepository;
import com.example.webserver.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    CustomerMapper mapper;

    public Subject putMet(Long id, Subject s) throws ResourceNotFoundException {
        Subject subject = findById(id);
        if(subject != null) {
            subject.setId(s.getId());
            subject.setName(s.getName());
            subject.setUserId(s.getUserId());
            subject.setTodayLearned(s.getTodayLearned());
            subject.setDays(s.getDays());
            return subjectRepository.save(subject);
        }else {
            s.setId(null);
           return subjectRepository.save(s);
        }
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

    public ArrayList<Subject> findAllByUserId(User user){
        return subjectRepository.findAllByUserId(user);
    }



    public boolean deleteUser(Long subjectId) {
        if (subjectRepository.findById(subjectId).isPresent()) {
            subjectRepository.deleteById(subjectId);
            return true;
        }
        return false;
    }

    public int numberOfDuplicateSubjects(Subject subject){
      ArrayList<Subject> names =  findAllByUserId(subject.getUserId());
        int count = 0;
        Pattern pattern = Pattern.compile(String.format("^%s( \\(\\d+\\))?", subject.getName()));
        for (Subject name : names) {
            Matcher matcher = pattern.matcher(name.getName());
            if (matcher.matches()) {
                count++;
            }
        }
       /* if (count > 0) {
            subject.setName(""+subject.getName()+String.format(" (%d)", count));
        }*/

        return count;
    }


    /*public void delete(Long id) throws ResourceNotFoundException {
        Subject subject = findById(id);
        questionRepository.deleteAllBySubId(subject);
        planRepository.deleteAllBySubId(subject);
        subjectRepository.delete(subject);
    }
    public void deleteAll(ArrayList<Subject> subjects) throws ResourceNotFoundException {
        for (Subject subject: subjects) {
            questionRepository.deleteAllBySubId(subject);
            planRepository.deleteAllBySubId(subject);
            subjectRepository.delete(subject);
        }
    }*/

    public Subject save(Subject subject){
        return subjectRepository.save(subject);
    }
    public Subject findById(Long id) throws ResourceNotFoundException {
        return subjectRepository.findById(id).orElse(null);
    }
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

}
