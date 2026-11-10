package com.random.challenge.service;

import com.random.challenge.client.RetrofitClientService;
import com.random.challenge.config.RetrofitClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetrofitCallService {

    @Autowired
    public Retrofit retrofitClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initRetrofitCalls(){

        if (this.retrofitClient == null){
            this.retrofitClient = new RetrofitClientConfig().retrofitClient();
        }

        RetrofitClientService retrofitCallService = this.retrofitClient.create(RetrofitClientService.class);

        Call<String> response = retrofitCallService.sendPingTest();

        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                log.info(response.code() + " | " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                log.error(throwable.getMessage());
            }
        });

    }

}
