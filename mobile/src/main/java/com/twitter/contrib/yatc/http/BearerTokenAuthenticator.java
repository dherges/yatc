package com.twitter.contrib.yatc.http;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

/** Inspired by https://github.com/square/okhttp/wiki/Recipes */
public class BearerTokenAuthenticator implements Authenticator {

    public interface Callback {

        String obtainToken();
    }

    private final Callback callback;

    public BearerTokenAuthenticator(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        if (responseCount(response) >= 3) {
            return null; // If we've failed 3 times, give up.
        }

        final String credential = "Bearer " + callback.obtainToken();
        if (credential.equals(response.request().header("Authorization"))) {
            return null; // If we already failed with these credentials, don't retry.
        }

        return response.request().newBuilder()
                .header("Authorization", credential)
                .build();
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }

        return result;
    }

}
