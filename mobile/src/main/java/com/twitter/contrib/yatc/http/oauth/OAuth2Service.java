package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface OAuth2Service {

    @POST("/oauth2/token")
    @Headers({"Accepts: */*"})
    @FormUrlEncoded
    Call<TokenResponse> requestToken(@Field("grant_type") String grantType);

    @POST("/oauth2/invalidate_token")
    @Headers({"Accepts: */*"})
    @FormUrlEncoded
    Call<TokenResponse> invalidateToken(@Field("access_token") String accessToken);

    @GET("/sign-in-with-twitter")
    Call<ResponseBody> signInWithTwitter(@Query("oauth_token") String oAuthToken,
                                         @Query("oauth_verifier") String oAuthVerified);

}
