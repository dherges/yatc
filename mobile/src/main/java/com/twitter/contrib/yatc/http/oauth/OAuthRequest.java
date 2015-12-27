package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.SortedMap;
import java.util.TreeMap;

import okio.Buffer;

/**
 * OAuthRequest wraps an OK HTTP request with oauth_* parameters
 */
public class OAuthRequest {

    private final Request wrapped;
    private final SortedMap<String, String> oAuthParams = new TreeMap<>();

    public OAuthRequest(Request request) {
        this.wrapped = request;
    }

    public Request request() {
        return wrapped;
    }

    /** Returns the "oauth_*" parameters */
    public SortedMap<String, String> params() {
        return oAuthParams;
    }

    /** Returns the "OAuth" string that should be used as Authorization header */
    public String auth() {
        final Buffer oAuthHeader = new Buffer();
        for (String key : oAuthParams.keySet()) {
            if (oAuthHeader.size() > 0) {
                oAuthHeader.writeUtf8(", "); // If there are key/value pairs remaining, append a comma ‘,’ and a space ’ ’ to DST.
            } else {
                oAuthHeader.writeUtf8("OAuth ");
            }

            oAuthHeader.writeUtf8(OAuth.Encoder.encode(key))
                    .writeByte('=')
                    .writeUtf8("\"")
                    .writeUtf8(OAuth.Encoder.encode(oAuthParams.get(key)))
                    .writeUtf8("\"");
        }

        return oAuthHeader.readUtf8();
    }

    public Request applyAuth() {
        return wrapped.newBuilder()
                .addHeader("Authorization", auth())
                .build();
    }



    public class AccessTokenRequestBuilder {
        private String verifier;
        private String verb;
        private String url;

        public AccessTokenRequestBuilder() {
            this.verb = "POST";
        }

        public AccessTokenRequestBuilder url(String url) {
            this.url = url;

            return this;
        }

        public AccessTokenRequestBuilder verb(String verb) {
            this.verb = verb;

            return this;
        }

        public AccessTokenRequestBuilder verifier(String verifier) {
            this.verifier = verifier;

            return this;
        }

        public OAuthRequest build() {
            RequestBody body = null;
            if (verifier != null) {
                body = new FormEncodingBuilder().add(OAuth.VERIFIER, verifier).build();
            }

            Request request = new Request.Builder()
                    .method(verb, body)
                    .url(url)
                    .build();

            OAuthRequest oAuthRequest = new OAuthRequest(request);
            if (verifier != null) {
                oAuthRequest.oAuthParams.put(OAuth.VERSION, verifier);
            }

            return oAuthRequest;
        }
    }

    public class RequestTokenRequestBuilder {
        private String callback;
        private String verb;
        private String url;

        public RequestTokenRequestBuilder() {
            this.verb = "POST";
        }

        public RequestTokenRequestBuilder url(String url) {
            this.url = url;

            return this;
        }

        public RequestTokenRequestBuilder verb(String verb) {
            this.verb = verb;

            return this;
        }

        public RequestTokenRequestBuilder callback(String callback) {
            this.callback = callback;

            return this;
        }

        public OAuthRequest build() {
            RequestBody body = null;
            if (callback != null) {
                body = new FormEncodingBuilder().add(OAuth.CALLBACK, callback).build();
            }

            Request request = new Request.Builder()
                    .method(verb, body)
                    .url(url)
                    .build();

            OAuthRequest oAuthRequest = new OAuthRequest(request);
            if (callback != null) {
                oAuthRequest.oAuthParams.put(OAuth.CALLBACK, callback);
            }

            return oAuthRequest;
        }
    }

}
