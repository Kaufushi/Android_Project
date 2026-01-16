package com.hua.it2023105.myapplication.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hua.it2023105.myapplication.R;
import com.hua.it2023105.myapplication.database.AppDatabase;
import com.hua.it2023105.myapplication.database.Task;
import com.hua.it2023105.myapplication.database.TaskStatus;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskId;
        TextView tvShortName;
        TextView tvStatus;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskId = itemView.findViewById(R.id.tv_task_id);
            tvShortName = itemView.findViewById(R.id.tv_short_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        void bind(Task task, OnTaskClickListener listener) {
            tvTaskId.setText("ID: " + task.getUid());
            tvShortName.setText(task.getShortName());

            // Get status name
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(itemView.getContext());
                String statusName = "";

                if (task.getStatusId() == db.taskStatusDao().getStatusIdByName(TaskStatus.RECORDED)) {
                    statusName = "Recorded";
                } else if (task.getStatusId() == db.taskStatusDao().getStatusIdByName(TaskStatus.IN_PROGRESS)) {
                    statusName = "In Progress";
                } else if (task.getStatusId() == db.taskStatusDao().getStatusIdByName(TaskStatus.EXPIRED)) {
                    statusName = "Expired";
                } else if (task.getStatusId() == db.taskStatusDao().getStatusIdByName(TaskStatus.COMPLETED)) {
                    statusName = "Completed";
                }

                String finalStatusName = statusName;
                ((android.app.Activity) itemView.getContext()).runOnUiThread(() -> {
                    tvStatus.setText("Status: " + finalStatusName);
                });
            });

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }
}