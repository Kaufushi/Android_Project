package com.hua.it2023105.myapplication.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = TaskStatus.class,
                parentColumns = "id",
                childColumns = "status_id",
                onDelete = ForeignKey.CASCADE))
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "short_name")
    private String shortName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "difficulty")
    private int difficulty;

    @ColumnInfo(name = "task_date")
    private String taskDate;

    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "status_id")
    private int statusId;

    @ColumnInfo(name = "location")
    private String location;

    // Constructor
    public Task(String shortName, String description, int difficulty,
                String taskDate, String startTime, int duration, int statusId, String location) {
        this.shortName = shortName;
        this.description = description;
        this.difficulty = difficulty;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.duration = duration;
        this.statusId = statusId;
        this.location = location;
    }

    // Getters and Setters
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public String getTaskDate() { return taskDate; }
    public void setTaskDate(String taskDate) { this.taskDate = taskDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}