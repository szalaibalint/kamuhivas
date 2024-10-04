package hu.szalaj.fakecall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class InCallActivity extends AppCompatActivity {
    ImageView imageView;
    ImageView endCallButton;
    private TextView timerTextView;
    private Handler handler = new Handler();
    private int seconds = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incall);
        makeFullScreen();

        Intent intent = getIntent();
        String nameData = intent.getStringExtra("nameData");
        String photoStr = intent.getStringExtra("photo");
        Uri photo = Uri.parse(photoStr);

        TextView callerNameTxt = findViewById(R.id.txtCallerName);
        callerNameTxt.setText(nameData);

        imageView = findViewById(R.id.imgViewPhoto);
        Glide.with(getApplicationContext()).load(photo).into(imageView);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.incall), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        timerTextView = findViewById(R.id.txtCallTime);
        startTimer();

        endCallButton = findViewById(R.id.imgViewEndCall);

        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCallStart();
            }
        });
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
    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;
                String timeFormatted = String.format("%02d:%02d", minutes, remainingSeconds);
                timerTextView.setText(timeFormatted);
                seconds++;
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void endCallStart(){
        handler.removeCallbacksAndMessages(null);
        timerTextView.setText("Call ended");
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.endcallC, typedValue, true);
        endCallButton.setBackgroundColor(typedValue.data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Close the app
                finishAffinity(); // Closes the app
            }
        }, 2000);
    }
}