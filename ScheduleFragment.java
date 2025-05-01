package com.example.taskmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private CalendarView calendarView;
    private LinearLayout taskListLayout;
    private Button addTaskButton;
    private Calendar selectedDateTime;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        selectedDateTime = Calendar.getInstance();

        calendarView = view.findViewById(R.id.calendar_view);
        taskListLayout = view.findViewById(R.id.task_list_layout);
        addTaskButton = view.findViewById(R.id.add_task_button);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDateTime.set(year, month, dayOfMonth);
            loadTasksForDate();
        });

        addTaskButton.setOnClickListener(v -> showAddEventDialog());

        loadTasksForDate();

        return view;
    }

    private void showAddEventDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_event, null);

        EditText titleInput = dialogView.findViewById(R.id.dialog_task_title_input);
        EditText descriptionInput = dialogView.findViewById(R.id.dialog_task_description_input);
        Button dateButton = dialogView.findViewById(R.id.dialog_date_button);
        Button timeButton = dialogView.findViewById(R.id.dialog_time_button);

        Calendar tempCalendar = Calendar.getInstance();

        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        tempCalendar.set(Calendar.YEAR, year);
                        tempCalendar.set(Calendar.MONTH, month);
                        tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateButton.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
                    },
                    tempCalendar.get(Calendar.YEAR),
                    tempCalendar.get(Calendar.MONTH),
                    tempCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> {
                        tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        tempCalendar.set(Calendar.MINUTE, minute);
                        timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    },
                    tempCalendar.get(Calendar.HOUR_OF_DAY),
                    tempCalendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Add New Event")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, which) -> {
                    String title = titleInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter an event name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long timestamp = tempCalendar.getTimeInMillis(); // Store timestamp

                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("description", description);
                    values.put("datetime", timestamp);
                    values.put("status", "pending"); // default status

                    long id = db.insert("tasks", null, values);
                    if (id != -1) {
                        Toast.makeText(getContext(), "Event created!", Toast.LENGTH_SHORT).show();
                        loadTasksForDate();
                    } else {
                        Toast.makeText(getContext(), "Failed to create event", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadTasksForDate() {
        taskListLayout.removeAllViews();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        long startOfDay = getStartOfDayMillis(selectedDateTime);
        long endOfDay = getEndOfDayMillis(selectedDateTime);

        Cursor cursor = db.query("tasks",
                null,
                "datetime BETWEEN ? AND ?",
                new String[]{String.valueOf(startOfDay), String.valueOf(endOfDay)},
                null,
                null,
                "datetime ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                long datetime = cursor.getLong(cursor.getColumnIndexOrThrow("datetime"));

                String timeFormatted = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(datetime);
                addTaskToLayout(taskListLayout, title, description, timeFormatted);
            }
            cursor.close();
        }
        db.close();
    }

    private void addTaskToLayout(LinearLayout layout, String title, String description, String time) {
        LinearLayout taskLayout = new LinearLayout(requireContext());
        taskLayout.setOrientation(LinearLayout.VERTICAL);
        taskLayout.setPadding(24, 24, 24, 24);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 32);
        taskLayout.setLayoutParams(params);

        TextView titleView = new TextView(requireContext());
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView descriptionView = new TextView(requireContext());
        descriptionView.setText(description);
        descriptionView.setTextSize(18);
        descriptionView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));

        TextView timeView = new TextView(requireContext());
        timeView.setText(time);
        timeView.setTextSize(14);
        timeView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));

        taskLayout.addView(titleView);
        taskLayout.addView(descriptionView);
        taskLayout.addView(timeView);

        layout.addView(taskLayout);
    }

    private long getStartOfDayMillis(Calendar calendar) {
        Calendar start = (Calendar) calendar.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start.getTimeInMillis();
    }

    private long getEndOfDayMillis(Calendar calendar) {
        Calendar end = (Calendar) calendar.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end.getTimeInMillis();
    }
}
