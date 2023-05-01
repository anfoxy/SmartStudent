package com.example.webserver.mapper;

import com.example.webserver.dto.QuestionDTO;
import com.example.webserver.dto.SubjectDTO;
import com.example.webserver.dto.UserDTO;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-01T13:40:04+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 19.0.2 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public void updateQuestionFromDto(QuestionDTO dto, Question entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getQuestion() != null ) {
            entity.setQuestion( dto.getQuestion() );
        }
        if ( dto.getAnswer() != null ) {
            entity.setAnswer( dto.getAnswer() );
        }
        if ( dto.getSubId() != null ) {
            entity.setSubId( dto.getSubId() );
        }
    }

    @Override
    public void updateSubjectFromDto(SubjectDTO dto, Subject entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getDays() != null ) {
            entity.setDays( dto.getDays() );
        }
        if ( dto.getUserId() != null ) {
            entity.setUserId( dto.getUserId() );
        }
    }

    @Override
    public void updateUserFromDto(UserDTO dto, User entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getLogin() != null ) {
            entity.setLogin( dto.getLogin() );
        }
        if ( dto.getPassword() != null ) {
            entity.setPassword( dto.getPassword() );
        }
        if ( dto.getMatchingPassword() != null ) {
            entity.setMatchingPassword( dto.getMatchingPassword() );
        }
    }
}
