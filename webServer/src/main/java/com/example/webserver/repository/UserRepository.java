package com.example.webserver.repository;

import com.example.webserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT id_user From public.user " +
            "WHERE email IN(:email) " +
            "AND   user_password IN(:user_password);",nativeQuery = true)
    String searchUserByEmailAndPassword(@Param("email") String email,
                                              @Param("user_password") String user_password);

    User findByEmail(String email);

    User findByLogin(String login);


}
