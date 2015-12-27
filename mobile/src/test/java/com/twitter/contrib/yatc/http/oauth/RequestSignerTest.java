package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test data from..
 *
 * @link https://dev.twitter.com/oauth/overview/authorizing-requests
 * @link https://dev.twitter.com/oauth/overview/creating-signatures
 */
public class RequestSignerTest {

    protected final String consumerKey = "xvz1evFS4wEEPTGEFPHBog";
    protected final String consumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
    protected final String token = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb";
    protected final String tokenSecret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";

    @Test
    public void testSignRequestWithTokenAndSecret() throws Exception {
        final RequestSigner signer = new RequestSigner.Builder()
                .consumer(consumerKey, consumerSecret)
                .token(token, tokenSecret)
                .nonce(new OAuth.NonceGenerator() {

                    @Override
                    public String create() {
                        return "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg";
                    }
                })
                .timestamp(new OAuth.TimestampGenerator() {

                    @Override
                    public long create() {
                        return 1318622958;
                    }
                })
                .build();

        final Request request = new Request.Builder()
                .url("https://api.twitter.com/1/statuses/update.json?include_entities=true")
                .post(new FormEncodingBuilder()
                        .add("status", "Hello Ladies + Gentlemen, a signed OAuth request!")
                        .build())
                .build();

        final OAuthRequest oAuthRequest = new OAuthRequest.Builder()
                .request(request)
                .build();

        final OAuthRequest signed = signer.signRequest(oAuthRequest);

        assertThat(signed).isNotNull();

        assertThat(signed.oauth().get("oauth_signature"))
                .isEqualTo("tnnArxj06cWHq44gCs1OSKk/jLY=");

        assertThat(signed.auth())
                .isEqualTo("OAuth oauth_consumer_key=\"xvz1evFS4wEEPTGEFPHBog\""
                             + ", oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\""
                             + ", oauth_signature=\"tnnArxj06cWHq44gCs1OSKk%2FjLY%3D\""
                             + ", oauth_signature_method=\"HMAC-SHA1\""
                             + ", oauth_timestamp=\"1318622958\""
                             + ", oauth_token=\"370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb\""
                             + ", oauth_version=\"1.0\"");
    }

    @Test
    public void testSignRequestWithConsumerKeySecret() throws Exception {
        final RequestSigner signer = new RequestSigner.Builder()
                .consumer("cChZNFj6T5R0TigYB9yd1w", "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg")
                .nonce(new OAuth.NonceGenerator() {

                    @Override
                    public String create() {
                        return "ea9ec8429b68d6b77cd5600adbbb0456";
                    }
                })
                .timestamp(new OAuth.TimestampGenerator() {

                    @Override
                    public long create() {
                        return 1318467427;
                    }
                })
                .build();

        final OAuthRequest oAuthRequest = new OAuthRequest.Builder()
                .requestToken("POST", "https://api.twitter.com/oauth/request_token", "http://localhost/sign-in-with-twitter/")
                .build();

        final OAuthRequest signed = signer.signRequest(oAuthRequest);

        assertThat(signed).isNotNull();

        assertThat(signed.oauth().get("oauth_signature"))
                .isEqualTo("F1Li3tvehgcraF8DMJ7OyxO4w9Y=");
    }

}
