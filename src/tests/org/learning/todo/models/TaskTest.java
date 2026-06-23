package org.learning.todo.models;

import org.junit.jupiter.api.Test;
import org.learning.todo.views.TaskView;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    @Test
    void shouldToggleTaskStatus() {
        Task task = new Task("T1", "Buy Milk", false);
        assertFalse(task.<TaskView>project(TaskView::new).isDone());

        task.toggleStatus();
        assertTrue(task.<TaskView>project(TaskView::new).isDone());
    }
}
