package com.example.lo_lab_5_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {

    private final Paint paint = new Paint();
    private final Bitmap bitmap;
    private final Canvas canvas;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        bitmap = Bitmap.createBitmap(800, 1200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        clearCanvas();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawCircle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                drawLine(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                drawCircle(event.getX(), event.getY());
                break;
        }
        return true;
    }

    private void drawCircle(float x, float y) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 10, paint);
        invalidate();
    }

    private void drawLine(float x, float y) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(x, y, x, y, paint);
        invalidate();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void clearCanvas() {
        canvas.drawColor(Color.WHITE);
        invalidate();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }
}
