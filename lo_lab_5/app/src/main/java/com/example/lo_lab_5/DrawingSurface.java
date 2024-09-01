package com.example.lo_lab_5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder surfaceHolder;
    private Thread drawingThread;
    private boolean isDrawing = false;
    private Paint paint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    private float lastX, lastY;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    public void clearCanvas() {
        bitmapCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public void saveDrawing() {
        try (FileOutputStream fos = getContext().openFileOutput("drawing_" + System.currentTimeMillis() + ".png", Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.WHITE);
        isDrawing = true;
        drawingThread = new Thread(this);
        drawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
        try {
            drawingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                bitmapCanvas.drawCircle(x, y, 5, paint);
                break;
            case MotionEvent.ACTION_MOVE:
                bitmapCanvas.drawLine(lastX, lastY, x, y, paint);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                bitmapCanvas.drawCircle(x, y, 5, paint);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    public void run() {
        while (isDrawing) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        canvas.drawBitmap(bitmap, 0, 0, null);
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
