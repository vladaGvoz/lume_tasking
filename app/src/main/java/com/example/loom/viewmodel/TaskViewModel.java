package com.example.loom.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loom.data.TaskRepository;
import com.example.loom.model.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final LiveData<List<Task>> allTasks;
    private final MutableLiveData<Task> taskToEdit = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    // crud wrappers

    public void insert(Task task) {
        repository.insert(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void deleteById(int taskId) {
        repository.deleteById(taskId);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<Task> getTaskById(int taskId) {
        Task fetchedTask = repository.getTaskById(taskId);
        taskToEdit.postValue(fetchedTask);
        return taskToEdit;
    }
//
//    public LiveData<Task> getTaskToEdit() {
//        return taskToEdit;
//    }
}
