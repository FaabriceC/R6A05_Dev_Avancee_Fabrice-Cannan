package com.master.air.mapper;

import com.master.air.dto.UserDTO;
import com.master.air.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User entity);
}