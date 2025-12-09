package com.example.loom.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loom.R;
import com.example.loom.model.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("ClassEscapesDefinedScope")
public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private boolean selectionMode = false;
    private final SparseBooleanArray selectedItems = new SparseBooleanArray();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Task task);
        void onItemCheckChanged(Task task, boolean isChecked);
        void onItemLongClick(Task task);
        void onSelectionCountChanged(int count);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle()) &&
                            oldItem.getDescription().equals(newItem.getDescription()) &&
                            oldItem.isCompleted() == newItem.isCompleted() &&
                            oldItem.getDueDate() == newItem.getDueDate();
                }
            };


    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }

        selectionMode = selectedItems.size() > 0;
        notifyItemChanged(position);

        if (listener != null) {
            listener.onSelectionCountChanged(selectedItems.size());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelection() {
        selectedItems.clear();
        selectionMode = false;
        notifyDataSetChanged();
        if (listener != null) listener.onSelectionCountChanged(0);
    }

    public boolean isSelectionMode() { return selectionMode; }

    public List<Task> getSelectedTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            int pos = selectedItems.keyAt(i);
            tasks.add(getItem(pos));
        }
        return tasks;
    }

    public void cancelSelection() {
        clearSelection();
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);

        // Remove listener to avoid unwanted triggers
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(task.isCompleted());
        holder.titleTextView.setText(task.getTitle());

        // Description preview
        String desc = task.getDescription();
        if (desc != null && !desc.isEmpty()) {
            holder.descriptionPreview.setVisibility(View.VISIBLE);
            holder.descriptionPreview.setText(desc);
        } else {
            holder.descriptionPreview.setVisibility(View.GONE);
        }

        DateFormat df = DateFormat.getDateInstance();
        holder.dueTextView.setText("Due: " + df.format(new Date(task.getDueDate())));

        // highlight
        holder.itemView.setBackgroundColor(
                selectedItems.get(position, false)
                        ? Color.parseColor("#FFF59D")   // pale yellow selection
                        : Color.TRANSPARENT
        );

        holder.checkBox.setOnCheckedChangeListener((b, isChecked) -> {
            if (!selectionMode && listener != null) {
                listener.onItemCheckChanged(task, isChecked);
            }
        });
    }


    // VIEW HOLDER
    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dueTextView, descriptionPreview;
        CheckBox checkBox;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.task_title);
            dueTextView = itemView.findViewById(R.id.task_due_date);
            descriptionPreview = itemView.findViewById(R.id.task_description_preview);
            checkBox = itemView.findViewById(R.id.task_checkbox);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                if (selectionMode) {
                    toggleSelection(pos);
                } else if (listener != null) {
                    listener.onItemClick(getItem(pos));
                }
            });


            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return true;

                if (!selectionMode) {
                    selectionMode = true;
                    toggleSelection(pos);
                    if (listener != null) listener.onItemLongClick(getItem(pos));
                }
                return true;
            });
        }
    }
}
