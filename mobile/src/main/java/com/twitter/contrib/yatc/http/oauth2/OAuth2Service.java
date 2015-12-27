package com.twitter.contrib.yatc.http.oauth2;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface OAuth2Service {

    @POST("/oauth2/token")
    @Headers({"Accepts: */*"})
    @FormUrlEncoded
    Call<TokenResponse> obtainToken(@Field("grant_type") String grantType);

    @POST("/oauth2/invalidate_token")
    @Headers({"Accepts: */*"})
    @FormUrlEncoded
    Call<TokenResponse> invalidateToken(@Field("access_token") String accessToken);

}
