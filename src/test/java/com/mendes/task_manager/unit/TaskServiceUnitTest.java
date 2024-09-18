package com.mendes.task_manager.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mendes.task_manager.dto.TaskDTO;
import com.mendes.task_manager.dto.TaskRequestDTO;
import com.mendes.task_manager.dto.mapper.TaskMapper;
import com.mendes.task_manager.exception.TaskNotFoundException;
import com.mendes.task_manager.model.Task;
import com.mendes.task_manager.repository.TaskRepository;
import com.mendes.task_manager.service.TaskService;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUnitTest {
    
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    
    @BeforeEach
    public void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("title 1");
        task1.setDescription("description 1");
        task1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        task1.setLastUpdatedAt(task1.getCreatedAt());
    }

    @Test
    public void whenFindingAllTasks_thenTaskDTOListShouldBeReturned() {
        Task task2 = new Task();
            task2.setId(2L);
            task2.setTitle("title 2");
            task2.setDescription("description 2");
            task2.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 1));
            task2.setLastUpdatedAt(task2.getCreatedAt());

        TaskDTO taskDTO1 = new TaskDTO(task1);
        TaskDTO taskDTO2 = new TaskDTO(task2);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));
        when(taskMapper.toTaskDTO(task1)).thenReturn(taskDTO1);
        when(taskMapper.toTaskDTO(task2)).thenReturn(taskDTO2);

        List<TaskDTO> result = taskService.findAllTasks();

        verify(taskRepository, times(1)).findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(taskDTO1, result.get(0));
        assertEquals(taskDTO2, result.get(1));
    }

    @Test
    void whenNoTaskWasFound_thenEmptyListShouldBeReturned() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList());

        List<TaskDTO> result = taskService.findAllTasks();

        verify(taskRepository, times(1)).findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void whenTaskIsFoundById_thenTaskDTOShouldBeReturned() {
        final Long ID = 1L;
        when(taskRepository.findById(ID)).thenReturn(Optional.of(task1));

        TaskDTO taskDTO1 = new TaskDTO(task1);
        when(taskMapper.toTaskDTO(task1)).thenReturn(taskDTO1);
        
        TaskDTO result = taskService.findTaskById(ID);
        
        verify(taskRepository, times(1)).findById(ID);
        verify(taskMapper, times(1)).toTaskDTO(task1);
        
        assertNotNull(result);
        assertEquals(taskDTO1, result);
    }

    @Test
    void whenTaskIsNotFound_thenNoTaskFoundExceptionShouldBeRaised() {
        final Long ID = 0L;

        when(taskRepository.findById(ID)).thenReturn(Optional.empty());
    
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, 
                                                            () -> taskService.findTaskById(ID));

        verify(taskRepository, times(1)).findById(ID);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Task with ID %s not found.", ID));
    }

    @Test
    void whenTaskIsSaved_thenTaskDTOShouldBeReturned() {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(task1);
        when(taskMapper.toTaskEntity(taskRequestDTO)).thenReturn(task1);

        TaskDTO taskDTO1 = new TaskDTO(task1);
        when(taskMapper.toTaskDTO(task1)).thenReturn(taskDTO1);

        when(taskRepository.save(task1)).thenReturn(task1);

        TaskDTO result = taskService.saveTask(taskRequestDTO);

        verify(taskMapper, times(1)).toTaskEntity(taskRequestDTO);
        verify(taskRepository, times(1)).save(task1);
        verify(taskMapper, times(1)).toTaskDTO(task1);

        assertNotNull(result);
        assertEquals(taskDTO1, result);
    }

    @Test
    void whenTaskIsUpdated_thenUpdatedTaskDTOShouldBeReturned() {
        final Long ID = 1L;
        final String UPDATED_TITLE = "New title";

        TaskRequestDTO requestDTO = new TaskRequestDTO();
            requestDTO.setTitle(UPDATED_TITLE);
            requestDTO.setDescription(task1.getDescription());

        Task updatedTask = new Task();
            updatedTask.setId(task1.getId());
            updatedTask.setTitle(requestDTO.getTitle());
            updatedTask.setDescription(requestDTO.getDescription());
            updatedTask.setCreatedAt(task1.getCreatedAt());
            updatedTask.setLastUpdatedAt(task1.getCreatedAt().plusMinutes(1));

        TaskDTO updatedTaskDTO = new TaskDTO(updatedTask);

        when(taskRepository.findById(ID)).thenReturn(Optional.of(task1));
        when(taskRepository.save(task1)).thenReturn(updatedTask);
        when(taskMapper.toTaskDTO(updatedTask)).thenReturn(updatedTaskDTO);
       
        TaskDTO result = taskService.updateTask(ID, requestDTO);

        verify(taskMapper, times(1)).toTaskDTO(updatedTask);
        
        assertNotNull(result);
        assertEquals(updatedTaskDTO, result);
    }

    @Test
    void whenDeletingTaskById_thenRepositoryDeleteByIdShouldBeCalled() {
        final Long ID = 1L;

        taskService.deleteTaskById(ID);

        verify(taskRepository, times(1)).deleteById(ID);
    }
}
