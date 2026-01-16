package com.hua.it2023105.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import com.hua.it2023105.myapplication.database.TaskStatus;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvShortName, tvDescription, tvDifficulty, tvDate,
            tvStartTime, tvDuration, tvStatus, tvLocation;
    private Button btnComplete, btnViewMap;
    private AppDatabase database;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        database = AppDatabase.getInstance(this);

        tvShortName = findViewById(R.id.tv_detail_short_name);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvDifficulty = findViewById(R.id.tv_detail_difficulty);
        tvDate = findViewById(R.id.tv_detail_date);
        tvStartTime = findViewById(R.id.tv_detail_start_time);
        tvDuration = findViewById(R.id.tv_detail_duration);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvLocation = findViewById(R.id.tv_detail_location);
        btnComplete = findViewById(R.id.btn_complete);
        btnViewMap = findViewById(R.id.btn_view_map);

        int taskId = getIntent().getIntExtra("TASK_ID", -1);

        if (taskId != -1) {
            loadTaskDetails(taskId);
        }

        btnComplete.setOnClickListener(v -> markAsCompleted());
        btnViewMap.setOnClickListener(v -> openMap());
    }

    private void loadTaskDetails(int taskId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentTask = database.taskDao().getTaskById(taskId);

            if (currentTask != null) {
                String statusName = getStatusName(currentTask.getStatusId());

                runOnUiThread(() -> {
                    tvShortName.setText("Name: " + currentTask.getShortName());
                    tvDescription.setText("Description: " + currentTask.getDescription());
                    tvDifficulty.setText("Difficulty: " + currentTask.getDifficulty() + "/10");
                    tvDate.setText("Date: " + currentTask.getTaskDate());
                    tvStartTime.setText("Start Time: " + currentTask.getStartTime());
                    tvDuration.setText("Duration: " + currentTask.getDuration() + " hours");
                    tvStatus.setText("Status: " + statusName);

                    String location = currentTask.getLocation();
                    if (location != null && !location.isEmpty()) {
                        tvLocation.setText("Location: " + location);
                        btnViewMap.setEnabled(true);
                    } else {
                        tvLocation.setText("Location: Not specified");
                        btnViewMap.setEnabled(false);
                    }
                });
            }
        });
    }

    private String getStatusName(int statusId) {
        if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.RECORDED)) {
            return "Recorded";
        } else if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.IN_PROGRESS)) {
            return "In Progress";
        } else if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.EXPIRED)) {
            return "Expired";
        } else if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED)) {
            return "Completed";
        }
        return "Unknown";
    }

    private void markAsCompleted() {
        if (currentTask != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                int completedId = database.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED);
                currentTask.setStatusId(completedId);
                database.taskDao().update(currentTask);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    private void openMap() {
        if (currentTask != null && currentTask.getLocation() != null &&
                !currentTask.getLocation().isEmpty()) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(currentTask.getLocation()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}