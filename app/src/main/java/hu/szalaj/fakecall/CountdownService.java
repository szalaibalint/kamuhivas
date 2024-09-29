package hu.szalaj.fakecall;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class CountdownService extends Service {

    private CountDownTimer countDownTimer;
    private int seconds;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String nameData = intent.getStringExtra("nameData");
        String numberData = intent.getStringExtra("numberData");
        String photoStr = intent.getStringExtra("photo");
        String secondsStr = intent.getStringExtra("seconds");
        seconds = Integer.parseInt(secondsStr);
        if (seconds < 0) {
            seconds = 0;
        }
        // Start a countdown (e.g., 10 seconds = 10000 milliseconds)
        countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Optional: Notify user of remaining time

            }

            @Override
            public void onFinish() {
                // Launch the activity when the countdown finishes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(CountdownService.this)) {
                    // Start the activity if the overlay permission is granted
                    Intent intent = new Intent(CountdownService.this, CallActivity.class);
                    intent.putExtra("nameData", nameData);
                    intent.putExtra("numberData", numberData);
                    intent.putExtra("photo", photoStr);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Show a notification prompting the user to reopen the app
                    showNotification(nameData, numberData, photoStr);
                }
            }
        }.start();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private static final String CHANNEL_ID = "call_notification";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;

    private void showNotification(String nameData, String numberData, String photoStr) {
        // Create an Intent to reopen the app when the notification is clicked
        Intent intent = new Intent(CountdownService.this, CallActivity.class);
        intent.putExtra("nameData", nameData);
        intent.putExtra("numberData", numberData);
        intent.putExtra("photo", photoStr);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create a full-screen intent to display the notification like a heads-up notification
        Intent fullScreenIntent = new Intent(this, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(nameData)
                .setContentText("Calling you")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for heads-up notification
                .setDefaults(NotificationCompat.DEFAULT_ALL)  // Sound, vibration, and lights
                .setContentIntent(pendingIntent) // Open the app when clicked
                .setFullScreenIntent(fullScreenPendingIntent, true) // Full-screen intent for critical notifications
                .setAutoCancel(true); // Dismiss notification when clicked

        // For Android 8.0+ (Oreo) and above, create the NotificationChannel with high importance
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, "Call", NotificationManager.IMPORTANCE_HIGH); // High importance
                channel.setDescription("Notification for calls");
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
