package com.mendes.task_manager.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mendes.task_manager.dto.TaskDTO;
import com.mendes.task_manager.dto.TaskRequestDTO;
import com.mendes.task_manager.model.Task;

@Component
public class TaskMapper {
    @Autowired
    private ModelMapper mapper;

    public Task toTaskEntity(TaskRequestDTO taskDTO) {
        Task task = mapper.map(taskDTO, Task.class);
        return task;
    }

    public TaskDTO toTaskDTO(Task taskEntity) {
        TaskDTO dto = mapper.map(taskEntity, TaskDTO.class);
        return dto;
    }
}
