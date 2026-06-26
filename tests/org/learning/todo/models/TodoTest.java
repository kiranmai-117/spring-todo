package org.learning.todo.models;

import org.junit.jupiter.api.Test;
import org.learning.todo.exceptions.TaskNotFoundException;
import org.learning.todo.views.TaskView;
import org.learning.todo.views.TodoView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TodoTest {
    @Test
    void shouldAddATask() {
        Todo todo = new Todo("1", "Project Todo");
        todo.addTask(new Task("T1", "Test story #123", false));

        TaskView expectedTask = new TaskView("T1", "Test story #123", false);
        assertEquals(expectedTask, todo.project(TodoView::new, TaskView::new).tasks().get(0));
    }

    @Test
    void shouldToggleATaskStatus() {
        Todo todo = new Todo("1", "Project Todo");
        todo.addTask(new Task("T1", "Test story #123", false));

        assertEquals(new TaskView("T1", "Test story #123", true), todo.toggleStatus("T1", TaskView::new));
        assertEquals(new TaskView("T1", "Test story #123", false), todo.toggleStatus("T1", TaskView::new));
    }

    @Test
    void shouldErrorOnUnknownTaskToggleStatus() {
        Todo todo = new Todo("Todo1", "Office");

        assertThrows(TaskNotFoundException.class, () -> todo.toggleStatus("T1", TaskView::new));
    }

    @Test
    void shouldReturnATodoView() {
        Todo todo = new Todo("Todo1", "Office");
        todo.addTask(new Task("T1", "Task1", false));
        todo.addTask(new Task("T2", "Task2", false));

        TodoView actualTodoView = todo.project(TodoView::new, TaskView::new);
        TodoView expectedTodoView = new TodoView("Todo1",
                "Office",
                List.of(
                        new TaskView("T1", "Task1", false),
                        new TaskView("T2", "Task2", false)
                ));

        assertEquals(expectedTodoView, actualTodoView);
    }
}
