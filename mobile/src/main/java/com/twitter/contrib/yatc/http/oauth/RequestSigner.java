package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okio.Buffer;
import okio.ByteString;

/**
 * Signs OK HTTP requests with OAuth 1.0a HMAC-SHA1 signature
 *
 * @link https://dev.twitter.com/oauth/overview/creating-signatures
 */
public class RequestSigner {
    private static final MediaType FORM_CONTENT_TYPE =
            MediaType.parse("application/x-www-form-urlencoded");
    private static final String SIGNATURE_TYPE = "HmacSHA1";


    private final String consumerKey;
    private final String tokenValue;
    private final OAuth.NonceGenerator nonce;
    private final OAuth.TimestampGenerator timestamp;
    private Mac mac;

    protected RequestSigner(Builder b) {
        this.consumerKey = b.consumerKey;
        this.tokenValue = b.tokenValue;
        this.nonce = b.nonce;
        this.timestamp = b.timestamp;
    }

    /**
     * Initializes the key that is used for creating signatures.
     *
     * @param consumerSecret An OAuth consumer key
     * @param tokenSecret An OAuth token secret, obtained from either the request token or access token; may be null
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    protected void initKey(String consumerSecret, String tokenSecret) throws NoSuchAlgorithmException, InvalidKeyException {
        final Buffer key = new Buffer()
                .writeUtf8(OAuth.Encoder.encode(consumerSecret))
                .writeByte('&');
        if (tokenSecret != null && tokenSecret.length() > 0) {
            key.writeUtf8(OAuth.Encoder.encode(tokenSecret));
        }

        final SecretKeySpec secret = new SecretKeySpec(key.readByteArray(), SIGNATURE_TYPE);
        this.mac = Mac.getInstance(SIGNATURE_TYPE);
        mac.init(secret);
    }

    /**
     * Signs a OK HTTP request
     *
     * @param request Original request
     * @return Signed request with "oauth_*" parameters
     */
    public OAuthRequest signRequest(OAuthRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        final OAuthRequest signedRequest = new OAuthRequest(request.request());
        signedRequest.params().putAll(request.params());

        // Collect oauth_* params
        signedRequest.params().put(OAuth.CONSUMER_KEY, consumerKey);
        signedRequest.params().put(OAuth.NONCE, nonce.create());
        signedRequest.params().put(OAuth.SIGNATURE_METHOD, OAuth.SIGNATURE_METHOD_VALUE_HMAC_SHA1);
        signedRequest.params().put(OAuth.TIMESTAMP, "" + timestamp.create());
        if (tokenValue != null && tokenValue.length() > 0) {
            signedRequest.params().put(OAuth.TOKEN, tokenValue);
        }
        signedRequest.params().put(OAuth.VERSION, OAuth.VERSION_VALUE_10);

        // Create oauth_signature
        final String requestMethod = signedRequest.request().method();
        final HttpUrl requestUrl = signedRequest.request().httpUrl();
        final String baseUrl = requestUrl.newBuilder().query(null).fragment(null).build().toString();
        final SortedMap<String, String> signingParams = collectSignatureParameters(signedRequest.request(), signedRequest.params());

        final String signature = signatureOf(requestMethod, baseUrl, signingParams);
        signedRequest.params().put(OAuth.SIGNATURE, signature);

        return signedRequest;
    }

    private String signatureOf(String requestMethod, String baseUrl, SortedMap<String, String> signatureParams)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        // Build the parameter string
        final Buffer parameterString = new Buffer();
        for (String key : signatureParams.keySet()) {
            if (parameterString.size() > 0) {
                parameterString.writeByte('&');
            }

            parameterString.writeUtf8(key)
                    .writeByte('=')
                    .writeUtf8(signatureParams.get(key));
        }

        // Build the signature base string
        final Buffer signatureBase = new Buffer();
        signatureBase.writeUtf8(requestMethod.toUpperCase())
                .writeByte('&')
                .writeUtf8(OAuth.Encoder.encode(baseUrl))
                .writeByte('&')
                .writeUtf8(OAuth.Encoder.encode(parameterString.readUtf8()));

        // Create the signature
        final ByteString signature = ByteString.of(mac.doFinal(signatureBase.readByteArray()));

        return signature.base64();
    }

    private static SortedMap<String, String> collectSignatureParameters(Request request, SortedMap<String, String> oAuthParams)
            throws IOException {

        // copy over the initial oauth_* params
        final SortedMap<String, String> encodedSignatureParams = new TreeMap<>(oAuthParams);

        // append HTTP query params
        final HttpUrl url = request.httpUrl();
        for (int i = 0, len = url.querySize(); i < len; i++) {
            final String key = OAuth.Encoder.encode(url.queryParameterName(i)); // decode(..) ??
            final String value = OAuth.Encoder.encode(url.queryParameterValue(i)); // decode(..) ??

            encodedSignatureParams.put(key, value);
        }

        final RequestBody requestBody = request.body();
        if (requestBody.contentType().equals(FORM_CONTENT_TYPE)) {
            final Buffer body = new Buffer();
            requestBody.writeTo(body);

            while (!body.exhausted()) {
                long keyEnd = body.indexOf((byte) '=');
                if (keyEnd == -1) throw new IllegalStateException("Key with no value: " + body.readUtf8());
                String key = body.readUtf8(keyEnd);
                body.skip(1); // Equals.

                long valueEnd = body.indexOf((byte) '&');
                String value = valueEnd == -1 ? body.readUtf8() : body.readUtf8(valueEnd);
                if (valueEnd != -1) body.skip(1); // Ampersand.

                encodedSignatureParams.put(key, value); // decode(..) ???
            }
        }

        return encodedSignatureParams;
    }


    public static class DefaultConsumer implements OAuth.Consumer {

        private final String key;
        private final String secret;

        public DefaultConsumer(String key, String secret) {
            this.key = key;
            this.secret = secret;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String secret() {
            return secret;
        }
    }

    public static class DefaultToken implements OAuth.Token {

        private final String token;
        private final String secret;

        public DefaultToken(String token, String secret) {
            this.token = token;
            this.secret = secret;
        }

        @Override
        public String value() {
            return token;
        }

        @Override
        public String secret() {
            return secret;
        }
    }

    public static class DefaultNonceGenerator implements OAuth.NonceGenerator {

        public String create() {
            byte[] b = new byte[32];
            new Random().nextBytes(b);

            return ByteString.of(b).base64();
        }
    }

    public static class DefaultTimestampGenerator implements OAuth.TimestampGenerator {

        public long create() {
            return System.currentTimeMillis() / 1000L;
        }
    }


    public static class Builder {
        private String consumerKey;
        private String consumerSecret;
        private String tokenValue;
        private String tokenSecret;
        private OAuth.NonceGenerator nonce;
        private OAuth.TimestampGenerator timestamp;

        public Builder() {}

        public Builder consumer(String key, String secret) {
            consumerKey = key;
            consumerSecret = secret;

            return this;
        }

        public Builder token(String value) {
            tokenValue = value;

            return this;
        }

        public Builder token(String value, String secret) {
            tokenValue = value;
            tokenSecret = secret;

            return this;
        }

        public Builder nonce(OAuth.NonceGenerator nonce) {
            this.nonce = nonce;

            return this;
        }

        public Builder timestamp(OAuth.TimestampGenerator timestamp) {
            this.timestamp = timestamp;

            return this;
        }

        public RequestSigner build() {
            if (nonce == null) {
                nonce = new DefaultNonceGenerator();
            }
            if (timestamp == null) {
                timestamp = new DefaultTimestampGenerator();
            }
            RequestSigner rs = new RequestSigner(this);

            try {
                rs.initKey(consumerSecret, tokenSecret);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

            consumerSecret = null;
            tokenSecret = null;

            return rs;
        }
    }
}
