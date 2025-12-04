package com.example.loom.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.loom.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Query("DELETE FROM task_table WHERE id = :taskId")
    void deleteById(int taskId);

    @Query("SELECT * FROM task_table ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM task_table WHERE id = :taskId")
    Task getTaskByIdSync(int taskId);
}
