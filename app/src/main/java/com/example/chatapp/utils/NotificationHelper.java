package com.example.chatapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.chatapp.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "OTP_Channel";
    private static final String CHANNEL_NAME = "OTP Notifications";

    public static void showNotification(Context context, String otp) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for OTP notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.person) // Replace with your app icon

                .setContentTitle("Your OTP Code")
                .setDefaults(NotificationCompat.DEFAULT_ALL)

                .setContentText("Your OTP is: " + otp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // Automatically remove the notification after it's tapped
        Log.d("NotificationHelper", "Showing OTP Notification: " + otp);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}
