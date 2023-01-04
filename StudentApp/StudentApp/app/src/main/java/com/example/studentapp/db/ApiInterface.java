package com.example.studentapp.db;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @POST("users/auth")
    Call<Users> authUsers(@Body Users user);

    @POST("users/update")
    Call<ArrayList<Subjects>> update(@Body ArrayList<Subjects> sub);

    @POST("users/register")
    Call<String> regUsers(@Body Users user);

    @GET("/user/{id}")
    Call<Users> getUsers(@Path("id") int id);

    @PUT("/user/{id}")
    Call<Users> editUsers(@Path("id") int id,@Body Users user);

    @GET("subjects/byUser/{id}")
    Call<ArrayList<Subjects>> getSubjectsByUser(@Path("id") int id);

    @GET("subjects_question/{id}")
    Call<Subjects> getSubjectById(@Path("id") int id);

    @GET("subjects/{id}")
    Call<Subjects> getSubjectByIdNotQuestion(@Path("id") int id);

    @POST("subjects")
    Call<Subjects> addSubject(@Body Subjects subjects);

    @POST("questions")
    Call<Questions> addQuestion(@Body Questions questions);

    @POST("plan")
    Call<ArrayList<Plan>> addPlan(@Body ArrayList<Plan> plan);

    @POST("plans/{id}")
    Call<ArrayList<Plan>> addPlans(@Path("id") int id, @Body ArrayList<Plan> plan);

    @DELETE("subjects/{id}")
    Call<Subjects> deleteSubject(@Path("id") int id);

    @DELETE("questions/{id}")
    Call<Questions> deleteQuestion(@Path("id") int id);

    @PUT("subjects/{id}")
    Call<Subjects> updateSubject(@Path("id") int id, @Body Subjects sub);

    @PUT("questions/{id}")
    Call<Questions> updateQuestion(@Path("id") int id, @Body Questions question);

    @PUT("plan/{id}")
    Call<Plan> updatePlan(@Path("id") int id, @Body Plan plan);

    @POST("friends_add")
    Call<String> friendsAdd(@Body Friends friends);

    @POST("friends_delete")
    Call<String> friendsDelete(@Body Friends friends);

    @GET("friends_by_user/{id}")
    Call<ArrayList<Users>> friendsByUser(@Path("id") int id);

    @GET("friends_is/{id}")
    Call<ArrayList<Users>> friendsIs(@Path("id") int id);

    @GET("friends_in/{id}")
    Call<ArrayList<Users>> friendsIn(@Path("id") int id);

    @POST("friends_accept")
    Call<Friends> friendsAccept(@Body Friends friends);

    @POST("friends_delete_is")
    Call<Friends> friendsDeleteIs(@Body Friends friends);

    @POST("friends_refuse")
    Call<Friends> friendsRefuse(@Body Friends friends);

    @POST("friends_subjects_by_id_not_table/{id}")
    Call<ArrayList<Subjects>> getAllFriendsSubjectsNotTabl(@Path("id") int id,@Body Users user);

    @POST("friends_subjects_delete_is")
    Call<FriendsSubjects> friendsSubjectsDeleteIs(@Body FriendsSubjects friends);

    @POST("friends_subjects_accept")
    Call<FriendsSubjects> friendsSubjectsAccept(@Body FriendsSubjects friends);

    @POST("friends_subjects_refuse")
    Call<FriendsSubjects> friendsSubjectsRefuse(@Body FriendsSubjects friends);

    @POST("friends_subjects_sent")
    Call<String> sentFriendsSubjects(@Body ArrayList<FriendsSubjects> friends);

    @POST("friends_subjects_is")
    Call<ArrayList<Subjects>> friendsSubjectsIs(@Body FriendsSubjects friends);

    @POST("friends_subjects_in")
    Call<ArrayList<Subjects>> friendsSubjectsIn(@Body FriendsSubjects friends);

}
