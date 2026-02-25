package com.master.air.mapper;

import com.master.air.dto.CategoryDTO;
import com.master.air.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category entity);
}