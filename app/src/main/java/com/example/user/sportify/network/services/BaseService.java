package com.example.user.sportify.network.services;

import com.example.user.sportify.network.models.BaseServerAnswer;
import com.example.user.sportify.network.models.BaseGameDataApi;
import com.example.user.sportify.network.models.DataUserToken;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.network.models.LocationData;
import com.example.user.sportify.network.models.MyGamesData;
import com.example.user.sportify.network.models.ProfileData;
import com.example.user.sportify.network.models.UserParticipantData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface BaseService {
    @GET("route.php?action=get-games")
    Call<BaseServerAnswer<BaseGameDataApi>> getGames(
            @Query("category") int categoryId,
            @Query("page") int pageNumber
    );

    @FormUrlEncoded
    @POST("route.php?action=sign-in")
    Call<BaseServerAnswer<DataUserToken>> signIn(
            @Field("phone") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("route.php?action=sign-up")
    Call<BaseServerAnswer<DataUserToken>> signUp(
            @Field("name") String name,
            @Field("age") String age,
            @Field("phone") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("route.php?action=get-profile")
    Call<BaseServerAnswer<ProfileData>> getProfile(
            @Field("token") String token
    );

    @Multipart
    @POST("route.php?action=game-create")
    Call<ResponseBody> createGame(
            @Part("token") RequestBody token,
            @Part("category_id") RequestBody categoryId,
            @Part("description") RequestBody description,
            @Part("date") RequestBody date,
            @Part("time") RequestBody time,
            @Part("location") RequestBody location,
            @Part MultipartBody.Part picture,
            @Part("coordinates") RequestBody coordinates,
            @Part("max_people_quantity") RequestBody maxPeopleQuantity,
            @Part("current_people_quantity") RequestBody currentPeopleQuantity
    );

    @FormUrlEncoded
    @POST("route.php?action=game-update")
    Call<ResponseBody> updateGame(
            @Field("category_id") String categoryId,
            @Field("description") String description,
            @Field("date") String date,
            @Field("time") String time,
            @Field("location") String location,
            @Field("coordinates") String coordinates,
            @Field("max_people_quantity") String maxPeopleQuantity,
            @Field("game_id") String gameId
    );

    @FormUrlEncoded
    @POST("route.php?action=game-attach")
    Call<BaseServerAnswer<String>> attachToGame(
            @Field("token") String token,
            @Field("game_id") String gameId
    );

    @FormUrlEncoded
    @POST("route.php?action=game-unattach")
    Call<BaseServerAnswer<String>> unAttachFromGame(
            @Field("token") String token,
            @Field("game_id") String gameId
    );

    @FormUrlEncoded
    @POST("route.php?action=update-cancel")
    Call<BaseServerAnswer<String>> cancelGame(
            @Field("token") String token,
            @Field("cancel") int cancel,
            @Field("game_id") String gameId
    );

    @GET("GeoCoder.php")
    Call<LocationData> getLocation(
            @Query("geocode") String geocode
    );

    @FormUrlEncoded
    @POST("route.php?action=update-profile")
    Call<ResponseBody> updateProfile(
            @Field("token") String token,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("password") String password
    );


    //    @GET("route.php?action=game-participants")
//    Call<BaseServerAnswer> getGameParticipants(
//
//    );
//
//    @GET("route.php?action=game-attach")
//    Call<BaseServerAnswer> attachPersonToGame(
//
//    );
//
    @GET("route.php?action=game-my")
    Call<BaseServerAnswer<MyGamesData>> getMyGames(
            @Query("token") String token
    );

    @GET("route.php?action=game-participants")
    Call<BaseServerAnswer<List<GamesParticipantData>>> getGameParticipants(
            @Query("game_id") String gameId
    );

    @GET("route.php?action=get-user")
    Call<BaseServerAnswer<UserParticipantData>> getUserPhone(
            @Query("id") String userId
    );
//
//    @GET("route.php?action=game-remove")
//    Call<BaseServerAnswer> cancelGame(
//
//    );
}
