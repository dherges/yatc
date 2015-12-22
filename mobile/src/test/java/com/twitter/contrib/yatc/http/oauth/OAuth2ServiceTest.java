package com.twitter.contrib.yatc.http.oauth;


import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.junit.Before;
import org.junit.Test;

import java.net.URLEncoder;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import static org.junit.Assert.*;


public class OAuth2ServiceTest {

    protected OAuth2Service oAuth2Service;

    @Before
    public void setUp() throws Exception {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient.networkInterceptors().add(logging);

        oAuth2Service = new OAuth2Service.Builder(new Retrofit.Builder())
                .baseUrl("https://api.twitter.com")
                .authentication("vp3R3eeSvXcYAJLot3TJOE1SJ", "qqI5GFRqJCnHFiIaK10gyVqDhrvGftZFUNIfO7bWGiSvhIyoM0")
                .client(okHttpClient)
                .build();
    }

    @Test
    public void requestToken() throws Exception {
        String consumerKeyEncoded = URLEncoder.encode("vp3R3eeSvXcYAJLot3TJOE1SJ");
        String consumerSecretEncoded = URLEncoder.encode("qqI5GFRqJCnHFiIaK10gyVqDhrvGftZFUNIfO7bWGiSvhIyoM0");
        String credential = Credentials.basic(consumerKeyEncoded, consumerSecretEncoded);

        Call<TokenResponse> call = oAuth2Service.requestToken("client_credentials");
        Response<TokenResponse> response = call.execute();

        assertEquals(response.code(), 200);
        assertTrue(response.body() != null);
        assertTrue(!response.body().access_token.isEmpty());
        assertTrue(response.body().token_type.equals("bearer"));
    }
}
