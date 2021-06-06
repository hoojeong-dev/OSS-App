package com.example.oss_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.InitializationCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.constant.CalibrationModeType;
import camp.visual.gazetracker.constant.InitializationErrorType;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.device.GazeDevice;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.ScreenState;
import camp.visual.gazetracker.state.TrackingState;
import camp.visual.gazetracker.util.ViewLayoutChecker;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.oss_app.calibration.CalibrationDataStorage;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA // 시선 추적 input
    };

    private static final int REQ_PERMISSION = 1000;
    private GazeTracker gazeTracker;
    private ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private HandlerThread backgroundThread = new HandlerThread("background");
    private Handler backgroundHandler;
    private boolean isUseGazeFilter = true;
    private CalibrationModeType calibrationType = CalibrationModeType.FIVE_POINT;
    private CalibrationViewer viewCalibration;
    static PointView viewPoint;
    static int pageType = 2, mode = 0;
    static int count =0, count2=0, count3=0, safebar = 0, pagenum, Lsafebar=30;
    float display_x;
    float display_y;
    static boolean magnifier;

    MyQueue xq = new MyQueue();
    MyQueue yq = new MyQueue();
    MyQueue lxq = new MyQueue();
    MyQueue lyq = new MyQueue();

    ImageButton modebtn, titleImage;
    Button btncali, sign_in_btn, sign_up_btn;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation fadein = new AlphaAnimation(0.0f, 10.f);
        fadein.setDuration(10000);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 0);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics(); // 디스플레이 메트릭스를 dm이라는 변수에 저장 dm = 디스플레이 메트릭스 형식
        display_x = dm.widthPixels; // x축 픽셀 수 = x축의 길이
        display_y = dm.heightPixels;

        titleImage = (ImageButton) findViewById(R.id.titleImage);
        modebtn = (ImageButton) findViewById(R.id.mode);
        title = (TextView) findViewById(R.id.title);
        btncali = (Button) findViewById(R.id.btncali);
        viewPoint = findViewById(R.id.view_point);
        viewCalibration = findViewById(R.id.view_calibration);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        if(MainActivity.mode == 0){
            modebtn.setBackground(ContextCompat.getDrawable(this, R.drawable.eye_2));
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        } else {
            modebtn.setBackground(ContextCompat.getDrawable(this, R.drawable.eye));
            MainActivity.viewPoint.setVisibility(View.VISIBLE);
        }

        viewPoint.setVisibility(View.INVISIBLE);
        pagenum = 0;


        titleImage.startAnimation(fadein);
        modebtn.startAnimation(fadein);
        title.startAnimation(fadein);
        btncali.startAnimation(fadein);

        initGaze();
        checkPermission();
        initHandler();
        xq.newQueue(10);
        yq.newQueue(10);
        lxq.newQueue(20);
        lyq.newQueue(20);

        btncali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalibration();
                sign_up_btn.setText("REGISTER");
                sign_in_btn.setText("Login");
                Animation fadeintstart = new AlphaAnimation(0.0f, 10.f);
                fadeintstart.setDuration(20000);
                sign_up_btn.startAnimation(fadeintstart);
                sign_in_btn.startAnimation(fadeintstart);
            }
        });
    }

    public void login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void controlMode(View v){
        ImageButton modebtn = (ImageButton) findViewById(R.id.mode);

        if(mode == 0) {
            mode = 1;
            modebtn.setBackground(ContextCompat.getDrawable(this, R.drawable.eye));
            viewPoint.setVisibility(View.VISIBLE);
        }
        else if(mode == 1) {
            mode = 0;
            modebtn.setBackground(ContextCompat.getDrawable(this, R.drawable.eye_2));
            viewPoint.setVisibility(View.GONE);
        }
    }

    private void startTracking() {
        if (isGazeNonNull()) {
            gazeTracker.startTracking();
        }
    }

    private void stopTracking() {
        if (isGazeNonNull()) {
            gazeTracker.stopTracking();
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check permission status
            if (!hasPermissions(PERMISSIONS)) {

                requestPermissions(PERMISSIONS, REQ_PERMISSION);
            } else {
                checkPermission(true);
            }
        }else{
            checkPermission(true);
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private boolean hasPermissions(String[] permissions) {
        int result;
        // Check permission status in string array
        for (String perms : permissions) {
            if (perms.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(this)) {
                    return false;
                }
            }
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                // When if unauthorized permission found
                return false;
            }
        }
        // When if all permission allowed
        return true;
    }

    private void checkPermission(boolean isGranted) {
        if (isGranted) {
            permissionGranted();
        } else {
            //showToast("not granted permissions", true);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermissionAccepted) {
                        checkPermission(true);
                    } else {
                        checkPermission(false);
                    }
                }
                break;
        }
    }

    private void permissionGranted() {

        initGaze();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        // 화면 전환후에도 체크하기 위해
        setOffsetOfView();
    }

    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(viewPoint, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                viewPoint.setOffset(x, y);
                viewCalibration.setOffset(x, y);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseHandler();
        viewLayoutChecker.releaseChecker();
        //releaseGaze();
    }

    void initHandler() {
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void releaseHandler() {
        backgroundThread.quitSafely();
    }

    private InitializationCallback initializationCallback = new InitializationCallback() {
        @Override
        public void onInitialized(GazeTracker gazeTracker, InitializationErrorType error) {
            if (gazeTracker != null) {
                initSuccess(gazeTracker);
            } else {
                initFail(error);
            }
        }
    };

    private boolean isTracking() {
        if (isGazeNonNull()) {
            return gazeTracker.isTracking();
        }
        return false;
    }
    private boolean isGazeNonNull() {
        return gazeTracker != null;
    }
    private void hideTrackingWarning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //viewWarningTracking.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void initSuccess(GazeTracker gazeTracker) {
        this.gazeTracker = gazeTracker;
        /*if (preview.isAvailable()) {
            // When if textureView available
            //setCameraPreview(preview);
        }*/
        this.gazeTracker.setCallbacks(gazeCallback, calibrationCallback, statusCallback);
        startTracking();
        //hideProgress();
    }

    private void initFail(InitializationErrorType error) {
        String err = "";
        if (error == InitializationErrorType.ERROR_CAMERA_PERMISSION) {
            // When if camera permission doesn not exists
            err = "required permission not granted";
        } else  {
            // Gaze library initialization failure
            // It can ba caused by several reasons(i.e. Out of memory).
            err = error.toString();
        }
        //showToast(err, false);
        Log.w(TAG, "error description: " + err);
        //hideProgress();
    }

    private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(2);
    private GazeCallback gazeCallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            if (isGazeNonNull()) {
                TrackingState state = gazeInfo.trackingState;
                if (state == TrackingState.SUCCESS) {
                    hideTrackingWarning();
                    if (!gazeTracker.isCalibrating()) {
                        if (isUseGazeFilter) {
                            if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
                                float[] filteredPoint = oneEuroFilterManager.getFilteredValues();
                                //System.out.println(filteredPoint[0]);
                                showGazePoint(filteredPoint[0], filteredPoint[1], gazeInfo.screenState);
                                xq.enqueue(filteredPoint[0]);
                                yq.enqueue(filteredPoint[1]);
                                lxq.enqueue(filteredPoint[0]);
                                lyq.enqueue(filteredPoint[1]);
                                count=0;
                                count2=0;
                                count3=0;

                                if(pagenum == 1 && mode == 1) {
                                    System.out.println("page1======================================");
                                    for (int i = 0; i < 10; i++) {
                                        if (xq.QArray[0] + 70 > xq.QArray[i] && xq.QArray[0] - 70 < xq.QArray[i] && yq.QArray[0] + 100 > yq.QArray[i] && yq.QArray[0] - 100 < yq.QArray[i]) {
                                            count++;
                                        }
                                    }
                                    if (count > 7 && safebar < 0) {
                                        Instrumentation inst = new Instrumentation();
                                        long downTime = SystemClock.uptimeMillis();
                                        long eventTime = SystemClock.uptimeMillis();
                                        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, xq.QArray[0], yq.QArray[0], 0);
                                        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, xq.QArray[0], yq.QArray[0], 0);
                                        inst.sendPointerSync(event);
                                        inst.sendPointerSync(event2);
                                        System.out.println("%%%%%%%%%%%%%%%%%%%");
                                        safebar = 20;
                                    }
                                }
                                else if(pagenum == 2 && mode == 1){
                                    for (int i = 0; i < 10; i++) {
                                        if (xq.QArray[0] + 70 > xq.QArray[i] && xq.QArray[0] - 70 < xq.QArray[i] && yq.QArray[0] + 100 > yq.QArray[i] && yq.QArray[0] - 100 < yq.QArray[i]) {
                                            count++;
                                        }
                                    }
                                    if (count > 7 && safebar < 0) {
                                        Instrumentation inst = new Instrumentation();
                                        long downTime = SystemClock.uptimeMillis();
                                        long eventTime = SystemClock.uptimeMillis();
                                        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, xq.QArray[0], yq.QArray[0], 0);
                                        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, xq.QArray[0], yq.QArray[0], 0);
                                        inst.sendPointerSync(event);
                                        inst.sendPointerSync(event2);
                                        System.out.println("%%%%%%%%%%%%%%%%%%%");
                                        safebar = 20;
                                    }
                                }
                                safebar--;
                                System.out.println(safebar);
                            }
                        } else {
                            showGazePoint(gazeInfo.x, gazeInfo.y, gazeInfo.screenState);
                        }
                    }
                } else {
                    showTrackingWarning();
                }
//                Log.i(TAG, "check eyeMovement " + gazeInfo.eyeMovementState);
            }
        }
    };

    private void showTrackingWarning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //viewWarningTracking.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showGazePoint(final float x, final float y, final ScreenState type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPoint.setType(type == ScreenState.INSIDE_OF_SCREEN ? PointView.TYPE_DEFAULT : PointView.TYPE_OUT_OF_SCREEN);
                viewPoint.setPosition(x, y);
                //System.out.println("x좌표는"+x);
                //System.out.print("    y좌표는"+y);
            }
        });
    }

    private void setCalibrationPoint(final float x, final float y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("9999999999999999999999999999999999999999999999");
                viewCalibration.setVisibility(View.VISIBLE);
                viewCalibration.changeDraw(true, null);
                viewCalibration.setPointPosition(x, y);
                viewCalibration.setPointAnimationPower(0);
            }
        });
    }

    private void setCalibrationProgress(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setPointAnimationPower(progress);
            }
        });
    }

    private CalibrationCallback calibrationCallback = new CalibrationCallback() {
        @Override
        public void onCalibrationProgress(float progress) {
            setCalibrationProgress(progress);
        }

        @Override
        public void onCalibrationNextPoint(final float x, final float y) {
            setCalibrationPoint(x, y);
            System.out.println("-+++++++++++-");
            // Give time to eyes find calibration coordinates, then collect data samples
            backgroundHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startCollectSamples();
                }
            }, 1000);
        }

        @Override
        public void onCalibrationFinished(double[] calibrationData) {
            // 캘리브레이션이 끝나면 자동으로 gazepoint에 적용되어있고
            // calibrationDataStorage에 calibrationData를 넣는것은 다음번에 캘리브레이션 하지않고 사용하게 하기 위함이다.
            CalibrationDataStorage.saveCalibrationData(getApplicationContext(), calibrationData);
            hideCalibrationView();
            showToast("calibrationFinished", true);
        }
    };

    private void hideCalibrationView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setVisibility(View.INVISIBLE);
            }
        });
    }

    private StatusCallback statusCallback = new StatusCallback() {
        @Override
        public void onStarted() {
            // isTracking true
            // When if camera stream starting
            //setViewAtGazeTrackerState();
        }
        @Override
        public void onStopped(StatusErrorType error) {
            // isTracking false
            // When if camera stream stopping
            //setViewAtGazeTrackerState();
            if (error != StatusErrorType.ERROR_NONE) {
                switch (error) {
                    case ERROR_CAMERA_START:
                        // When if camera stream can't start
                        showToast("ERROR_CAMERA_START ", false);
                        break;
                    case ERROR_CAMERA_INTERRUPT:
                        // When if camera stream interrupted
                        showToast("ERROR_CAMERA_INTERRUPT ", false);
                        break;
                }
            }
        }
    };

    void initGaze() {
        //showProgress();
        GazeDevice gazeDevice = new GazeDevice();
        if (gazeDevice.isCurrentDeviceFound()) {
            // 돌린 기기의 device info가 있는지확인
            Log.d(TAG, "이 디바이스는 gazeinfo 설정이 필요 없습니다.");
        } else {
            // 예시입니다. SM-T720은 갤럭시탭 s5e 모델명
            System.out.println("111111111111111111111111111111111111111111111");
            gazeDevice.addDeviceInfo("SM-T720", -10f, -3f);
        }

        String licenseKey = "dev_l0ocjnf2dyy2xbweoq182w01nad2l7k8nli5083i";
        GazeTracker.initGazeTracker(getApplicationContext(), gazeDevice, licenseKey, initializationCallback);
    }

    private void showToast(final String msg, final boolean isShort) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private boolean startCollectSamples() {
        boolean isSuccess = false;
        if (isGazeNonNull()) {
            isSuccess = gazeTracker.startCollectSamples();
        }
        //setViewAtGazeTrackerState();
        return isSuccess;
    }

    private void stopCalibration() {
        if (isGazeNonNull()) {
            gazeTracker.stopCalibration();
        }
        hideCalibrationView();
        //setViewAtGazeTrackerState();
    }

    private void setCalibration() {
        if (isGazeNonNull()) {
            double[] calibrationData = CalibrationDataStorage.loadCalibrationData(getApplicationContext());
            if (calibrationData != null) {
                // When if stored calibration data in SharedPreference
                if (!gazeTracker.setCalibrationData(calibrationData)) {
                    showToast("calibrating", false);
                } else {
                    showToast("setCalibrationData success", false);
                }
            } else {
                // When if not stored calibration data in SharedPreference
                showToast("Calibration data is null", true);
            }
        }
        //setViewAtGazeTrackerState();
    }

    private boolean startCalibration() {
        boolean isSuccess = false;
        if (isGazeNonNull()) {
            isSuccess = gazeTracker.startCalibration(calibrationType);
            if (!isSuccess) {
                showToast("calibration start fail", false);
            }
        }
        System.out.println("+------------------------------------+");
        //setViewAtGazeTrackerState();
        return isSuccess;
    }
}