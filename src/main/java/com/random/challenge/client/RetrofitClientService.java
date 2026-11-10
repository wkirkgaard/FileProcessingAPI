package com.random.challenge.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Tag;

import java.util.concurrent.CompletableFuture;


public interface RetrofitClientService {

    class ListenTag{}

    @GET("/internal/ping")
    public Call<String> sendPingTest();

}
