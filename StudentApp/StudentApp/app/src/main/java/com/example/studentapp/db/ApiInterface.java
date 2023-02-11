package com.example.studentapp.db;





import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
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
    Call<Users> getUsers(@Path("id") long id);

    @PUT("/user/{id}")
    Call<Users> editUsers(@Path("id") long id,@Body Users user);

    @POST("plan")
    Call<ArrayList<Plan>> addPlan(@Body ArrayList<Plan> plan);

    @POST("friends_add")
    Call<String> friendsAdd(@Body Friends friends);

    @POST("friends_delete")
    Call<String> friendsDelete(@Body Friends friends);

    @GET("friends_by_user/{id}")
    Call<ArrayList<Users>> friendsByUser(@Path("id") long id);

    @GET("friends_is/{id}")
    Call<ArrayList<Users>> friendsIs(@Path("id") long id);

    @GET("friends_in/{id}")
    Call<ArrayList<Users>> friendsIn(@Path("id") long id);

    @POST("friends_accept")
    Call<Friends> friendsAccept(@Body Friends friends);

    @POST("friends_delete_is")
    Call<Friends> friendsDeleteIs(@Body Friends friends);

    @POST("friends_refuse")
    Call<Friends> friendsRefuse(@Body Friends friends);

    @POST("friends_subjects_by_id_not_table/{id}")
    Call<ArrayList<Subjects>> getAllFriendsSubjectsNotTabl(@Path("id") long id,@Body Users user);

    @POST("friends_subjects_delete_is")
    Call<FriendsSubjects> friendsSubjectsDeleteIs(@Body FriendsSubjects friends);

    @POST("friends_subjects_accept")
    Call<Subjects> friendsSubjectsAccept(@Body FriendsSubjects friends);

    @POST("friends_subjects_refuse")
    Call<FriendsSubjects> friendsSubjectsRefuse(@Body FriendsSubjects friends);

    @POST("friends_subjects_sent")
    Call<String> sentFriendsSubjects(@Body ArrayList<FriendsSubjects> friends);

    @POST("friends_subjects_is")
    Call<ArrayList<Subjects>> friendsSubjectsIs(@Body FriendsSubjects friends);

    @POST("friends_subjects_in")
    Call<ArrayList<Subjects>> friendsSubjectsIn(@Body FriendsSubjects friends);


    @POST("/game/set")
    Call<Game> gameSet(@Body Game game);


    @GET("/game/check_start/{id}")
    Observable<String> gameCheckStart(@Path("id") long id);

    @POST("/game/checking_availability/{id}")
    Call<Game> checkingForGameAvailability(@Path("id") long id,@Body Users friend);

    @POST("/game/set_status/{id}")
    Call<Game> gameSetStatus(@Path("id") long id,@Body String status);

    @GET("/game/start/{id}")
    Call<Game> gameStart(@Path("id") long id);

    @POST("/game_subjects/get_question")
    Call<GameSubjects> gameGetQuestion(@Body Game game);

    @POST("/game_subjects/set_question_host")
    Call<GameSubjects> gameSetQuestionHost(@Body GameSubjects game);

    @POST("/game_subjects/set_question_friend")
    Call<GameSubjects> gameSetQuestionFriend(@Body GameSubjects game);

    @POST("/game_subjects/set_result_host")
    Call<GameSubjects> gameSetResultHost(@Body GameSubjects game);

    @POST("/game_subjects/set_result_friend")
    Call<GameSubjects> gameSetResultFriend(@Body GameSubjects game);

    @POST("/game_subjects/get_result")
    Call<GameSubjects> gameGetResult(@Body Game game);

    @POST("/game/get_question_list")
    Call<ArrayList<GameSubjects>> gameGetQuestionList(@Body Game game);

    @POST("/game_history/get_all/{id}")
    Call<ArrayList<Game>> getAllGamesByUserId(@Path("id") long id,@Body Users user);

    @HTTP(method = "DELETE", path = "/game_history", hasBody = true)
    Call<Integer> deleteGame(@Body GameHistory game);

    @POST("/game/exiting_the_game/{id}")
    Call<Integer> exitingTheGame(@Path("id") long id,@Body Game game);

}
