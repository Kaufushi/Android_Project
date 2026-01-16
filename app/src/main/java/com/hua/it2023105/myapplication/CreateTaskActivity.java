package com.hua.it2023105.myapplication;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import com.hua.it2023105.myapplication.database.TaskStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText etShortName, etDescription, etDuration, etLocation;
    private SeekBar sbDifficulty;
    private TextView tvDifficulty, tvStartTime;
    private Button btnSelectTime, btnCreate;
    private AppDatabase database;
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        database = AppDatabase.getInstance(this);

        etShortName = findViewById(R.id.et_short_name);
        etDescription = findViewById(R.id.et_description);
        etDuration = findViewById(R.id.et_duration);
        etLocation = findViewById(R.id.et_location);
        sbDifficulty = findViewById(R.id.sb_difficulty);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        tvStartTime = findViewById(R.id.tv_start_time);
        btnSelectTime = findViewById(R.id.btn_select_time);
        btnCreate = findViewById(R.id.btn_create);

        SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        int defaultDuration = prefs.getInt("default_duration", 1);
        int defaultDifficulty = prefs.getInt("default_difficulty", 5);

        etDuration.setText(String.valueOf(defaultDuration));
        sbDifficulty.setProgress(defaultDifficulty);
        tvDifficulty.setText("Difficulty: " + defaultDifficulty);

        sbDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDifficulty.setText("Difficulty: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnCreate.setOnClickListener(v -> createTask());
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    tvStartTime.setText("Start Time: " + selectedTime);
                }, 12, 0, true);
        timePicker.show();
    }

    private void createTask() {
        String shortName = etShortName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        int difficulty = sbDifficulty.getProgress();

        // Validation
        if (shortName.isEmpty() || shortName.length() > 20) {
            Toast.makeText(this, "Short name must be 1-20 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty() || description.length() > 150) {
            Toast.makeText(this, "Description must be 1-150 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Duration is required", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationStr);
        if (duration <= 0) {
            Toast.makeText(this, "Duration must be positive", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select start time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Create task
        Executors.newSingleThreadExecutor().execute(() -> {
            int recordedId = database.taskStatusDao().getStatusIdByName(TaskStatus.RECORDED);

            Task task = new Task(shortName, description, difficulty, currentDate,
                    selectedTime, duration, recordedId, location);

            database.taskDao().insert(task);

            runOnUiThread(() -> {
                Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}