package com.hua.it2023105.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import com.hua.it2023105.myapplication.database.TaskStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskUpdateService extends Service {

    private Handler handler;
    private Runnable updateRunnable;
    private AppDatabase database;
    private static final long UPDATE_INTERVAL = 60 * 60 * 1000; // 1 hour

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        handler = new Handler();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateTaskStatuses();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(updateRunnable);
        return START_STICKY;
    }

    private void updateTaskStatuses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Get current date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                String currentTime = timeFormat.format(new Date());

                // Delete tasks from previous days
                database.taskDao().deleteTasksBeforeDate(currentDate);

                // Get all tasks
                List<Task> tasks = database.taskDao().getAllTasks();

                int recordedId = database.taskStatusDao().getStatusIdByName(TaskStatus.RECORDED);
                int inProgressId = database.taskStatusDao().getStatusIdByName(TaskStatus.IN_PROGRESS);
                int expiredId = database.taskStatusDao().getStatusIdByName(TaskStatus.EXPIRED);
                int completedId = database.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED);

                for (Task task : tasks) {
                    // Skip completed tasks
                    if (task.getStatusId() == completedId) {
                        continue;
                    }

                    // Check if task should be in-progress
                    if (task.getStatusId() == recordedId &&
                            currentTime.compareTo(task.getStartTime()) >= 0) {
                        task.setStatusId(inProgressId);
                        database.taskDao().update(task);
                    }

                    // Check if task should be expired
                    String[] startTimeParts = task.getStartTime().split(":");
                    int startHour = Integer.parseInt(startTimeParts[0]);
                    int startMinute = Integer.parseInt(startTimeParts[1]);

                    int endHour = startHour + task.getDuration();
                    String endTime = String.format(Locale.getDefault(), "%02d:%02d",
                            endHour % 24, startMinute);

                    if ((task.getStatusId() == inProgressId || task.getStatusId() == recordedId) &&
                            currentTime.compareTo(endTime) >= 0) {
                        task.setStatusId(expiredId);
                        database.taskDao().update(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}