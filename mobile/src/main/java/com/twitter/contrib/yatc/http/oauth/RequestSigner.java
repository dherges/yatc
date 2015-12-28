package com.twitter.contrib.yatc.http.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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
        final OAuthRequest.Builder signedRequest = request.newBuilder();

        // Build oauth_* params, need to percent encode all the values
        final Map<String, String> oAuthParams = new HashMap<>(request.oauth());
        oAuthParams.put(OAuth.CONSUMER_KEY, consumerKey);
        oAuthParams.put(OAuth.NONCE, nonce.create());
        oAuthParams.put(OAuth.SIGNATURE_METHOD, OAuth.SIGNATURE_METHOD_VALUE_HMAC_SHA1);
        oAuthParams.put(OAuth.TIMESTAMP, "" + timestamp.create());
        if (tokenValue != null && tokenValue.length() > 0) {
            oAuthParams.put(OAuth.TOKEN, tokenValue);
        }
        oAuthParams.put(OAuth.VERSION, OAuth.VERSION_VALUE_10);

        // Create oauth_signature
        final SortedMap<String, String> signingParams = new TreeMap<>();
        for (String key : oAuthParams.keySet()) {
            signingParams.put(key, OAuth.Encoder.encode(oAuthParams.get(key)));
        }
        signingParams.putAll(request.query());
        signingParams.putAll(request.body());

        final String signature = signatureOf(request.verb(), request.url(), signingParams);
        oAuthParams.put(OAuth.SIGNATURE, signature);
        signedRequest.params(oAuthParams);

        return signedRequest.build();
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


    public static class DefaultNonceGenerator implements OAuth.NonceGenerator {

        public String create() {
            byte[] b = new byte[32];
            new Random().nextBytes(b);

            return ByteString.of(b).hex();
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
