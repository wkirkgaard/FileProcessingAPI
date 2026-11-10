package com.random.challenge.handler;

import com.random.challenge.processor.FileProcessor;
import com.random.challenge.service.CacheService;
import com.random.challenge.service.MetadataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileProcessorHandler {

    @Autowired
    FileProcessor fileProcessor;

    @Autowired
    CacheService cacheService;

    @Autowired
    MetadataService metadataService;


    public Mono<ServerResponse> uploadFile(ServerRequest serverRequest) {
        return serverRequest.multipartData()
                .map(multipartMap -> multipartMap.getFirst("file"))
                .ofType(FilePart.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Missing or invalid 'file' part")))
                .flatMap(filePart ->
                        fileProcessor.processFile(filePart, metadataService, cacheService)
                )
                .flatMap(fileResultsDto -> ServerResponse.ok().bodyValue(fileResultsDto))
                .onErrorResume(ex -> {
                    log.error("Error uploading file", ex);
                    String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown error";
                    return ServerResponse.badRequest().bodyValue(errorMessage);
                })
                .log("Returning response! UPLOAD");
    }


    public Mono<ServerResponse> ping(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Ping test Complete!")
                .log("Returning response! PING");
    }

    public Mono<ServerResponse> getResultsByFilename(ServerRequest serverRequest) {

        return metadataService.findDataByFilename(serverRequest, cacheService)
                .flatMap(
                        fileResults -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(fileResults)
                                .switchIfEmpty(ServerResponse.notFound()
                                        .build()));

    }

    public Mono<ServerResponse> getAllResults(ServerRequest serverRequest) {
        return metadataService.getAllRecords(cacheService)
                .flatMap(
                        response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                                .switchIfEmpty(ServerResponse.noContent()
                                        .build()));

    }
}
