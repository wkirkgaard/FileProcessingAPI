package com.random.challenge.service;

import com.random.challenge.dto.FileResultsDto;
import com.random.challenge.entities.FileResultsEntity;
import com.random.challenge.mapper.FileResultsMapper;
import com.random.challenge.repository.FileResultsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataService {

    @Autowired
    public FileResultsRepository fileResultsRepository;

    @Autowired
    public FileResultsMapper fileResultsMapper;

    @Transactional
    public Mono<FileResultsDto> findDataByFilename(ServerRequest serverRequest, CacheService cacheService) {

        String filename = serverRequest.pathVariable("filename");

        return cacheService.getFileResulstFromCache(filename)
                .switchIfEmpty(
                        Mono.fromCallable(()-> fileResultsRepository.findById(filename)
                                .stream()
                                .findFirst()
                                .map(returnedResults ->
                                        fileResultsMapper.toFileResultsDto(returnedResults))
                                .orElseThrow()
                        )
                );

    }

    public Mono<ConcurrentMap<String, FileResultsDto>> getAllRecords(CacheService cacheService) {
        return cacheService.getAllCachedFiles() // Assumed to return Mono<ConcurrentHashMap<String, FileResultsDto>>
                .switchIfEmpty(
                        Mono.fromCallable(() -> fileResultsRepository.findAll())
                                .subscribeOn(Schedulers.boundedElastic()) // Offload blocking DB call
                                .map(list -> list.stream().collect(
                                        ConcurrentHashMap::new,
                                        (map, entity) -> map.put(entity.getFilename(), fileResultsMapper.toFileResultsDto(entity)),
                                        ConcurrentHashMap::putAll
                                ))
                );
    }

    public Mono<FileResultsDto> save(FileResultsDto fileResultsDto, CacheService cacheService) {
        FileResultsEntity fileResultsEntity = fileResultsMapper.toFileResultsEntity(fileResultsDto);
        this.fileResultsRepository.save(fileResultsEntity);
        cacheService.put(fileResultsDto);
        return Mono.justOrEmpty(fileResultsDto);
    }

}
