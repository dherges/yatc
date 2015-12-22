package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;
import com.twitter.contrib.yatc.http.BasicAuthenticationInterceptor;

import retrofit.Call;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
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
    Call<TokenResponse> requestToken(@Field("grant_type") String grantType);

    @POST("/oauth2/invalidate_token")
    @Headers({"Accepts: */*"})
    @FormUrlEncoded
    Call<TokenResponse> invalidateToken(@Header("Authorization") String authorizationHeader,
                                        @Field("access_token") String accessToken);

    @GET("/sign-in-with-twitter")
    Call<ResponseBody> signInWithTwitter(@Query("oauth_token") String oAuthToken,
                                         @Query("oauth_verifier") String oAuthVerified);



    class Builder {

        private final Retrofit.Builder builder;
        private MoshiConverterFactory factory;
        private OkHttpClient okHttpClient;
        private String userName;
        private String password;

        public Builder(Retrofit.Builder builder) {
            this.builder = builder;
        }

        public Builder authentication(String userName, String password) {
            this.userName = userName;
            this.password = password;

            return this;
        }

        public Builder baseUrl(String baseUrl) {
            builder.baseUrl(baseUrl);

            return this;
        }

        public Builder moshi(MoshiConverterFactory factory) {
            this.factory = factory;

            return this;
        }

        public Builder client(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;

            return this;
        }

        public OAuth2Service build() {
            if (factory == null) {
                factory = MoshiConverterFactory.create();
            }
            builder.addConverterFactory(factory);

            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient();
            }
            okHttpClient.networkInterceptors().add(new BasicAuthenticationInterceptor(userName, password));
            builder.client(okHttpClient);

            return builder.build().create(OAuth2Service.class);
        }

    }
}
