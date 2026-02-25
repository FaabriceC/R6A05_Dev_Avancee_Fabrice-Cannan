package com.master.air.mapper;

import com.master.air.dto.AnnonceCreateDTO;
import com.master.air.dto.AnnonceDTO;
import com.master.air.dto.AnnoncePatchDTO;
import com.master.air.model.Annonce;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.label", target = "categoryLabel")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorUsername")
    AnnonceDTO toDTO(Annonce annonce);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    Annonce toEntity(AnnonceCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateFromPatch(AnnoncePatchDTO dto, @MappingTarget Annonce annonce);
}
