package com.random.challenge.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.random.challenge.dto.FileResultsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    public Cache<String, FileResultsDto> cache = CacheBuilder.newBuilder()
            .maximumSize(1500)
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();

    public void put(FileResultsDto fileResultsDto){
        cache.put(fileResultsDto.getFilename(), fileResultsDto);
    }

    public Mono<FileResultsDto> getFileResulstFromCache(String filename) {
        return Mono.justOrEmpty(cache.getIfPresent(filename));
    }

    public Mono<ConcurrentMap<String, FileResultsDto>> getAllCachedFiles(){
        return Mono.justOrEmpty(cache.asMap());
    }

}
