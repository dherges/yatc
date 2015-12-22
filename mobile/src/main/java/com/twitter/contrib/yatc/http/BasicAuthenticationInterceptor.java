package com.twitter.contrib.yatc.http;


import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class BasicAuthenticationInterceptor implements Interceptor {

    private final String userName;
    private final String password;

    public BasicAuthenticationInterceptor(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", Credentials.basic(userName, password))
                .build();

        return chain.proceed(request);
    }

}
