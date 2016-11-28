package com.example.qrcodelogodemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class ShowQrCodeActivity extends AppCompatActivity {
    static final int QR_CODE_SIZE = 600;
    static final int LOGO_MAX_SIZE = 180; // 600 * 0.3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qrcode);

        ImageView imageQRCode = (ImageView) findViewById(R.id.image_qrcode);

        String qrcodeContent = this.getIntent().getStringExtra(MainActivity.EXTRA_QR_CONTENT);
        Uri logoUri = Uri.parse(this.getIntent().getStringExtra(MainActivity.EXTRA_QR_LOGO_URI));
        Log.d("ShowQrCodeActivity", "qr_content: " + qrcodeContent);
        Log.d("ShowQrCodeActivity", "logo_uri: " + logoUri.toString());

        /* Gen QRCode image */
        Map hint = new HashMap();
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.MARGIN, 1);
        hint.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8.name());
        BitMatrix qrBitMatrix;

        try {
            qrBitMatrix = new QRCodeWriter().encode(qrcodeContent, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hint);
        }
        catch (Exception e) {
            Toast.makeText(this, "Cannot encode to QR Code!", Toast.LENGTH_SHORT).show();
            Log.e("ShowQrCodeActivity", "Cannot encode to QR Code: " + e.getMessage());
            return;
        }

        Bitmap qrBitmap = Bitmap.createBitmap(qrBitMatrix.getWidth(), qrBitMatrix.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < qrBitMatrix.getWidth(); x++) {
            for (int y = 0; y < qrBitMatrix.getHeight(); y++) {
                if (qrBitMatrix.get(x, y)) {  qrBitmap.setPixel(x, y, Color.BLACK);  }
                else {  qrBitmap.setPixel(x, y, Color.WHITE);  }
            }
        }

        if (!logoUri.equals(Uri.EMPTY)) {
            try {
                Bitmap logo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), logoUri);
                Canvas tempCanvas = new Canvas(qrBitmap);
                float logoMargin = (QR_CODE_SIZE - LOGO_MAX_SIZE) / 2;
                tempCanvas.drawBitmap(logo, logoMargin, logoMargin, null);
                tempCanvas.save();
            } catch (Exception e) {
                Toast.makeText(this, "Cannot read logo!", Toast.LENGTH_SHORT).show();
                Log.e("ShowQrCodeActivity", "Cannot read logo!");
                Log.e("ShowQrCodeActivity", e.getMessage());
            }
        }

        imageQRCode.setImageBitmap(qrBitmap);
    }
}
