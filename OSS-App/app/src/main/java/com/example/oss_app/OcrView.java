package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
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

    private static CameraSurfaceView cameraSurfaceView;
    private SurfaceHolder holder;
    public static OcrView getInstance;
    private static Camera mCamera;
    private int RESULT_PERMISSIONS = 100;

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

        requestPermissionCamera();

        title = null;
        saveState = false;
        contents = null;
    }

    public static Camera getCamera(){
        return mCamera;
    }

    private void setInit(){
        getInstance = this;

        // 왜 안 열림 시바알
        mCamera = Camera.open();
        setContentView(R.layout.activity_ocr_view);

        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);

        cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);

        holder = cameraSurfaceView.getHolder();
        holder.addCallback(cameraSurfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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

    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(OcrView.this, new String[]{Manifest.permission.CAMERA}, RESULT_PERMISSIONS);
            }else {
                setInit();
            }
        }else{  // version 6 이하일때
            setInit();
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (RESULT_PERMISSIONS == requestCode) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가시
                setInit();
            } else {
                // 권한 거부시
            }
            return;
        }
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
            //String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
            //result = result.replaceAll(match, " ");
            //result = result.replaceAll(" ", "");
            //result = "2021. 6. 3. Eye tracking -Wlklpedla One study focused on what particular features caused people to notice an ad, whether they viewed ads in a particular order and how viewing times varied. The study revealed that ad size, graphics, color, and copy all influence attention to advertisements. Knowing this allows researchers to assess in great detail how often a sample of consumers fixates on the target logo, product or ad. As a result, an advertiser can quantify the success of a given campaign in terms of actual visual attention.區Another example of this is a study that found that in a search engine resultl 1age, authorship snippets received more attention than the paid ads or even the first organic result효으 Yet another example of commercial eye-tracking research comes from the field of recruitment. A study analyzed how recruiters screen Linkedin profiles and presented results as heat maps.區Safety applications Scientists in 2017 constructed a Deep Integrated Neural Network (DINN) out of a Deep Neural Network and a convolutional neural network.~ The goal was to use deep learning to examine images of drivers and determine their level of drowsiness by \"classify[ing] eye states.\" With enough images, the proposed DINN could ideally determine when drivers blink, how often they blink, and for how long. From there, it could judge how tired a given driver appears to be, effectively conducting an eye-tracking exercise. The DINN was trained on data from over 2,400 subjects and correctly diagnosed their states 96%-99.5% of the time. Most other artificial intelligence models performed at rates above 90%.~ This technology could ideally provide another avenue for driver drowsiness detection. Game theory applications In a 2019 study, a Convolutional Neural Network (CNN) was constructed with the ability to identify individual chess pieces the same way other CNNs can identify facial features.區l1twas then fed eye-tracking input data from thirty chess players of various skilllevels. With this data, the CNN used gaze estimation to determine parts of the chess board to which a player was paying close attention. It then generated a saliency map to illustrate those parts of the board. Ultimately, the CNN would combine its knowledge of the board and pieces with its saliency map to predict the players' next move. Regardless of the training dataset the neural network system was trained upon, it predicted the next move more accurately than if it had selected any possible move at random, and the saliency maps drawn for any given player and situation were more than 54% similar.區Assistive technology People with severe motor impairment can use eye trackin? !or interacting with computers 區as it is faster than single switch scanning techniques and intmt1ve to operate.~갭띄 Motor impairment caused by Cerebral Palsy 固。rAmyotrophic lateral sclerosis often affects speech, and users with Severe Speech and Motor Impairment (SSMI) use a type of software known as Augmentative and Alternative Communication (AAC) aid,區Jthat displays icons, words and letters on screen 15.21 and uses text-to-speech software to generate spoken output陶In recent times, researchers also explored eye tracking to control robotic arms 回and powered wheelchairs.~ Eye tracking is also helpful in analysing visual search patterns,~ detecting presence of ~딱쁩혼 and detecting early signs of learning disability by analysing eye gaze movement during reading.i Aviation applications https://en.wikipedia.org/wikl/Eye_tracklng 10/19";
            textView.setText(result);

            contents = result;
            saveState = true;

            button.setEnabled(true);
            button.setText("텍스트 인식");
        }
    }
}