package org.sam;

import org.sam.Tasks.*;
import java.util.ArrayList;
import java.util.List;

public class UtilityTaskList implements TaskListInterface {
    private List<Task> tasks;
    private samInfernalShale main;
    
    public UtilityTaskList(samInfernalShale main) {
        this.main = main;
        tasks = new ArrayList<>();
        initializeUtilityTasks();
    }
    
    private void initializeUtilityTasks() {
        tasks.add(new TimingReset(main));
        tasks.add(new Running(main));
        tasks.add(new SpecialAttack(main));
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
}