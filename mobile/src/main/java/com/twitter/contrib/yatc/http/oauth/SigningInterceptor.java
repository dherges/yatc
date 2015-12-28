package com.twitter.contrib.yatc.http.oauth;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SigningInterceptor implements Interceptor {

    protected RequestSigner requestSigner;

    public void signer(RequestSigner signer) {
        this.requestSigner = signer;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        if (requestSigner == null) { // skip, if request signer is not yet configured
            return chain.proceed(chain.request());
        }

        OAuthRequest req = new OAuthRequest.Builder()
                .request(chain.request())
                .build();

        try {
            OAuthRequest signed = requestSigner.signRequest(req);

            return chain.proceed(signed.request());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (InvalidKeyException e) {
            throw new IOException(e);
        }
    }

}
