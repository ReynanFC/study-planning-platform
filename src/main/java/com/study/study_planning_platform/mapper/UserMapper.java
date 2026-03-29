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
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserRegistrationRequestDTO requestDTO);

    UserResponseDTO toResponseDTO(User user);
}
