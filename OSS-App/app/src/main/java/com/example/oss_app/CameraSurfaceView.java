package com.example.oss_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private Camera mCamera;
    private Context context;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mCamera = OcrView.getCamera();
        if(mCamera == null)
            mCamera = Camera.open();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            if(mCamera == null)
                mCamera = Camera.open();

            Camera.Parameters parameters = mCamera.getParameters();

            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(surfaceHolder);

            // 카메라 미리보기를 시작한다.
            mCamera.startPreview();

            // 자동포커스 설정
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {

                    }
                }
            });
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {

                    }
                });
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public boolean capture(Camera.PictureCallback callback)
    {
        if (mCamera != null)
        {
            mCamera.takePicture(null, null, callback);
            return true;
        }
        else
        {
            return false;
        }
    }
}