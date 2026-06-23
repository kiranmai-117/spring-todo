package org.learning.todo.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.learning.todo.repository.TodoRepository;
import org.learning.todo.views.TodoView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TodoServiceTest {
    @Test
    @Disabled
    void shouldCreateATodo() {
        IdGenerator idGenerator = mock(IdGenerator.class);
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoService todoService = new TodoService(idGenerator, todoRepository);

        when(idGenerator.generate()).thenReturn("TD1", "T1", "T2");

        todoService.createTodo("Office");
        todoService.createTask("TD1", "Task1");
        todoService.createTask("TD1", "Task2");
        TodoView officeTodo = todoService.getTodo("TD1");


        assertEquals("TD1", officeTodo.id());
        assertEquals("Office", officeTodo.title());
        assertEquals(2, officeTodo.tasks().size());
    }


}