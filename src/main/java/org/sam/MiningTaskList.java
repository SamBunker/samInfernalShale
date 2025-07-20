package org.sam;

import org.sam.Tasks.*;
import java.util.ArrayList;
import java.util.List;

public class MiningTaskList implements TaskListInterface {
    private List<Task> tasks;
    private samInfernalShale main;
    private MiningConfig config;
    
    public MiningTaskList(samInfernalShale main, MiningConfig config) {
        this.main = main;
        this.config = config;
        tasks = new ArrayList<>();
        initializeMiningTasks();
    }
    
    private void initializeMiningTasks() {
        if (config.getMiningMethod().equals("3T Mining")) {
            tasks.add(new ThreeTick(main, config));
        } else if (config.getMiningMethod().equals("AFK Mining")) {
            tasks.add(new AfkMine(main));
        } else {
            tasks.add(new Mining(main, config));
        }
        
        tasks.add(new Crush(main));
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