package com.random.challenge.mapper;

import com.random.challenge.dto.FileResultsDto;
import com.random.challenge.entities.FileResultsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy =  ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileResultsMapper {


    FileResultsEntity toFileResultsEntity(FileResultsDto fileResultsDto);

    FileResultsDto toFileResultsDto(FileResultsEntity fileResultsEntity);

}
