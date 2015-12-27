package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSource;


/**
 * OAuthResponse wraps around an OK HTTP response extracting oauth parameters (i.e. tokens)
 */
public class OAuthResponse {

    protected final Map<String, String> params;

    protected OAuthResponse(Builder b) {
        this.params = Collections.unmodifiableMap(parseResponseBody(b.response.body()));
    }

    public Map<String, String> params() {
        return params;
    }

    public String token() {
        return params.get(OAuth.TOKEN);
    }

    public String tokenSecret() {
        return params.get(OAuth.TOKEN_SECRET);
    }

    public boolean callbackConfirmed() {
        return Boolean.valueOf(params.get(OAuth.CALLBACK_CONFIRMED));
    }

    public String screenName() {
        return params.get(OAuth.SCREEN_NAME);
    }

    public String verifier() {
        return params.get(OAuth.VERIFIER);
    }


    public static class Builder {
        protected Response response;

        public Builder() {}

        public Builder response(Response response) {
            this.response = response;

            return this;
        }

        public OAuthResponse build() {
            return new OAuthResponse(this);
        }
    }


    static Map<String, String> parseResponseBody(ResponseBody body) {
        final Map<String, String> params = new HashMap<>();

        if (body != null) {
            try {
                final BufferedSource source = body.source();
                while (!source.exhausted()) {
                    long keyEnd = source.indexOf((byte) '=');
                    if (keyEnd == -1)
                        throw new IllegalStateException("Key with no value: " + source.readUtf8());
                    String key = source.readUtf8(keyEnd);
                    source.skip(1); // Equals.

                    long valueEnd = source.indexOf((byte) '&');
                    String value = valueEnd == -1 ? source.readUtf8() : source.readUtf8(valueEnd);
                    if (valueEnd != -1) source.skip(1); // Ampersand.

                    params.put(key, value);
                }
            } catch (IOException e) {
            }
        }

        return params;
    }

}
