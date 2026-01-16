package com.hua.it2023105.myapplication.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.taskmanager.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tasks");

    private static final int TASKS = 1;
    private static final int TASK_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "tasks", TASKS);
        uriMatcher.addURI(AUTHORITY, "tasks/#", TASK_ID);
    }

    private AppDatabase database;
    private ExecutorService executor;

    @Override
    public boolean onCreate() {
        database = AppDatabase.getInstance(getContext());
        executor = Executors.newSingleThreadExecutor();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Future<Cursor> future = executor.submit(() -> {
            int match = uriMatcher.match(uri);
            List<Task> tasks;

            if (match == TASKS) {
                tasks = database.taskDao().getAllTasks();
            } else if (match == TASK_ID) {
                long id = ContentUris.parseId(uri);
                Task task = database.taskDao().getTaskById((int) id);
                tasks = task != null ? List.of(task) : List.of();
            } else {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            String[] columns = {"uid", "short_name", "description", "difficulty",
                    "task_date", "start_time", "duration", "status_id", "location"};
            MatrixCursor cursor = new MatrixCursor(columns);

            for (Task task : tasks) {
                cursor.addRow(new Object[]{task.getUid(), task.getShortName(),
                        task.getDescription(), task.getDifficulty(), task.getTaskDate(),
                        task.getStartTime(), task.getDuration(), task.getStatusId(),
                        task.getLocation()});
            }

            return cursor;
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Future<Uri> future = executor.submit(() -> {
            Task task = new Task(
                    values.getAsString("short_name"),
                    values.getAsString("description"),
                    values.getAsInteger("difficulty"),
                    values.getAsString("task_date"),
                    values.getAsString("start_time"),
                    values.getAsInteger("duration"),
                    values.getAsInteger("status_id"),
                    values.getAsString("location")
            );

            long id = database.taskDao().insert(task);
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(CONTENT_URI, id);
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        Future<Integer> future = executor.submit(() -> {
            long id = ContentUris.parseId(uri);
            Task task = database.taskDao().getTaskById((int) id);

            if (task != null) {
                if (values.containsKey("short_name"))
                    task.setShortName(values.getAsString("short_name"));
                if (values.containsKey("status_id"))
                    task.setStatusId(values.getAsInteger("status_id"));

                database.taskDao().update(task);
                getContext().getContentResolver().notifyChange(uri, null);
                return 1;
            }
            return 0;
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        Future<Integer> future = executor.submit(() -> {
            long id = ContentUris.parseId(uri);
            Task task = database.taskDao().getTaskById((int) id);

            if (task != null) {
                database.taskDao().delete(task);
                getContext().getContentResolver().notifyChange(uri, null);
                return 1;
            }
            return 0;
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == TASKS) {
            return "vnd.android.cursor.dir/vnd.com.example.taskmanager.tasks";
        } else if (match == TASK_ID) {
            return "vnd.android.cursor.item/vnd.com.example.taskmanager.tasks";
        }
        return null;
    }
}