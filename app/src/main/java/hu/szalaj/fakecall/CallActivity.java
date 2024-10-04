package hu.szalaj.fakecall;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class CallActivity extends AppCompatActivity {
    ImageView imageView;
    /*private MediaPlayer mediaPlayer;*/
    private Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);

        makeFullScreen();

        // Get the current default ringtone URI
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        // Initialize the ringtone with the URI
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.call), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });



        playSound();


        Intent intent = getIntent();
        String nameData = intent.getStringExtra("nameData");
        String numberData = intent.getStringExtra("numberData");
        String photoStr = intent.getStringExtra("photo");
        Uri photo = Uri.parse(photoStr);

        TextView callerNameTxt = findViewById(R.id.txtCallerName);
        TextView callerIdTxt = findViewById(R.id.txtCallerId);

        callerNameTxt.setText(nameData);
        callerIdTxt.setText("Mobile " + numberData);

        imageView = findViewById(R.id.imgViewPhoto);
        Glide.with(getApplicationContext()).load(photo).into(imageView);


        ImageButton animatedButton = findViewById(R.id.btnAnswer);

        // Create up and down movement animation
        TranslateAnimation upDownAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, // Start X position
                Animation.RELATIVE_TO_SELF, 0f, // End X position
                Animation.RELATIVE_TO_SELF, -0.1f, // Start Y position (Up)
                Animation.RELATIVE_TO_SELF, 0.1f // End Y position (Down)
        );
        upDownAnimation.setDuration(500); // Animation duration in milliseconds
        upDownAnimation.setRepeatCount(Animation.INFINITE); // Infinite loop
        upDownAnimation.setRepeatMode(Animation.REVERSE); // Reverse direction after each cycle

        // Create shaking (rotation) animation
        RotateAnimation shakeAnimation = new RotateAnimation(
                -5f, 5f, // From and To degrees (slight rotation)
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X (center of the button)
                Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y (center of the button)
        );
        shakeAnimation.setDuration(100); // Shake duration
        shakeAnimation.setRepeatCount(Animation.INFINITE); // Infinite loop
        shakeAnimation.setRepeatMode(Animation.REVERSE); // Reverse after each cycle

        // Start animations
        animatedButton.startAnimation(upDownAnimation);
        animatedButton.startAnimation(shakeAnimation);

        // Set an OnClickListener
        animatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpCall(nameData, photoStr);
            }
        });
    }
    private void playSound(){
        /*mediaPlayer = MediaPlayer.create(this, R.raw.testaudio); // Replace 'sound' with your file name
        if (mediaPlayer != null) {
            mediaPlayer.start(); // Start playing the sound
        }*/
        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();  // Play the ringtone
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mediaPlayer != null) {
            mediaPlayer.release(); // Release resources when done
            mediaPlayer = null;
        }*/
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();  // Stop the ringtone
        }
    }

    private void stopAudio(){
        /*if (mediaPlayer != null) {
            mediaPlayer.release(); // Release resources when done
            mediaPlayer = null;
        }*/
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();  // Stop the ringtone
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Make sure immersive mode is applied again if the activity is resumed
        makeFullScreen();
    }
    private void makeFullScreen() {
        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void pickUpCall(String nameData, String photoStr){
        stopAudio();
        Intent intent = new Intent(this, InCallActivity.class);
        intent.putExtra("nameData", nameData);
        intent.putExtra("photo", photoStr);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}