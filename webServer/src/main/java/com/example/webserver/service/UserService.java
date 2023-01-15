package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import com.example.webserver.repository.SubjectRepository;
import com.example.webserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendSubjectsService friendSubjectsService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    PlanService planService ;
    @Autowired
    QuestionService questionService;
    @Autowired
    CustomerMapper mapper;
    @Autowired
    private SubjectRepository subjectRepository;

    public User putMet(Long id, User s) throws ResourceNotFoundException {
        User user = findById(id);
        user.setLogin(s.getLogin());
        user.setPassword(s.getPassword());
        user.setMatchingPassword(s.getMatchingPassword());
        user.setUpdateDbTime(s.getUpdateDbTime());
        userRepository.save(user);
        return user;
    }


    public User findByLogin(String login){
        User user = userRepository.findByLogin(login);
        return user;
    }
    public User findByEmail(String email){
        User user = userRepository.findByEmail(email);
        return user;
    }
    public User login(String userName, String password) {
        String str =  userRepository.searchUserByEmailAndPassword(userName, password);
        return userRepository.findById(Long.valueOf(str)).orElseThrow(null);


    }
    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) return "email exists";
        if(!user.getPassword().equals(user.getMatchingPassword())) return "password doesn't match";
        if (userRepository.findByLogin(user.getLogin()) != null) return "login exists";
        user.setUpdateDbTime("1900-01-01-01-01-01");
        userRepository.save(user);
        return "ok";
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByLogin(user.getLogin());

        if (userFromDB != null) return false;

        userRepository.save(user);
        return true;
    }



/*    public void delete(Long id) throws ResourceNotFoundException {
        User user = findById(id);
        subjectService.deleteAll(subjectService.findAllByUserId(user));
        friendService.deleteAll(user);
        friendSubjectsService.deleteAll(user);
        userRepository.delete(user);
    }*/

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found for id:" + id + ""));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public ArrayList<Subject> updateDBTime(User userLoc, User userSer,ArrayList<Subject> subjects) throws ResourceNotFoundException {
        if(!checkTime(userLoc,userSer)) {
            System.out.println("обновляю сервер");
            // deleteAllSub(userSer);
            for (Subject s : subjects) {

                subjectService.putMet(s.getId(), s);
                questionService.deleteAllBySubId(s);
                planService.deleteAllBySubId(s);
                addAllQueSubId( s.getQuestions(),s);
                addAllPlanSubId(s.getPlans(),s);
            }

            List<Long> ids = subjects.stream()
                    .map(Subject::getId)
                    .collect(Collectors.toList());
            ArrayList<Subject> sub = subjectRepository.findAllByUserIdAndIdNotIn(userSer,ids);
            deleteAllSub(sub);

            userSer.setUpdateDbTime(currentUpdateDbTime());
            save(userSer);
            return findAllByUserIdPlusQuestionAndPlan(userSer);
        }
        System.out.println("обновляю локальную.");
        return findAllByUserIdPlusQuestionAndPlan(userSer);
    }
    @Transactional
    public void deleteAllSub(ArrayList<Subject> subjects) throws ResourceNotFoundException {
        for (Subject subject : subjects) {
            friendSubjectsService.deleteAllBySubId(subject);
            questionService.deleteAllBySubId(subject);
            planService.deleteAllBySubId(subject);
            subjectService.delete(subject);
        }
    }
    private ArrayList<Subject> findAllByUserIdPlusQuestionAndPlan(User user){
        ArrayList<Subject> s = subjectService.findAllByUserId(user);


        for (Subject sub: s) {
            sub.setQuestions(questionService.findAllBySubId(sub));
            for (Question q: sub.getQuestions()) {
                q.setSubId(null);
            }
            sub.setPlans(planService.findAllBySubId(sub));
            for (Plan p: sub.getPlans()) {
                p.setSubId(null);
            }
        }
        System.out.println(s);
        return s;
    }


    private String currentUpdateDbTime() {
        Calendar cal = new GregorianCalendar();
        String time = "" + cal.get(Calendar.YEAR)+
                "-" +  checkDateFor0(cal.get(Calendar.MONTH)+1)+
                "-" +  checkDateFor0(cal.get(Calendar.DATE))+
                "-" +  checkDateFor0(cal.get(Calendar.HOUR_OF_DAY))+
                "-" +  checkDateFor0(cal.get(Calendar.MINUTE))+
                "-" +  checkDateFor0(cal.get(Calendar.SECOND));
       return time;
    }
    private String checkDateFor0(int figure){
        return figure < 10 ? "0" + figure : "" + figure;
    }
    private void deleteAllSubId(Subject subject) throws ResourceNotFoundException {
        questionService.deleteAllBySubId(subject);
        planService.deleteAllBySubId(subject);
    }
    private void addAllQueSubId(List<Question> questions,Subject subject) throws ResourceNotFoundException {
        for (Question q: questions) {
            q.setSubId(subject);
            System.out.println("обновляю вопрос = " + q);
            questionService.save(q);
        }

    }

    private void addAllPlanSubId(List<Plan> plans,Subject subject) throws ResourceNotFoundException {
        for (Plan p: plans) {
            p.setSubId(subject);
            planService.save(p);
        }
    }
    private boolean checkTime(User userLoc, User userSer){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            // Парсим строки с временем в объекты Date
            Date date1 = format.parse(userLoc.getUpdateDbTime());
            Date date2 = format.parse(userSer.getUpdateDbTime());
            // Сравниваем объекты Date
            return date1.before(date2);
        } catch (Exception e) {
            // Обрабатываем ошибки
            e.printStackTrace();
            return false;
        }

    }
}


