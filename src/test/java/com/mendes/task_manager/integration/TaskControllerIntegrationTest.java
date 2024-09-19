package com.mendes.task_manager.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendes.task_manager.dto.TaskRequestDTO;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskControllerIntegrationTest {

    final String PATH_END_POINT = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenGetInvalidResource_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/invalidURI"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("No static resource invalidURI."));
    }

    @Test
    @Sql(scripts = "/data/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void whenGellAllTasks_thenReturnOk() throws Exception {
        mockMvc.perform(get(PATH_END_POINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test title 1"))
                .andExpect(jsonPath("$[0].description").value("Test description 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Test title 2"))
                .andExpect(jsonPath("$[1].description").value("Test description 2"))
                .andExpect(jsonPath("$[2].id").doesNotExist());
    }

    @Test
    @Sql(scripts = "/data/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void whenGetTaskById_thenReturnOk() throws Exception {
        final Long ID = 2L;
        mockMvc.perform(get(String.format("%s/%s", PATH_END_POINT, ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value("Test title 2"))
                .andExpect(jsonPath("$.description").value("Test description 2"));
    }
    
    @Test
    public void whenGetTaskByIdWithInvalidId_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", PATH_END_POINT, -1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("ID must be a positive Integer."));

        mockMvc.perform(get(String.format("%s/%s", PATH_END_POINT, "InvalidID")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("ID must be a positive Integer."));
    }

    @Test
    public void whenGetTaskByIdWithTaskNotFound_thenReturnNotFound() throws Exception {
        final Long ID = 1L;
        mockMvc.perform(get(String.format("%s/%s", PATH_END_POINT, ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value(String.format("Task with ID %s not found.", ID)));
    }

    @Test
    public void whenPostTask_thenReturnCreated() throws Exception{
        final Long ID = 1L;
        final String LOCATION = String.format("http://localhost%s/%s", PATH_END_POINT, ID); // No port is defined since the rest api is not running 

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title");
            taskRequestDTO.setDescription("Test description");

            mockMvc.perform(post(PATH_END_POINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", LOCATION))
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.title").value(taskRequestDTO.getTitle()))
                    .andExpect(jsonPath("$.description").value(taskRequestDTO.getDescription()));
    }

    @Test
    public void whenPostTaskWithMissingField_thenReturnBadRequest() throws Exception {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title");

        mockMvc.perform(post(PATH_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("The field `description` cannot be empty."));
    }

    @Test
    public void whenPostTaskWithInvalidField_thenReturnBadRequest() throws Exception {
        final Integer MAX = 50;
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ");
            taskRequestDTO.setDescription("Test description");

        mockMvc.perform(post(PATH_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(String.format("The field `title` can have a maximum of %s characters.", MAX)));
    }

    @Test
    @Sql(scripts = "/data/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void whenUpdateTask_thenReturnOK() throws Exception{
        final Long ID = 1L;
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title 1");
            taskRequestDTO.setDescription("Test new description");

            mockMvc.perform(put(String.format("%s/%s", PATH_END_POINT, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.title").value(taskRequestDTO.getTitle()))
                    .andExpect(jsonPath("$.description").value(taskRequestDTO.getDescription()));
    }

    @Test
    public void whenUpdateTaskWithMissingField_thenReturnBadRequest() throws Exception {
        final Long ID = 1L;
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title");

        mockMvc.perform(put(String.format("%s/%s", PATH_END_POINT, ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("The field `description` cannot be empty."));
    }

    @Test
    public void whenUpdateTaskWithInvalidField_thenReturnBadRequest() throws Exception {
        final Long ID = 1L;
        final Integer MAX = 50;
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ");
            taskRequestDTO.setDescription("Test description");

        mockMvc.perform(put(String.format("%s/%s", PATH_END_POINT, ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(String.format("The field `title` can have a maximum of %s characters.", MAX)));
    }

    @Test
    public void whenUpdateTaskByIdWithInvalidId_thenReturnBadRequest() throws Exception {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title");
            taskRequestDTO.setDescription("Test description");
            
        mockMvc.perform(put(String.format("%s/%s", PATH_END_POINT, "-1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("ID must be a positive Integer."));

        mockMvc.perform(put(String.format("%s/%s", PATH_END_POINT, "invalidID"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("ID must be a positive Integer."));
    }

    @Test
    public void whenUpdateTaskByIdWithTaskNotFound_thenReturnNotFound() throws Exception {
        final Long ID = 1L;

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
            taskRequestDTO.setTitle("Test title");
            taskRequestDTO.setDescription("Test description");

        mockMvc.perform(put(String.format("%s/%s", PATH_END_POINT, ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value(String.format("Task with ID %s not found.", ID)));
    }

    @Test
    public void whenDeleteTaskById_thenReturnNoContent() throws Exception {
        final Long ID = 1L;
        mockMvc.perform(delete(String.format("%s/%s", PATH_END_POINT, ID)))
                .andExpect(status().isNoContent());
    }
 
    @Test
    public void whenDeleteTaskByIdWithInvalidId_thenReturnBadRequest() throws Exception {
        mockMvc.perform(delete(PATH_END_POINT + "/InvalidID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("ID must be a positive Integer."));
    }

}
