package com.example.loom.ui;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loom.R;
import com.example.loom.model.Task;
import com.example.loom.viewmodel.TaskViewModel;

public class TaskDetailFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private NavController navController;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonSave;

    private int taskId = -1;
    private long taskCreationTimestamp = 0;
    private boolean isTaskCompleted = false;

    public TaskDetailFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        editTextTitle = view.findViewById(R.id.edit_text_task_title);
        editTextDescription = view.findViewById(R.id.edit_text_task_description);
        buttonSave = view.findViewById(R.id.button_save_task);

        if (getArguments() != null && getArguments().containsKey("taskId")) {
            int id = getArguments().getInt("taskId", -1);
            if (id != -1) {
                taskId = id;
                loadTaskDetails(taskId);
            }
        }

        buttonSave.setOnClickListener(v -> saveTask());
    }

    private void loadTaskDetails(int id) {
        taskViewModel.getTaskById(id).observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                editTextTitle.setText(task.getTitle());
                editTextDescription.setText(task.getDescription());

                taskCreationTimestamp = task.getCreationTimestamp();
                isTaskCompleted = task.isCompleted();

                buttonSave.setText(R.string.button_update);
            } else {
                Toast.makeText(getContext(), "Error loading task.", Toast.LENGTH_SHORT).show();
                navController.popBackStack();
            }
        });
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = taskId == -1 ? System.currentTimeMillis() : taskCreationTimestamp;

        Task task = new Task(title, description, isTaskCompleted, timestamp);

        if (taskId == -1) {
            // INSERT
            taskViewModel.insert(task);
            Toast.makeText(getContext(), "Task Added!", Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE
            task.setId(taskId);
            taskViewModel.update(task);
            Toast.makeText(getContext(), "Task Updated!", Toast.LENGTH_SHORT).show();
        }

        // back
        navController.popBackStack();
    }
}
