package com.hua.it2023105.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TaskStatusDao {

    @Insert
    long insert(TaskStatus status);

    @Query("SELECT * FROM task_status WHERE status_name = :statusName LIMIT 1")
    TaskStatus getStatusByName(String statusName);

    @Query("SELECT * FROM task_status")
    List<TaskStatus> getAllStatuses();

    @Query("SELECT id FROM task_status WHERE status_name = :statusName LIMIT 1")
    int getStatusIdByName(String statusName);
}