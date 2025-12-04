package com.example.loom.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Defines the Task entity, representing a single row in the 'task_table'.
 */
@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private boolean isCompleted;

    private long dueDate;   // <-- renamed field

    public Task(String title, String description, boolean isCompleted, long dueDate) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public boolean isCompleted() { return isCompleted; }

    public long getDueDate() { return dueDate; }  // <-- new getter
}
