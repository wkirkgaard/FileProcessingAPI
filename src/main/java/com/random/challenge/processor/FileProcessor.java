package com.random.challenge.processor;

import com.random.challenge.dto.FileResultsDto;
import com.random.challenge.entities.FileResultsEntity;
import com.random.challenge.service.CacheService;
import com.random.challenge.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileProcessor {

    static Pattern wordPattern = Pattern.compile("\\p{L}+");
    static Pattern camelCaseSplitPattern = Pattern.compile("(?<=\\p{Ll})(?=\\p{Lu})|(?<=\\p{Lu})(?=\\p{Lu}\\p{Ll})");

    public static void words(List<String> wordList,List<String> fileData) {

        for (String line : fileData) {
            Matcher matcher = wordPattern.matcher(line);
            while (matcher.find()) {
                String token = matcher.group();

                // Only split if it's an abnormally long unspaced block (protects iPhone, YouTube, etc.)
                if (token.length() > 20 && !token.contains(" ")) {
                    String[] parts = camelCaseSplitPattern.split(token);
                    for (String part : parts) {
                        if (!part.isEmpty()) {
                            wordList.add(part);
                        }
                    }
                } else {
                    // Normal words and brand names stay completely intact
                    wordList.add(token);
                }
            }
        }
    }



    int previousLength = 0;


    public void parseToDTO(FileResultsDto fileResultsDto, List<String> fileData) {
        //cache byte data from file read

        ArrayList<String> wordList = new ArrayList<>();
        FileProcessor.words(wordList, fileData);

        fileResultsDto.setWordCount(wordList.size());
        fileResultsDto.setUniqueWords(new HashSet<>(wordList)
                .size());

        fileData.stream().filter(line -> line.length() > fileResultsDto.getLongestLine().length())
                .forEachOrdered(line -> {
                    fileResultsDto.setLongestLine(line);
                    previousLength = line.length();
                });

        fileResultsDto.setSizeOfBiggestWord(wordList.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0));

        // Get the length of the shortest word(s)
        fileResultsDto.setSizeOfSmallestWord(wordList.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0));

        fileResultsDto.setWordList(wordList.toArray(String[]::new));

    }


    public Mono<FileResultsDto> processFile(FilePart filePart, MetadataService metadataService, CacheService cacheService) {
        FileResultsDto fileResultsDto = new FileResultsDto(filePart.filename());

        return cacheService.getFileResulstFromCache(fileResultsDto.getFilename())
                // If cache hits, we just return the cached DTO directly
                .switchIfEmpty(Mono.defer(() -> filePart.content()
                        .next() // Grab the first DataBuffer
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("File content is empty")))
                        .flatMap(dataBuffer -> {
                            try {
                                int dataSize = 4096;
                                DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

                                // Read InputStream and release buffers cleanly
                                Flux<String> dataBufferFlux = DataBufferUtils.readInputStream(
                                        () -> dataBuffer.asInputStream(true),
                                        dataBufferFactory,
                                        dataSize
                                ).map(buffer -> {
                                    try {
                                        byte[] bytes = new byte[buffer.readableByteCount()];
                                        buffer.read(bytes);
                                        return new String(bytes, StandardCharsets.UTF_8);
                                    } finally {
                                        DataBufferUtils.release(buffer);
                                    }
                                });

                                return dataBufferFlux.collectList()
                                        .flatMap(chunks -> {
                                            try {
                                                String fullContent = String.join("", chunks);
                                                List<String> fileData = Arrays.asList(fullContent.split("\\r?\\n"));
                                                parseToDTO(fileResultsDto, fileData);
                                                return metadataService.save(fileResultsDto, cacheService)
                                                        .subscribeOn(Schedulers.boundedElastic());
                                            } catch (Exception exception) {
                                                return Mono.error(exception);
                                            }
                                        });
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        })
                ));
    }

}


