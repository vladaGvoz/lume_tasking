package com.example.loom.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.loom.R;
import com.example.loom.model.Task;
import com.example.loom.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskListFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private NavController navController;

    private TextView tvEmpty;

    public TaskListFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        // --- Empty text reference ---
        tvEmpty = view.findViewById(R.id.tvEmpty);

        // --- Observe data with empty-state handling ---
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.submitList(tasks);

            if (tasks == null || tasks.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        });

        // --- Adapter click listeners ---
        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemCheckChanged(Task task, boolean isChecked) {
                // UPDATE OPERATION
                Task updatedTask = new Task(
                        task.getTitle(),
                        task.getDescription(),
                        isChecked,
                        task.getCreationTimestamp()
                );
                updatedTask.setId(task.getId());
                taskViewModel.update(updatedTask);

                Toast.makeText(getContext(), isChecked ? "Task Completed!" : "Task Unchecked.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(Task task) {
                // EDIT TASK
                Bundle bundle = new Bundle();
                bundle.putInt("taskId", task.getId());
                navController.navigate(R.id.action_taskListFragment_to_taskDetailFragment, bundle);
            }

            @Override
            public void onItemLongClick(Task task) {
                // DELETE TASK (with confirmation)
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            taskViewModel.deleteById(task.getId());
                            Toast.makeText(getContext(), "Task Deleted!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // --- FAB add ---
        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> navController.navigate(R.id.action_taskListFragment_to_taskDetailFragment));
    }
}
