package com.example.loom.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.loom.model.Task;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskRepository {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.insert(task);
            } catch (Exception e) {
                System.err.println("Error inserting task: " + e.getMessage());
            }
        });
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.update(task);
            } catch (Exception e) {
                System.err.println("Error updating task: " + e.getMessage());
            }
        });
    }

    public void deleteById(int taskId) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.deleteById(taskId);
            } catch (Exception e) {
                System.err.println("Error deleting task: " + e.getMessage());
            }
        });
    }

    public Task getTaskById(int taskId) {
        Future<Task> future = TaskDatabase.databaseWriteExecutor.submit(() -> taskDao.getTaskByIdSync(taskId));
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error retrieving task by ID: " + e.getMessage());
            return null;
        }
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }
}
