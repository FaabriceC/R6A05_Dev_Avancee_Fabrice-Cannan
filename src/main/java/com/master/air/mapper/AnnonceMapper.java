package com.master.air.mapper;

import com.master.air.dto.AnnonceCreateRequestDTO;
import com.master.air.dto.AnnonceDTO;
import com.master.air.dto.AnnonceUpdateRequestDTO;
import com.master.air.model.Annonce;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    AnnonceDTO toDto(Annonce entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "version", ignore = true)
    Annonce toEntity(AnnonceCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(AnnonceUpdateRequestDTO dto, @MappingTarget Annonce entity);
}