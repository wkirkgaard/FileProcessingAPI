package com.random.challenge.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Configuration
@Slf4j
public class RetrofitClientConfig {

    @Bean
    public Retrofit retrofitClient(){
        log.info("Creating Retrofit Client to CALL internal/ping");
        return new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .client(new OkHttpClient()
                        .newBuilder()
                        .build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

    }

}
