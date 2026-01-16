package com.hua.it2023105.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    Task getTaskById(int taskId);

    @Query("SELECT * FROM tasks WHERE status_id != :completedStatusId ORDER BY " +
            "CASE WHEN status_id = :expiredStatusId THEN 1 " +
            "WHEN status_id = :inProgressStatusId THEN 2 " +
            "WHEN status_id = :recordedStatusId THEN 3 END")
    List<Task> getAllNonCompletedTasksOrdered(int completedStatusId, int expiredStatusId,
                                              int inProgressStatusId, int recordedStatusId);

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE task_date < :currentDate")
    List<Task> getTasksBeforeDate(String currentDate);

    @Query("DELETE FROM tasks WHERE task_date < :currentDate")
    void deleteTasksBeforeDate(String currentDate);

    @Query("SELECT * FROM tasks WHERE status_id != :completedStatusId")
    List<Task> getAllNonCompletedTasks(int completedStatusId);
}