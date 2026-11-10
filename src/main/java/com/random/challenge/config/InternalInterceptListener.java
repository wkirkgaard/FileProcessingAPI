package com.random.challenge.config;

import com.random.challenge.client.RetrofitClientService;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class InternalInterceptListener implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        RetrofitClientService.ListenTag tag = request.tag(RetrofitClientService.ListenTag.class);

        if (tag != null){
            System.out.println("Retrofit Intercepted a route: " + request.url());
        }

        return chain.proceed(request);

    }


}
