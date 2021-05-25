package com.example.oss_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class PointView extends View {
    int count =0;
    public PointView(Context context) {
        super(context);
        init();
    }

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private int defaultColor = Color.rgb(0x00, 0x00, 0xff);
    private int outOfScreenColor = Color.rgb(0xff, 0x00, 0x00);
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_OUT_OF_SCREEN = 1;
    private int type = TYPE_DEFAULT;
    private Paint paint;
    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#d3d3d3"));
        paint.setStrokeWidth(2f);
    }

    private float offsetX, offsetY;
    private PointF position = new PointF();
    public void setOffset(int x, int y) {
        offsetX = x;
        offsetY = y;
    }
    public void setPosition(float x, float y) {
        position.x = x - offsetX;
        position.y = y - offsetY;
        invalidate();
    }

    public void setType(int type) {
        //paint.setColor(type == TYPE_DEFAULT ? defaultColor : outOfScreenColor);
        paint.setColor(Color.parseColor("#d3d3d3"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(position.x, position.y, 50, paint );


        //canvas.drawLine(0, position.y, getWidth(), position.y, paint);
        //canvas.drawLine(position.x, 0, position.x, getHeight(), paint);
    }
}