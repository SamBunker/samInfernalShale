package org.sam;

public interface TaskListInterface {
    boolean executeNextTask();
    void addTask(Task task);
    void clearTasks();
    boolean hasActiveTasks();
    String getCurrentTaskName();
}