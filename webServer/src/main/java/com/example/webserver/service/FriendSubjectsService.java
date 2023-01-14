package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.*;
import com.example.webserver.repository.FriendsSubjectsRepository;
import com.example.webserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendSubjectsService {
    @Autowired
    FriendsSubjectsRepository friendsSubjectsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubjectService subjectService;

    @Autowired
    PlanService planService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CustomerMapper mapper;


    public ArrayList<Subject> findIn(FriendsSubjects id) throws ResourceNotFoundException {

        ArrayList<FriendsSubjects> friends =  friendsSubjectsRepository.findAllByUserIdAndFriendId(id.getUserId(),id.getFriendId());

        friends.removeIf(date -> {
            if(date.getStatus().equals("ACCEPTED")) return true;
            else return date.getStatus().equals("REQUEST_SENT");
        });

        ArrayList<Subject> subjects = new ArrayList<>();
        for (FriendsSubjects sbj:friends) {
            subjects.add(sbj.getSubId());
        }
        return subjects;
    }
    public ArrayList<Subject> findIs(FriendsSubjects id) throws ResourceNotFoundException {

        ArrayList<FriendsSubjects> friends =  friendsSubjectsRepository.findAllByUserIdAndFriendId(id.getUserId(),id.getFriendId());

        friends.removeIf(date -> {
            if(date.getStatus().equals("ACCEPTED")) return true;
            else return date.getStatus().equals("INVITATION_RECEIVED");
        });

        ArrayList<Subject> subjects = new ArrayList<>();
        for (FriendsSubjects sbj:friends) {
            subjects.add(sbj.getSubId());
        }

        return subjects;
    }


    public Subject acceptFriendsSubjects(FriendsSubjects friends){
        FriendsSubjects friends1 = friendsSubjectsRepository.findByUserIdAndFriendIdAndSubId(friends.getUserId(),friends.getFriendId(),friends.getSubId());
        //friends1.setStatus("ACCEPTED");
        friendsSubjectsRepository.delete(friends1);
        FriendsSubjects friends2 = friendsSubjectsRepository.findByUserIdAndFriendIdAndSubId(friends.getFriendId(),friends.getUserId(),friends.getSubId());
        //friends2.setStatus("ACCEPTED");
        friendsSubjectsRepository.delete(friends2);

        Subject subject = new Subject(null,friends.getSubId().getName(),
                friends.getSubId().getDays(),0,friends.getUserId());
      /*  int count = subjectService.numberOfDuplicateSubjects(subject);
        if (count > 0) {
            subject.setName(""+subject.getName()+String.format("(%d)", count));
        }*/


        int count =  subjectService.numberOfDuplicateSubjects(subject);
        if (count > 0) {
            subject.setName(subjectService.replaceNumberInBrackets(subject.getName(),count));
        }

        //Subject s = subjectService.save(subject);
        subject.setQuestions(new ArrayList<>());

        for (Question q:questionService.findAllBySubId(friends.getSubId())) {
           subject.getQuestions().add(new Question(null,q.getQuestion(),q.getAnswer(),q.getDate(),0,0,null));
            //questionService.save();
        }
      /*  for (Plan p:planService.findAllBySubId(friends.getSubId())) {
            planService.save(new Plan(null,p.getDate(),0,s));
        }*/


        return subject;

    }
    public void refuseFriendsSubjects(FriendsSubjects friends){

        FriendsSubjects friends1 = friendsSubjectsRepository.findByUserIdAndFriendIdAndSubId(friends.getUserId(),friends.getFriendId(),friends.getSubId());
        if(friends1 != null) friendsSubjectsRepository.delete(friends1);
        FriendsSubjects friends2 = friendsSubjectsRepository.findByUserIdAndFriendIdAndSubId(friends.getFriendId(),friends.getUserId(),friends.getSubId());
        if(friends2 != null) friendsSubjectsRepository.delete(friends2);
    }
    public void deleteIsFriendsSubjects(FriendsSubjects friends){

        FriendsSubjects friends1 = friendsSubjectsRepository.findByUserIdAndFriendIdAndSubId(friends.getUserId(),friends.getFriendId(),friends.getSubId());
        friendsSubjectsRepository.delete(friends1);
    }


    public String sentFriendsSubjects(ArrayList<FriendsSubjects> f){

        String str = "OK";
        for (FriendsSubjects friends:f) {
            if (friends.getFriendId() == null || friends.getUserId() == null) str = "Not";
            else {
                friendsSubjectsRepository.save(new FriendsSubjects(null, "REQUEST_SENT",
                        friends.getFriendId(), friends.getUserId(), friends.getSubId()));
                friendsSubjectsRepository.save(new FriendsSubjects(null, "INVITATION_RECEIVED",
                        friends.getUserId(), friends.getFriendId(), friends.getSubId()));
            }
        }
       return str;
    }

    public ArrayList<Subject>findAllFriendsSubjectsNotTablByIdFriend (Long id, User user) throws ResourceNotFoundException {

        User friend = userRepository.findById(id).orElse(null);
        ArrayList<FriendsSubjects> friendsSubjects = friendsSubjectsRepository.findAllByUserIdAndFriendId(user,friend);
        ArrayList<Subject> subjectArrayList = subjectService.findAllByUserId(user);
        subjectArrayList.removeIf(subject -> {
            for (FriendsSubjects friendsSub: friendsSubjects) {
               if (subject.equals(friendsSub.getSubId())) return true;
            }
            return false;
        });
        return subjectArrayList;
    }

    public FriendsSubjects putMet(Long id, FriendsSubjects res) throws ResourceNotFoundException {
        FriendsSubjects friends = findById(id);

        friendsSubjectsRepository.save(friends);
        return friends;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        FriendsSubjects friends = findById(id);
        friendsSubjectsRepository.delete(friends);
    }
    @Transactional
    public void deleteAllBySubId(Subject subject) throws ResourceNotFoundException {
        if (!friendsSubjectsRepository.findAllBySubId(subject).isEmpty())
            friendsSubjectsRepository.deleteAllBySubId(subject);
    }
    public FriendsSubjects save(FriendsSubjects friends){
        return friendsSubjectsRepository.save(friends);
    }
    public FriendsSubjects findById(Long id) throws ResourceNotFoundException {
        return friendsSubjectsRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("FriendsSubjects not found for id:" + id.toString() + ""));
    }
    public List<FriendsSubjects> findAll() {
        return friendsSubjectsRepository.findAll();
    }

}
