package com.hua.it2023105.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "task_status")
public class TaskStatus {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "status_name")
    private String statusName;

    // Constructor
    public TaskStatus(String statusName) {
        this.statusName = statusName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    // Status constants
    public static final String RECORDED = "recorded";
    public static final String IN_PROGRESS = "in-progress";
    public static final String EXPIRED = "expired";
    public static final String COMPLETED = "completed";
}