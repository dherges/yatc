package com.twitter.contrib.yatc.http.oauth;


import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface OAuthService {

    @POST("/oauth/request_token")
    Call<TokenResponse10a> requestToken();

    @POST("/oauth/access_token")
    @FormUrlEncoded
    Call<TokenResponse10a> accessToken(@Field("oauth_verifier") String oAuthVerifier);

}
