package hu.szalaj.fakecall;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import android.Manifest;
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_PERMISSION_REQUEST_CODE = 125;
    private EditText edtTxtNumber;
    Uri photo;
    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            if (o == null) {
                Toast.makeText(MainActivity.this, "No image Selected", Toast.LENGTH_SHORT).show();
            } else {
                photo = o;
                ImageView pim = findViewById(R.id.pickImage);
                Glide.with(getApplicationContext()).load(photo).into(pim);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtTxtNumber = findViewById(R.id.edtTxtNumber);
        edtTxtNumber.setText("+36 ");
        edtTxtNumber.setSelection(4); // Move cursor after "+36 "

        edtTxtNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting; // Flag to prevent recursive formatting

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return; // Avoid recursive calls

                String input = s.toString();

                // Ensure "+36 " is present
                if (!input.startsWith("+36 ")) {
                    input = "+36 "; // Restore "+36 " if it was removed
                }

                // Remove the "+36 " prefix for processing the rest of the number
                String numberPart = input.substring(4).replace(" ", "");

                // Limit input to 10 digits
                if (numberPart.length() > 9) {
                    numberPart = numberPart.substring(0, 9); // Trim to 10 digits
                }

                // Format the phone number with spaces
                StringBuilder formatted = new StringBuilder("+36 ");
                if (numberPart.length() >= 2) {
                    formatted.append(numberPart.substring(0, 2)).append(" ");
                    if (numberPart.length() > 2) {
                        formatted.append(numberPart.substring(2, Math.min(5, numberPart.length()))).append(" ");
                    }
                    if (numberPart.length() > 5) {
                        formatted.append(numberPart.substring(5, Math.min(9, numberPart.length()))).append(" ");
                    }
                    if (numberPart.length() > 8) {
                        formatted.append(numberPart.substring(9));
                    }
                } else if (numberPart.length() == 1) {
                    formatted.append(numberPart);
                }

                // Set the flag to prevent recursive calls
                isFormatting = true;
                if (formatted.toString().length() > 4 && formatted.toString().endsWith(" ")){
                    formatted.deleteCharAt(formatted.length() - 1);
                    edtTxtNumber.setText(formatted.toString());
                }else {
                    edtTxtNumber.setText(formatted.toString());
                }

                // Move cursor to the end of the text
                edtTxtNumber.setSelection(formatted.length()); // Move cursor to the end
                isFormatting = false; // Reset the flag
            }
        });


        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check for overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 123);
        }

        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 124);
            }
        }

        // Handle image picking button
        ImageView pickImage = findViewById(R.id.pickImage);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGalleryPermission()) {
                    launchGallery();
                } else {
                    requestGalleryPermission();
                }
            }
        });
    }

    // Check if gallery permission is granted
    private boolean checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    // Request gallery permission
    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    // Launch the gallery picker
    private void launchGallery() {
        launcher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openCall(View view) {
        TextView nameTxt = findViewById(R.id.edtTxtName);
        EditText edtTxtSeconds = findViewById(R.id.edtTxtSeconds);
        if (photo != null && edtTxtNumber.getText().length() == 15 && nameTxt.getText() != null &&
                !nameTxt.getText().toString().isEmpty() &&
                nameTxt.getText().toString().trim().length() > 0 && edtTxtSeconds.getText() != null) {
            TextView numberTxt = findViewById(R.id.edtTxtNumber);
            String name_ = nameTxt.getText().toString();
            String number_ = numberTxt.getText().toString();
            String seconds = edtTxtSeconds.getText().toString();
            Intent intent = new Intent(this, CountdownService.class);
            intent.putExtra("nameData", name_);
            intent.putExtra("numberData", number_);
            intent.putExtra("photo", photo.toString());
            intent.putExtra("seconds", seconds);

            startService(intent);
        }
        else if (photo == null){
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        }
        else if (edtTxtNumber.getText().length() != 15){
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
        }
        else if (edtTxtSeconds.getText() == null){
            Toast.makeText(this, "Unset timer", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
        }
    }
}
