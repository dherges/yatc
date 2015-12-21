package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface OAuth2Service {

    @POST("/oauth2/token")
    @Headers({"Accepts: */*"})
    @FormUrlEncoded
    Call<ResponseBody> requestToken(@Header("Authorization") String authorizationHeader,
                                    @Field("grant_type") String grantType);

    @GET("/sign-in-with-twitter")
    Call<ResponseBody> signInWithTwitter(@Query("oauth_token") String oAuthToken,
                                         @Query("oauth_verifier") String oAuthVerified);

}
