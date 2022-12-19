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

    @GET("subjects/byUser/{id}")
    Call<ArrayList<Subjects>> getSubjectsByUser(@Path("id") int id);

    @GET("subjects/{id}")
    Call<Subjects> getSubjectById(@Path("id") int id);

    @POST("subjects")
    Call<Subjects> addSubject(@Body Subjects subjects);

    @POST("questions")
    Call<Questions> addQuestion(@Body Questions questions);

    @POST("plan")
    Call<Plan> addPlan(@Body Plan plan);

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
}
