package com.example.webserver.repository;

import com.example.webserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT id_user From public.user " +
            "WHERE user_name IN(:user_name) " +
            "AND   user_password IN(:user_password);",nativeQuery = true)
    String searchUserByLoginAndPassword(@Param("user_name") String user_name,
                                              @Param("user_password") String user_password);


    User findUserByLogin(String login);


}
