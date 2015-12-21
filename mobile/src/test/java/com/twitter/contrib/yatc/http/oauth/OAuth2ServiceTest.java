package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.junit.Before;
import org.junit.Test;

import retrofit.Call;
import retrofit.Retrofit;

import static org.junit.Assert.*;


public class OAuth2ServiceTest {

    protected OAuth2Service oAuth2Service;

    @Before
    public void setUp() throws Exception {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient.interceptors().add(logging);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twitter.com")
                .client(okHttpClient)
                .build();

        oAuth2Service = retrofit.create(OAuth2Service.class);
    }

    @Test
    public void requestToken() throws Exception {

        Call<ResponseBody> call = oAuth2Service.requestToken(
                "OAuth oauth_consumer_key=\"vp3R3eeSvXcYAJLot3TJOE1SJ\", oauth_nonce=\"2bc2f97bbb4ff9bc7893b74c00fe8897\", oauth_signature=\"P6L4zb8t1QuOTcjTwQ%2BwtvU2d1k%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1450456328\", oauth_version=\"1.0\"",
                "client_credentials");

        int statusCode = call.execute().code();

        assertEquals(statusCode, 200);
    }
}
