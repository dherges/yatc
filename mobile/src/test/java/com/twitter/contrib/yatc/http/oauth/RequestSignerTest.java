package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

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

    protected RequestSigner signer;

    @Before
    public void createSigner() {
        final String consumerKey = "xvz1evFS4wEEPTGEFPHBog";
        final String consumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
        final String token = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb";
        final String tokenSecret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";

        signer = new RequestSigner.Builder()
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
    }

    @Test
    public void testSignRequest() throws Exception {
        RequestBody body = new FormEncodingBuilder()
                .add("status", "Hello Ladies + Gentlemen, a signed OAuth request!")
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/1/statuses/update.json?include_entities=true")
                .post(body)
                .build();

        OAuthRequest result = signer.signRequest(new OAuthRequest.Builder().request(request).build());

        assertThat(result).isNotNull();

        assertThat(result.oauth().get("oauth_signature"))
                .isEqualTo("tnnArxj06cWHq44gCs1OSKk/jLY=");

        assertThat(result.auth())
                .isEqualTo("OAuth oauth_consumer_key=\"xvz1evFS4wEEPTGEFPHBog\", oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\", oauth_signature=\"tnnArxj06cWHq44gCs1OSKk%2FjLY%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1318622958\", oauth_token=\"370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb\", oauth_version=\"1.0\"");

    }

}
