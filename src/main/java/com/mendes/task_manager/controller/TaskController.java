package com.mendes.task_manager.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mendes.task_manager.dto.TaskDTO;
import com.mendes.task_manager.dto.TaskRequestDTO;
import com.mendes.task_manager.service.TaskService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> dtos = taskService.findAllTasks();
        return ResponseEntity.ok().body(dtos);
    };

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable @Positive(message = "ID must be a positive Integer.") Long id) {
        TaskDTO dto = taskService.findTaskById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskRequestDTO taskRequestDTO) {
        TaskDTO dto = taskService.saveTask(taskRequestDTO);

        URI taskUri = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(dto.getId())
                            .toUri();
        
        return ResponseEntity.created(taskUri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable @Positive(message = "ID must be a positive Integer.") Long id, 
                                                        @RequestBody @Valid TaskRequestDTO taskRequestDTO) {
        TaskDTO dto = taskService.updateTask(id, taskRequestDTO);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable @Positive(message = "ID must be a positive Integer.") Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }
}
