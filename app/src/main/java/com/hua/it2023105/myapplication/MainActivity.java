package com.hua.it2023105.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hua.it2023105.myapplication.adapter.TaskAdapter;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import com.hua.it2023105.myapplication.database.TaskStatus;
import com.hua.it2023105.myapplication.service.TaskUpdateService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private AppDatabase database;
    private List<Task> taskList;
    private Map<Integer, String> statusMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(this);
        taskList = new ArrayList<>();
        statusMap = new HashMap<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(taskList, this::onTaskClick, statusMap);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });

        // Start the update service
        Intent serviceIntent = new Intent(this, TaskUpdateService.class);
        startService(serviceIntent);

        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Load status map once
            int recordedId = database.taskStatusDao().getStatusIdByName(TaskStatus.RECORDED);
            int inProgressId = database.taskStatusDao().getStatusIdByName(TaskStatus.IN_PROGRESS);
            int expiredId = database.taskStatusDao().getStatusIdByName(TaskStatus.EXPIRED);
            int completedId = database.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED);

            Map<Integer, String> newStatusMap = new HashMap<>();
            newStatusMap.put(recordedId, "Recorded");
            newStatusMap.put(inProgressId, "In Progress");
            newStatusMap.put(expiredId, "Expired");
            newStatusMap.put(completedId, "Completed");

            List<Task> tasks = database.taskDao().getAllNonCompletedTasksOrdered(
                    completedId, expiredId, inProgressId, recordedId);

            runOnUiThread(() -> {
                statusMap.clear();
                statusMap.putAll(newStatusMap);
                taskList.clear();
                taskList.addAll(tasks);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void onTaskClick(Task task) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("TASK_ID", task.getUid());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_export) {
            exportTasks();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportTasks() {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }
}