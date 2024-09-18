package com.mendes.task_manager.dto;

import com.mendes.task_manager.model.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskRequestDTO {
    public TaskRequestDTO() {}

    public TaskRequestDTO(Task Task) {
        this.title = Task.getTitle();
        this.description = Task.getDescription();
    }

    @Size(max = 50, message = "The field `title` can have a maximum of {max} characters.")
    @NotBlank(message = "The field `title` cannot be empty.")
    private String title;

    @Size(max = 255, message = "The field `description` can have a maximum of {max} characters.")
    @NotBlank(message = "The field `description` cannot be empty.")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
