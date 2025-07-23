package org.sam;

import java.util.List;

public interface TaskListInterface {
    boolean executeNextTask();
    void addTask(Task task);
    void clearTasks();
    boolean hasActiveTasks();
    String getCurrentTaskName();
    List<Task> getTasks();
}