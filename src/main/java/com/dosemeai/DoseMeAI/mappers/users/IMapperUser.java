package com.dosemeai.DoseMeAI.mappers.users;

import com.dosemeai.DoseMeAI.domain.users.UserDtoRequest;
import com.dosemeai.DoseMeAI.domain.users.UserDtoResponse;
import com.dosemeai.DoseMeAI.domain.users.UserModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IMapperUser {
    UserModel toModel(UserDtoRequest dtoRequest);
    UserDtoResponse toDto(UserModel userModel);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(UserDtoRequest dtoRequest, UserModel userModel);
}
