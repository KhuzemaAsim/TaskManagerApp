package com.example.taskmanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreviousTasksFragment extends Fragment {

    private LinearLayout oldTasksLayout;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedState) {
        View root = inflater.inflate(R.layout.fragment_previous_tasks, parent, false);

        oldTasksLayout = root.findViewById(R.id.past_task_list_layout);
        dbHelper = new DatabaseHelper(getContext());

        populatePreviousTasks();
        return root;
    }

    private void populatePreviousTasks() {
        oldTasksLayout.removeAllViews();

        long now = System.currentTimeMillis();  // Current timestamp

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor taskCursor = db.rawQuery("SELECT title, description, datetime FROM tasks", null);

        while (taskCursor.moveToNext()) {
            String taskTitle = taskCursor.getString(taskCursor.getColumnIndex("title"));
            String taskDesc = taskCursor.getString(taskCursor.getColumnIndex("description"));
            long taskTime = taskCursor.getLong(taskCursor.getColumnIndex("datetime"));

            // Only display tasks that have already occurred
            if (taskTime < now) {
                LinearLayout taskCard = new LinearLayout(getContext());
                taskCard.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                int margin = (int) (10 * getResources().getDisplayMetrics().density);  // 10dp margin
                layoutParams.setMargins(0, margin, 0, margin);
                taskCard.setLayoutParams(layoutParams);
                taskCard.setPadding(20, 20, 20, 20);

                TextView titleView = createTextView(taskTitle, 18);
                TextView descView = createTextView(taskDesc, 14);
                TextView dateTimeView = createTextView(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(taskTime)), 12
                );

                taskCard.addView(titleView);
                taskCard.addView(descView);
                taskCard.addView(dateTimeView);

                oldTasksLayout.addView(taskCard);
            }
        }

        taskCursor.close();
        db.close();
    }

    private TextView createTextView(String text, float textSizeSp) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(textSizeSp);
        tv.setTextColor(getResources().getColor(R.color.purple_500, null));
        return tv;
    }
}
