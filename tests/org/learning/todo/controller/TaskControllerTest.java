package org.learning.todo.controller;

import org.junit.jupiter.api.Test;
import org.learning.todo.exceptions.TaskNotFoundException;
import org.learning.todo.exceptions.TodoNotFoundException;
import org.learning.todo.service.TodoService;
import org.learning.todo.views.TaskCreationRequest;
import org.learning.todo.views.TaskView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureRestTestClient
class TaskControllerTest {
    @Autowired
    private RestTestClient client;

    @MockitoBean
    private TodoService todoService;

    @Test
    void shouldCreateATaskInTodo() throws TodoNotFoundException {
        TaskView expectedTaskView = new TaskView("T1", "Task 1", false);

        when(todoService.createTask("12", "Task 1")).thenReturn(expectedTaskView);

        TaskView responseBody = client.post()
                .uri("/api/todo/12/task")
                .body(new TaskCreationRequest("Task 1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskView.class)
                .returnResult()
                .getResponseBody();

        assertEquals(expectedTaskView, responseBody);
    }

    @Test
    void shouldRespondWithNotFoundForUnknownTodoWhileCreatingATask() throws TodoNotFoundException {
        when(todoService.createTask("12", "Task 1")).thenThrow(new TodoNotFoundException("12"));

        client.post()
                .uri("/api/todo/12/task")
                .body(new TaskCreationRequest("Task 1"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldToggleATaskStatusInTodo() throws TodoNotFoundException, TaskNotFoundException {
        TaskView expectedTaskView = new TaskView("T1", "Task 1", true);

        when(todoService.toggleStatus("12", "T1")).thenReturn(expectedTaskView);

        TaskView responseBody = client.patch()
                .uri("/api/todo/12/task/T1/toggleStatus")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskView.class)
                .returnResult()
                .getResponseBody();

        assertEquals(expectedTaskView, responseBody);
    }

    @Test
    void shouldRespondWithNotFoundForUnknownTodoWhileTogglingTaskStatus() throws TodoNotFoundException, TaskNotFoundException {
        when(todoService.toggleStatus("12", "T1")).thenThrow(new TodoNotFoundException("12"));

        client.patch()
                .uri("/api/todo/12/task/T1/toggleStatus")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRespondWithNotFoundForUnknownTaskWhileTogglingTaskStatus() throws TodoNotFoundException, TaskNotFoundException {
        when(todoService.toggleStatus("12", "T1")).thenThrow(new TaskNotFoundException("T1"));

        client.patch()
                .uri("/api/todo/12/task/T1/toggleStatus")
                .exchange()
                .expectStatus().isNotFound();
    }
}