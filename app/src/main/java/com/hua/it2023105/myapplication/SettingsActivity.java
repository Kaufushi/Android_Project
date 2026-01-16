package com.hua.it2023105.myapplication;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hua.it2023105.myapplication.provider.TaskContentProvider;

public class SettingsActivity extends AppCompatActivity {

    private EditText etDefaultDuration;
    private SeekBar sbDefaultDifficulty;
    private TextView tvDefaultDifficulty;
    private Button btnSave;
    private Button btnTestProvider;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);

        etDefaultDuration = findViewById(R.id.et_default_duration);
        sbDefaultDifficulty = findViewById(R.id.sb_default_difficulty);
        tvDefaultDifficulty = findViewById(R.id.tv_default_difficulty);
        btnSave = findViewById(R.id.btn_save_settings);
        btnTestProvider = findViewById(R.id.btn_test_provider);

        // Load current settings
        int defaultDuration = prefs.getInt("default_duration", 1);
        int defaultDifficulty = prefs.getInt("default_difficulty", 5);

        etDefaultDuration.setText(String.valueOf(defaultDuration));
        sbDefaultDifficulty.setProgress(defaultDifficulty);
        tvDefaultDifficulty.setText("Default Difficulty: " + defaultDifficulty);

        sbDefaultDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDefaultDifficulty.setText("Default Difficulty: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> saveSettings());
        btnTestProvider.setOnClickListener(v -> testContentProvider());
    }

    private void testContentProvider() {
        try {
            // 1. Insert a test task via ContentResolver
            ContentValues values = new ContentValues();
            values.put("short_name", "Test Provider Task");
            values.put("description", "Created via Content Resolver");
            values.put("difficulty", 5);
            values.put("task_date", "2023-10-27");
            values.put("start_time", "10:00");
            values.put("duration", 2);
            values.put("status_id", 1); // Assuming 1 is a valid status ID
            values.put("location", "Test Location");

            Uri newUri = getContentResolver().insert(TaskContentProvider.CONTENT_URI, values);

            if (newUri != null) {
                // 2. Query to verify
                Cursor cursor = getContentResolver().query(TaskContentProvider.CONTENT_URI, null, null, null, null);
                if (cursor != null) {
                    int count = cursor.getCount();
                    cursor.close();
                    Toast.makeText(this, "Provider Test Success! Total tasks: " + count, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Provider Inserted but Query failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Provider Insert failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveSettings() {
        String durationStr = etDefaultDuration.getText().toString().trim();

        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Duration cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationStr);
        if (duration <= 0) {
            Toast.makeText(this, "Duration must be positive", Toast.LENGTH_SHORT).show();
            return;
        }

        int difficulty = sbDefaultDifficulty.getProgress();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("default_duration", duration);
        editor.putInt("default_difficulty", difficulty);
        editor.apply();

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}