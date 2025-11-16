package com.app.todo;

import com.app.todo.models.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskModelTest {
    @Test
    void testTaskConstructorAndGetters() {
        Task task = new Task("Write tests", false);
        assertEquals("Write tests", task.getTask());
        assertFalse(task.isCompleted());
    }

    @Test
    void testSetters() {
        Task task = new Task();
        task.setTask("Review code");
        task.setCompleted(true);
        assertEquals("Review code", task.getTask());
        assertTrue(task.isCompleted());
    }
}