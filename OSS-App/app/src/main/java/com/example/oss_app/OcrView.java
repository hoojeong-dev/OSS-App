package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OcrView extends AppCompatActivity {

    LayoutInflater ocrLayoutInflater;
    LinearLayout ocrLayout;
    LinearLayout.LayoutParams ocrLayoutParams;

    static boolean saveState;
    static String contents, title;
    TessBaseAPI tessBaseAPI;
    Button button;
    ImageView imageView;
    CameraSurfaceView surfaceView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_view);
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.INVISIBLE);

        title = null;
        saveState = false;
        contents = null;

        imageView = findViewById(R.id.imageView);
        surfaceView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.textView);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });

        tessBaseAPI = new TessBaseAPI();
        String dir = getFilesDir() + "/tesseract";
        if (checkLanguageFile(dir + "/tessdata"))
            tessBaseAPI.init(dir, "eng");
    }

    public void backPage(View v){
        Intent intent = new Intent(this, NewsList.class);
        intent.putExtra("value", "My");
        startActivity(intent);
    }

    public void saveContents(View v){
        ocrLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ocrLayout = (LinearLayout) ocrLayoutInflater.inflate(R.layout.activity_enter_title, null);
        MainActivity.viewPoint = findViewById(R.id.view_point);
        ocrLayout.setBackgroundColor(Color.parseColor("#99000000"));
        ocrLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(ocrLayout, ocrLayoutParams);
    }

    public void savetitle(View v){

        EditText gettitle = (EditText) findViewById(R.id.title);
        title = String.valueOf(gettitle.getText());

        System.out.println("==========================" + title);

        Intent intent = new Intent(this, NewsList.class);
        intent.putExtra("value", "My");
        startActivity(intent);
    }

    boolean checkLanguageFile(String dir) {
        File file = new File(dir);
        if (!file.exists() && file.mkdirs())
            createFiles(dir);
        else if (file.exists()) {
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if (!langDataFile.exists())
                createFiles(dir);
        }

        return true;
    }

    private void createFiles(String dir) {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("eng.traineddata");

            String destFile = dir + "/eng.traineddata";

            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void capture() {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = GetRotatedBitmap(bitmap, 90);

                imageView.setImageBitmap(bitmap);

                button.setEnabled(false);
                button.setText("텍스트 인식중...");
                new AsyncTess().execute(bitmap);

                camera.startPreview();
            }
        });
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {
            String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
            result = result.replaceAll(match, " ");
            result = result.replaceAll(" ", "");
            textView.setText(result);

            contents = result;
            saveState = true;

            button.setEnabled(true);
            button.setText("텍스트 인식");
        }
    }
}