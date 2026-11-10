package com.random.challenge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class FileResultsDto {

    public String filename;
    public String fileExtension;
    public int wordCount;
    public int uniqueWords;
    public String longestLine;
    public int lengthOfLongestLine;
    public int sizeOfBiggestWord;
    public int sizeOfSmallestWord;
    public String[] wordList;

    public FileResultsDto(String filename){
        this.filename = filename.split("\\.")[0];
        this.fileExtension = filename.split("\\.")[1];
        this.longestLine = "";
    }

}
