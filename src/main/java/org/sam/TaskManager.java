package org.sam;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private MiningTaskList miningTasks;
    private InventoryTaskList inventoryTasks;
    private UtilityTaskList utilityTasks;
    private MovementTaskList movementTasks;
    
    private List<TaskListInterface> taskLists;
    private samInfernalShale main;
    
    public TaskManager(samInfernalShale main, MiningConfig config) {
        this.main = main;
        
        miningTasks = new MiningTaskList(main, config);
        inventoryTasks = new InventoryTaskList(main, config);
        utilityTasks = new UtilityTaskList(main);
        movementTasks = new MovementTaskList(main, config);
        
        taskLists = new ArrayList<>();
        taskLists.add(utilityTasks);
        taskLists.add(movementTasks);
        taskLists.add(inventoryTasks);
        taskLists.add(miningTasks);
    }
    
    public void executeTask() {
        for (TaskListInterface taskList : taskLists) {
            if (taskList.executeNextTask()) {
                return;
            }
        }
    }
    
    public String getCurrentTaskName() {
        for (TaskListInterface taskList : taskLists) {
            String taskName = taskList.getCurrentTaskName();
            if (taskName != null) {
                return taskName;
            }
        }
        return "Idle";
    }
    
    public MiningTaskList getMiningTasks() {
        return miningTasks;
    }
    
    public InventoryTaskList getInventoryTasks() {
        return inventoryTasks;
    }
    
    public UtilityTaskList getUtilityTasks() {
        return utilityTasks;
    }
    
    public MovementTaskList getMovementTasks() {
        return movementTasks;
    }
}