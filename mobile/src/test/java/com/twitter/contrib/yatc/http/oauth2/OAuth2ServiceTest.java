package com.twitter.contrib.yatc.http.oauth2;


import com.squareup.moshi.Moshi;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.twitter.contrib.yatc.dagger.modules.TwitterModule;

import org.junit.Before;
import org.junit.Test;

import retrofit.Call;
import retrofit.Response;

import static org.junit.Assert.*;


public class OAuth2ServiceTest {

    protected OAuth2Service oAuth2Service;

    @Before
    public void setUp() throws Exception {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient.networkInterceptors().add(logging);

        oAuth2Service = new TwitterModule()
                .provideOAuth2Service(okHttpClient, new Moshi.Builder().build());
    }

    @Test
    public void requestToken() throws Exception {
        Call<TokenResponse> call = oAuth2Service.obtainToken("client_credentials");
        Response<TokenResponse> response = call.execute();

        assertEquals(response.code(), 200);
        assertTrue(response.body() != null);
        assertTrue(!response.body().access_token.isEmpty());
        assertTrue(response.body().token_type.equals("bearer"));
    }
}
