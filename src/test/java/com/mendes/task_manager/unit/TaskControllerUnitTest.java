package com.mendes.task_manager.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.mendes.task_manager.controller.TaskController;
import com.mendes.task_manager.dto.TaskDTO;
import com.mendes.task_manager.dto.TaskRequestDTO;
import com.mendes.task_manager.service.TaskService;

@ExtendWith(MockitoExtension.class)
public class TaskControllerUnitTest {

    final Long ID = 1L;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO taskDTO1;
        
    @BeforeEach
    public void setUp() {
        taskDTO1 = new TaskDTO();
        taskDTO1.setId(1L);
        taskDTO1.setTitle("title 1");
        taskDTO1.setDescription("description 1");
        taskDTO1.setCreatedAt(LocalDateTime.of(2024, 8, 1, 0, 0, 0));
        taskDTO1.setLastUpdatedAt(taskDTO1.getCreatedAt());
    }

    @Test
    public void whenGetAllTasks_thenReturnAllTasks() {
        TaskDTO taskDTO2 = new TaskDTO();
            taskDTO2.setId(2L);
            taskDTO2.setTitle("title 2");
            taskDTO2.setDescription("description 2");
            taskDTO2.setCreatedAt(LocalDateTime.of(2024, 8, 1, 0, 0, 0));
            taskDTO2.setLastUpdatedAt(taskDTO2.getCreatedAt());

        List<TaskDTO> tasksDTOList = Arrays.asList(taskDTO1, taskDTO2);

        when(taskService.findAllTasks()).thenReturn(tasksDTOList);

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(tasksDTOList, response.getBody());
    }

    @Test
    public void whenGetTaskById_thenReturnTask() {
        when(taskService.findTaskById(ID)).thenReturn(taskDTO1);

        ResponseEntity<TaskDTO> result = taskController.getTaskById(ID);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(taskDTO1, result.getBody());
    }

    @Test
    public void whenPostTask_thenReturnCreatedTask() {
        final Long ID_NEW_LOCATION = 3L;
        final String LOCATION = String.format("http://localhost:8080/api/tasks/%s", ID_NEW_LOCATION);

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title");
            taskRequestDTO.setDescription("Test description");


        TaskDTO taskResponseDTO = new TaskDTO();
            taskResponseDTO.setId(ID_NEW_LOCATION);
            taskResponseDTO.setDescription(taskRequestDTO.getDescription());
            taskResponseDTO.setCreatedAt(LocalDateTime.now());
            taskResponseDTO.setLastUpdatedAt(taskResponseDTO.getCreatedAt());

        when(taskService.saveTask(taskRequestDTO)).thenReturn(taskResponseDTO);

        mockStatic(ServletUriComponentsBuilder.class);
        ServletUriComponentsBuilder ServletUriComponentsBuilderMock = mock(ServletUriComponentsBuilder.class);
        UriComponentsBuilder uriComponentsBuilderMock = mock(UriComponentsBuilder.class);
        UriComponents uriComponentsMock = mock(UriComponents.class);

        when(ServletUriComponentsBuilder.fromCurrentRequest()).thenReturn(ServletUriComponentsBuilderMock);
        when(ServletUriComponentsBuilderMock.path("/{id}")).thenReturn(uriComponentsBuilderMock);
        when(uriComponentsBuilderMock.buildAndExpand(ID_NEW_LOCATION)).thenReturn(uriComponentsMock);
        when(uriComponentsMock.toUri()).thenReturn(URI.create(LOCATION));

        ResponseEntity<TaskDTO> response = taskController.createTask(taskRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(taskResponseDTO, response.getBody());
        assertEquals(LOCATION, response.getHeaders().get("Location").get(0));
    
}

    @Test
    public void whenUpdatTask_thenReturnUpdatedTask() {
        final String UPDATED_NAME = "New title";

        TaskRequestDTO requestDTO = new TaskRequestDTO();
            requestDTO.setTitle(UPDATED_NAME);
            requestDTO.setDescription(taskDTO1.getDescription());

        TaskDTO updatedTaskDTO = new TaskDTO();
            updatedTaskDTO.setId(taskDTO1.getId());
            updatedTaskDTO.setTitle(requestDTO.getTitle());
            updatedTaskDTO.setDescription(requestDTO.getDescription());
            updatedTaskDTO.setCreatedAt(taskDTO1.getCreatedAt());
            updatedTaskDTO.setLastUpdatedAt(LocalDateTime.now());
            
        when(taskService.updateTask(ID, requestDTO)).thenReturn(updatedTaskDTO);

        ResponseEntity<TaskDTO> result = taskController.updateTask(ID, requestDTO);
    
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updatedTaskDTO, result.getBody());
    }

    @Test
    public void whenDeleteTask_thenReturnNoContent() {
        ResponseEntity<Void> result = taskController.deleteTaskById(ID);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
    }

}
