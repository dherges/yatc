package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.twitter.contrib.yatc.dagger.modules.TwitterModule;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class TwitterApiIntegrationTest {

    protected OkHttpClient okHttpClient;

    @Before
    public void client() {
        okHttpClient = new OkHttpClient();
        final HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient.networkInterceptors().add(log);
    }

    @Test
    public void testObtainRequestToken() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        RequestSigner signer = new RequestSigner.Builder()
                .consumer(TwitterModule.Config.CONSUMER_KEY, TwitterModule.Config.CONSUMER_SECRET)
                .build();

        OAuthRequest oAuthRequest = new OAuthRequest.Builder()
                .requestToken("POST", "https://api.twitter.com/oauth/request_token", "content://blubb")
                .build();

        OAuthRequest signedRequest = signer.signRequest(oAuthRequest);

        Response response = okHttpClient.newCall(signedRequest.request()).execute();

        OAuthResponse oAuthResponse = new OAuthResponse.Builder()
                .response(response)
                .build();

        assertThat(response.code()).isEqualTo(200);
        assertThat(oAuthResponse.token()).isNotEmpty();
        assertThat(oAuthResponse.tokenSecret()).isNotEmpty();
    }

    @Test
    public void testObtainAccessToken() {

    }

}
