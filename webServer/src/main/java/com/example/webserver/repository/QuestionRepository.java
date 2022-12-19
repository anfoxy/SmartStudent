package com.example.webserver.repository;

import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface QuestionRepository extends JpaRepository<Question,Long> {

    ArrayList<Question> findAllBySubId(Subject subject);

    void deleteAllBySubId(Subject subject);
}
