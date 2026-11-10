package com.random.challenge.reactiveflux;

import com.random.challenge.handler.FileProcessorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@Slf4j
public class FileProcessorRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(FileProcessorHandler fileProcessorHandler){
        return RouterFunctions.route()
                .POST("files/upload", accept(MediaType.MULTIPART_FORM_DATA), fileProcessorHandler::uploadFile)
                .GET("internal/ping", fileProcessorHandler::ping)
                .GET( "file/metadata/{filename}", fileProcessorHandler::getResultsByFilename)
                .GET("files/resultsAll", fileProcessorHandler::getAllResults)
                .build();
    }


}
