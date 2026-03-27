package com.study.study_planning_platform.mapper;

import com.study.study_planning_platform.dto.request.TaskRequestDTO;
import com.study.study_planning_platform.dto.response.TaskResponseDTO;
import com.study.study_planning_platform.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "completedAt",  ignore = true)
    Task toEntity(TaskRequestDTO requestDTO);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.categoryName")
    TaskResponseDTO  toResponseDTO(Task task);

    List<TaskResponseDTO>  toResponseListDTO(List<Task> tasks);
}
