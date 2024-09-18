package com.mendes.task_manager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mendes.task_manager.dto.TaskDTO;
import com.mendes.task_manager.dto.TaskRequestDTO;
import com.mendes.task_manager.dto.mapper.TaskMapper;
import com.mendes.task_manager.exception.TaskNotFoundException;
import com.mendes.task_manager.model.Task;
import com.mendes.task_manager.repository.TaskRepository;

@Service
public class TaskService {
    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TaskRepository taskRepository;

    public List<TaskDTO> findAllTasks() {
        List<Task> foundTasks = taskRepository.findAll();
        return foundTasks
                .stream()
                .map(task -> taskMapper.toTaskDTO(task))
                .collect(Collectors.toList());
    };

    public TaskDTO findTaskById(Long id) {
        Task targetTask = taskRepository.findById(id)
                                        .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toTaskDTO(targetTask);
    }

    public TaskDTO saveTask(TaskRequestDTO taskRequestDTO) {
        Task taskRequest = taskMapper.toTaskEntity(taskRequestDTO);
        return taskMapper.toTaskDTO(taskRepository.save(taskRequest));
    }

    public TaskDTO updateTask(Long id, TaskRequestDTO taskRequestDTO) {
        Task targetTask = taskRepository.findById(id)
                                        .orElseThrow(() -> new TaskNotFoundException(id));
        targetTask.setTitle(taskRequestDTO.getTitle());
        targetTask.setDescription(taskRequestDTO.getDescription());
        return taskMapper.toTaskDTO(taskRepository.save(targetTask));
    };

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    };
}
