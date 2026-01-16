package com.hua.it2023105.myapplication.database;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, TaskStatus.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract TaskDao taskDao();
    public abstract TaskStatusDao taskStatusDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "task_database")
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Populate initial statuses
            Executors.newSingleThreadExecutor().execute(() -> {
                TaskStatusDao statusDao = instance.taskStatusDao();
                statusDao.insert(new TaskStatus(TaskStatus.RECORDED));
                statusDao.insert(new TaskStatus(TaskStatus.IN_PROGRESS));
                statusDao.insert(new TaskStatus(TaskStatus.EXPIRED));
                statusDao.insert(new TaskStatus(TaskStatus.COMPLETED));
            });
        }
    };
}