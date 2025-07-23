package org.sam;

import org.sam.Tasks.*;
import java.util.ArrayList;
import java.util.List;

public class MovementTaskList implements TaskListInterface {
    private List<Task> tasks;
    private samInfernalShale main;
    private MiningConfig config;
    
    public MovementTaskList(samInfernalShale main, MiningConfig config) {
        this.main = main;
        this.config = config;
        tasks = new ArrayList<>();
        initializeMovementTasks();
    }
    
    private void initializeMovementTasks() {
        tasks.add(new GoToRocks(main, config));
    }
    
    @Override
    public boolean executeNextTask() {
        for (Task task : tasks) {
            if (task.activate()) {
                task.execute();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }
    
    @Override
    public void clearTasks() {
        tasks.clear();
    }
    
    @Override
    public boolean hasActiveTasks() {
        return tasks.stream().anyMatch(Task::activate);
    }
    
    @Override
    public String getCurrentTaskName() {
        for (Task task : tasks) {
            if (task.activate()) {
                return task.name;
            }
        }
        return null;
    }
    
    @Override
    public List<Task> getTasks() {
        return tasks;
    }
}