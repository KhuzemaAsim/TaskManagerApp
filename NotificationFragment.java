package com.example.taskmanager;

import android.content.ContentValues;
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

public class NotificationFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private LinearLayout notificationListLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        dbHelper = new DatabaseHelper(getContext());
        notificationListLayout = view.findViewById(R.id.notification_list_layout);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM notifications", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            addDummyNotifications();
        }

        loadNotifications();
        return view;
    }

    private void addDummyNotifications() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long currentTime = System.currentTimeMillis();

        String[] messages = {
                "Reminder: Meeting with team at 10:00 AM",
                "Task 'Design new UX' is due tomorrow",
                "Workout session scheduled for 6:00 PM"
        };

        for (String message : messages) {
            ContentValues values = new ContentValues();
            values.put("message", message);
            values.put("datetime", currentTime);
            db.insert("notifications", null, values);
        }
        db.close();
    }

    private void loadNotifications() {
        notificationListLayout.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "notifications",
                new String[]{"id", "message", "datetime"},
                null,
                null,
                null,
                null,
                "datetime DESC"
        );

        while (cursor.moveToNext()) {
            String message = cursor.getString(cursor.getColumnIndex("message"));
            long datetime = cursor.getLong(cursor.getColumnIndex("datetime"));

            View notificationView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, notificationListLayout, false);
            TextView messageView = notificationView.findViewById(R.id.notification_message);
            TextView datetimeView = notificationView.findViewById(R.id.notification_datetime);

            messageView.setText(message);
            datetimeView.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(datetime)));


            int backgroundColor;
            if ((getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                backgroundColor = getResources().getColor(android.R.color.black, null); // Dark mode -> black
            } else {
                backgroundColor = getResources().getColor(android.R.color.white, null); // Light mode -> white
            }
            notificationView.setBackgroundColor(backgroundColor);

            notificationListLayout.addView(notificationView);
        }
        cursor.close();
        db.close();
    }

}