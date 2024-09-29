package hu.szalaj.fakecall;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);

        makeFullScreen();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.call), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

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
}