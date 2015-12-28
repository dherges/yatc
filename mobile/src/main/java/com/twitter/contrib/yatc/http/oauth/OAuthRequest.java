package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import okio.Buffer;

/**
 * OAuthRequest wraps around an OK HTTP request, adding oauth_* parameters
 */
public class OAuthRequest {
    private static final MediaType FORM_CONTENT_TYPE =
            MediaType.parse("application/x-www-form-urlencoded");

    private final SortedMap<String, String> oAuthParams;
    private final Map<String, String> bodyParams;
    private final Map<String, String> queryParams;

    private final String verb;
    private final String url;
    private final Request original;

    protected OAuthRequest(Builder b) {
        this.verb = b.verb;
        this.url = b.url;
        this.original = b.request;

        this.oAuthParams = b.oauth;
        this.bodyParams = Collections.unmodifiableMap(extractBodyParams(b.request.body()));
        this.queryParams = Collections.unmodifiableMap(extractQueryParams(b.request));
    }

    /** Returns the "oauth_*" parameters */
    public SortedMap<String, String> oauth() {
        return oAuthParams;
    }

    /** Returns body parameters */
    public Map<String, String> body() {
        return bodyParams;
    }

    /** Returns query parameters */
    public Map<String, String> query() {
        return queryParams;
    }

    /** Returns the "OAuth" string that should be used as Authorization header */
    public String auth() {
        return createOAuthHeader(oAuthParams);
    }

    /** Returns the OAuth verb (equal to the HTTP method) */
    public String verb() {
        return verb;
    }

    /** Returns the OAuth base url (equal to the HTTP url minus query minus fragment) */
    public String url() {
        return url;
    }

    /** Returns an OK HTTP request that represents this OAuthRequest */
    public Request request() {
        return original.newBuilder()
                .header("Authorization", auth())
                .build();
    }

    public Builder newBuilder() {
        return new Builder().oAuthRequest(this);
    }


    public static class Builder {
        protected String verb;
        protected String url;
        protected Request request;
        protected SortedMap<String, String> oauth = new TreeMap<>();

        public Builder() {}

        public Builder request(Request request) {
            this.request = request;
            this.verb = request.method();
            this.url = request.httpUrl().newBuilder().query(null).fragment(null).build().toString();

            return this;
        }

        public Builder oAuthRequest(OAuthRequest oAuthRequest) {
            request(oAuthRequest.request());
            oauth = oAuthRequest.oauth();

            return this;
        }

        public Builder requestToken(String verb, String url, String callback) {
            this.verb = verb;
            this.url = url;
            oauth.put(OAuth.CALLBACK, callback);

            return this;
        }

        public Builder param(String key, String value) {
            oauth.put(key, value);

            return this;
        }

        public Builder params(Map<String, String> params) {
            oauth.putAll(params);

            return this;
        }

        public OAuthRequest build() {
            if (request == null) {
                Request.Builder rb = new Request.Builder()
                        .url(url);

                boolean needsBody = (verb != null && verb.equals("POST")) || (oauth.containsKey(OAuth.VERIFIER));
                if (needsBody) {
                    FormEncodingBuilder body = new FormEncodingBuilder();

                    String verifier = oauth.remove(OAuth.VERIFIER);
                    if (verifier != null) {
                        body.add(OAuth.VERIFIER, verifier);
                    }

                    rb.method((verb != null) ? verb : request.method(), body.build());
                }

                request = rb.build();
            }

            return new OAuthRequest(this);
        }
    }


    static Map<String, String> extractBodyParams(RequestBody body) {
        // extract form-encoded HTTP body params
        final Map<String, String> bodyParams = new HashMap<>();
        if (body != null && body.contentType().equals(FORM_CONTENT_TYPE)) {
            final Buffer buffer = new Buffer();
            try {
                body.writeTo(buffer);

                while (!buffer.exhausted()) {
                    long keyEnd = buffer.indexOf((byte) '=');
                    if (keyEnd == -1)
                        throw new IllegalStateException("Key with no value: " + buffer.readUtf8());
                    String key = buffer.readUtf8(keyEnd);
                    buffer.skip(1); // Equals.

                    long valueEnd = buffer.indexOf((byte) '&');
                    String value = valueEnd == -1 ? buffer.readUtf8() : buffer.readUtf8(valueEnd);
                    if (valueEnd != -1) buffer.skip(1); // Ampersand.

                    bodyParams.put(key, value);
                }
            } catch (IOException e) {
            }
        }

        return bodyParams;
    }

    static Map<String, String> extractQueryParams(Request request) {
        // extract HTTP query params
        final HttpUrl url = request.httpUrl();
        final Map<String, String> queryParams = new HashMap<>(url.querySize());
        for (int i = 0, len = url.querySize(); i < len; i++) {
            final String key = OAuth.Encoder.encode(url.queryParameterName(i));
            final String value = OAuth.Encoder.encode(url.queryParameterValue(i));

            queryParams.put(key, value);
        }

        return queryParams;
    }

    static String createOAuthHeader(Map<String, String> oAuthParams) {
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

}
