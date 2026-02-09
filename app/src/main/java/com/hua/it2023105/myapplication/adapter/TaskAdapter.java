package com.hua.it2023105.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hua.it2023105.myapplication.R;
import com.hua.it2023105.myapplication.database.Task;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> tasks;
    private final OnTaskClickListener listener;
    private final Map<Integer, String> statusMap;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(@NonNull List<Task> tasks, @NonNull OnTaskClickListener listener, @NonNull Map<Integer, String> statusMap) {
        this.tasks = tasks;
        this.listener = listener;
        this.statusMap = statusMap;
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
        holder.bind(task, listener, statusMap);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskId;
        TextView tvShortName;
        TextView tvStatus;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskId = itemView.findViewById(R.id.tv_task_id);
            tvShortName = itemView.findViewById(R.id.tv_short_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        void bind(@NonNull final Task task, @NonNull final OnTaskClickListener listener, @NonNull Map<Integer, String> statusMap) {
            tvTaskId.setText("ID: " + task.getUid());
            tvShortName.setText(task.getShortName());

            String statusName = statusMap.get(task.getStatusId());
            if (statusName == null) {
                statusName = "Unknown";
            }
            tvStatus.setText("Status: " + statusName);

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }
}
