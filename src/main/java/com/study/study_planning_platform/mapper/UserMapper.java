package com.study.study_planning_platform.mapper;

import com.study.study_planning_platform.dto.request.UserLoginRequestDTO;
import com.study.study_planning_platform.dto.request.UserRegistrationRequestDTO;
import com.study.study_planning_platform.dto.response.UserResponseDTO;
import com.study.study_planning_platform.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "categories", ignore = true)
    User toEntity(UserRegistrationRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserLoginRequestDTO requestDTO);

    UserResponseDTO toResponseDTO(User user);
}
