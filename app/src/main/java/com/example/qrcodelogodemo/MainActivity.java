package com.example.qrcodelogodemo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.soundcloud.android.crop.Crop;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    static final String EXTRA_QR_CONTENT = "com.example.qrcodelogodemo.extras.EXTRA_QR_CONTENT";
    static final String EXTRA_QR_LOGO_URI = "com.example.qrcodelogodemo.extras.EXTRA_QR_LOGO_URI";

    private EditText editQrContent;
    private ImageView imageCroppedLogo;
    private Uri croppedLogoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonUnsetLogo = (Button) findViewById(R.id.button_unset_logo);
        Button buttonShowQrCode = (Button) findViewById(R.id.button_show_qrcode);
        Button buttonReset = (Button) findViewById(R.id.button_reset);
        editQrContent = (EditText) findViewById(R.id.edit_qr_content);
        imageCroppedLogo = (ImageView) findViewById(R.id.image_cropped_logo);
        croppedLogoUri = Uri.EMPTY;

        imageCroppedLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(MainActivity.this);
            }
        });

        buttonUnsetLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCroppedLogo.setImageURI(null);
                croppedLogoUri = Uri.EMPTY;
                Log.d("MainActivity", "Unset logo.");
            }
        });

        buttonShowQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qrContent = editQrContent.getText().toString();
                Log.d("MainActivity", "edit_qr_content: " + qrContent);
                if (qrContent.isEmpty()) {
                    editQrContent.setError("Please input the content");
                    return;
                }
                showQrCode(qrContent, croppedLogoUri.toString());
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editQrContent.setText("");
                imageCroppedLogo.setImageURI(null);
                croppedLogoUri = Uri.EMPTY;
                Log.d("MainActivity", "Reset edit_qr_content");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Uri pickedLogoUri = resultData.getData();
            Uri tempUri = Uri.fromFile(new File(getCacheDir(), "temp_logo"));
            Log.d("MainActivity", "Selected a source photo to become logo.");
            Crop.of(pickedLogoUri, tempUri).asSquare().withMaxSize(ShowQrCodeActivity.LOGO_MAX_SIZE, ShowQrCodeActivity.LOGO_MAX_SIZE).start(this);
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            croppedLogoUri = Crop.getOutput(resultData);
            imageCroppedLogo.setImageURI(croppedLogoUri);
            Log.d("MainActivity", "Logo cropped.");
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    public void showQrCode(String qrContent, String qrLogoUri) {
        Intent intent = new Intent();
        intent.setClass(getBaseContext(), ShowQrCodeActivity.class);
        intent.putExtra(EXTRA_QR_CONTENT, qrContent);
        intent.putExtra(EXTRA_QR_LOGO_URI, qrLogoUri);
        startActivity(intent);
        Log.d("MainActivity", "Launch ShowQrCodeActivity");
    }
}
