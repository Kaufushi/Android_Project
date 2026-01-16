package com.hua.it2023105.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import com.hua.it2023105.myapplication.database.TaskStatus;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class ExportActivity extends AppCompatActivity {

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        database = AppDatabase.getInstance(this);

        Button btnExportTxt = findViewById(R.id.btn_export_txt);
        Button btnExportHtml = findViewById(R.id.btn_export_html);

        btnExportTxt.setOnClickListener(v -> exportToText());
        btnExportHtml.setOnClickListener(v -> exportToHtml());
    }

    private void exportToText() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int completedId = database.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED);
            List<Task> tasks = database.taskDao().getAllNonCompletedTasks(completedId);

            if (tasks.isEmpty()) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No tasks to export", Toast.LENGTH_SHORT).show());
                return;
            }

            StringBuilder content = new StringBuilder();
            content.append("TASK EXPORT - NON-COMPLETED TASKS\n");
            content.append("=================================\n\n");

            for (Task task : tasks) {
                String statusName = getStatusName(task.getStatusId());
                content.append("ID: ").append(task.getUid()).append("\n");
                content.append("Name: ").append(task.getShortName()).append("\n");
                content.append("Description: ").append(task.getDescription()).append("\n");
                content.append("Difficulty: ").append(task.getDifficulty()).append("/10\n");
                content.append("Date: ").append(task.getTaskDate()).append("\n");
                content.append("Start Time: ").append(task.getStartTime()).append("\n");
                content.append("Duration: ").append(task.getDuration()).append(" hours\n");
                content.append("Status: ").append(statusName).append("\n");
                content.append("Location: ").append(task.getLocation() != null ?
                        task.getLocation() : "Not specified").append("\n");
                content.append("---------------------------------\n\n");
            }

            saveToFile(content.toString(), "tasks_export.txt");
        });
    }

    private void exportToHtml() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int completedId = database.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED);
            List<Task> tasks = database.taskDao().getAllNonCompletedTasks(completedId);

            if (tasks.isEmpty()) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No tasks to export", Toast.LENGTH_SHORT).show());
                return;
            }

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html>\n<head>\n");
            html.append("<title>Task Export</title>\n");
            html.append("<style>\n");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
            html.append("h1 { color: #333; }\n");
            html.append(".task { border: 1px solid #ddd; padding: 15px; margin: 10px 0; }\n");
            html.append(".task-header { font-weight: bold; color: #0066cc; }\n");
            html.append("</style>\n</head>\n<body>\n");
            html.append("<h1>Task Export - Non-Completed Tasks</h1>\n");

            for (Task task : tasks) {
                String statusName = getStatusName(task.getStatusId());
                html.append("<div class='task'>\n");
                html.append("<div class='task-header'>ID: ").append(task.getUid())
                        .append(" - ").append(task.getShortName()).append("</div>\n");
                html.append("<p><strong>Description:</strong> ").append(task.getDescription())
                        .append("</p>\n");
                html.append("<p><strong>Difficulty:</strong> ").append(task.getDifficulty())
                        .append("/10</p>\n");
                html.append("<p><strong>Date:</strong> ").append(task.getTaskDate()).append("</p>\n");
                html.append("<p><strong>Start Time:</strong> ").append(task.getStartTime())
                        .append("</p>\n");
                html.append("<p><strong>Duration:</strong> ").append(task.getDuration())
                        .append(" hours</p>\n");
                html.append("<p><strong>Status:</strong> ").append(statusName).append("</p>\n");
                html.append("<p><strong>Location:</strong> ").append(
                                task.getLocation() != null ? task.getLocation() : "Not specified")
                        .append("</p>\n");
                html.append("</div>\n");
            }

            html.append("</body>\n</html>");

            saveToFile(html.toString(), "tasks_export.html");
        });
    }

    private String getStatusName(int statusId) {
        if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.RECORDED)) {
            return "Recorded";
        } else if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.IN_PROGRESS)) {
            return "In Progress";
        } else if (statusId == database.taskStatusDao().getStatusIdByName(TaskStatus.EXPIRED)) {
            return "Expired";
        }
        return "Unknown";
    }

    private void saveToFile(String content, String filename) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, filename);

            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();

            runOnUiThread(() ->
                    Toast.makeText(this, "Exported to: " + file.getAbsolutePath(),
                            Toast.LENGTH_LONG).show());
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Export failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }
}