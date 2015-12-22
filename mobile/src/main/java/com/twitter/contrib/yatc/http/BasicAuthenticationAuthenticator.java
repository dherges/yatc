package com.twitter.contrib.yatc.http;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLEncoder;

/** Inspired by https://github.com/square/okhttp/wiki/Recipes */
public class BasicAuthenticationAuthenticator implements Authenticator {

    public interface Callback {

        String username();

        String password();
    }

    private final Callback callback;
    private final int maxRetries;

    public BasicAuthenticationAuthenticator(Callback callback) {
        this(callback, 3);
    }

    public BasicAuthenticationAuthenticator(Callback callback, int maxRetries) {
        this.callback = callback;
        this.maxRetries = maxRetries;
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        if (responseCount(response) >= maxRetries) {
            return null; // If we've failed 3 times, give up.
        }

        final String username = URLEncoder.encode(callback.username());
        final String password = URLEncoder.encode(callback.password());
        final String credential = "Basic " + Credentials.basic(username, password);
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
