package com.random.challenge.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "FileProcessor")
@Getter
@Setter
public class FileResultsEntity {

    @Id
    @GeneratedValue
    public int id;

    @Column(unique = true)
    public String filename;
    public String fileExtension;
    public int wordCount;
    public int uniqueWords;
    public String longestLine;
    public int lengthOfLongestLine;
    public int sizeOfBiggestWord;
    public int sizeOfSmallestWord;
    public String[] wordList;

}
