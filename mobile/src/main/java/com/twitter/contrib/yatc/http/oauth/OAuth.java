package com.twitter.contrib.yatc.http.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Common OAuth protocol values and abstractions
 */
public interface OAuth {

    String CALLBACK = "oauth_callback";
    String CONSUMER_KEY = "oauth_consumer_key";
    String NONCE = "oauth_nonce";
    String SIGNATURE = "oauth_signature";
    String SIGNATURE_METHOD = "oauth_signature_method";
    String TIMESTAMP = "oauth_timestamp";
    String TOKEN = "oauth_token";
    String VERIFIER = "oauth_verifier";
    String VERSION = "oauth_version";


    String VERSION_VALUE_10 = "1.0";
    String SIGNATURE_METHOD_VALUE_HMAC_SHA1 = "HMAC-SHA1";

    /** OAuth consumer */
    interface Consumer {

        String key();

        String secret();
    }

    /** OAuth token, either request token or access token */
    interface Token {

        String value();

        String secret();
    }

    /** Provider creating nonce sequences */
    interface NonceGenerator {

        String create();
    }

    /** Provider creating timestamps */
    interface TimestampGenerator {

        long create();
    }


    class Encoder {

        public static String encode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) { // this simply MUST NOT fail
                return s;
            }
        }
    }
}
