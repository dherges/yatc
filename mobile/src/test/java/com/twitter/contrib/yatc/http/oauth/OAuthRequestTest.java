package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.twitter.contrib.yatc.dagger.modules.TwitterModule;

import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuthRequestTest {

    @Test
    public void testObtainRequestToken() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        RequestSigner signer = new RequestSigner.Builder()
                .consumer(TwitterModule.Config.CONSUMER_KEY, TwitterModule.Config.CONSUMER_SECRET)
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/oauth/request_token")
                .post(new FormEncodingBuilder().build())
                .build();

        OAuthRequest signedRequest = signer.signRequest(new OAuthRequest.Builder().request(request).build());

        final OkHttpClient okHttpClient = new OkHttpClient();
        final HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient.networkInterceptors().add(log);

        Response response = okHttpClient.newCall(signedRequest.request()).execute();

        //assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("");

        /*
         Authorization: OAuth oauth_consumer_key="vp3R3eeSvXcYAJLot3TJOE1SJ",
                              oauth_nonce="f1d32ea233f1dcbd3c5ba28a1e8fbe5c",
                              oauth_signature="nrEhtkx%2B3ooHHUQ4qjreWNIPYN4%3D",
                              oauth_signature_method="HMAC-SHA1",
                              oauth_timestamp="1451216559",
                              oauth_version="1.0"
         */
    }

    @Test
    public void testObtainAccessToken() {

    }

}
