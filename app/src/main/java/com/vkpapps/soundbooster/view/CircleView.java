package com.vkpapps.soundbooster.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.vkpapps.soundbooster.R;

import java.util.ArrayList;

public class CircleView extends View {
    private final Paint paint;
    private boolean init;
    private float change;
    private int c = 0;
    private ArrayList<Circle> circles = new ArrayList<>();
    private int cx, cy;
    private Bitmap bitmap;
    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(getResources().getColor(R.color.colorAccent));
    }

    public CircleView(Context context) {
        super(context);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(getResources().getColor(R.color.colorAccent));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.white));
        if (init) {
            for (int i = 0; i < circles.size(); i++) {
                Circle aFloat = circles.get(i);
                canvas.drawCircle(cx, cy, aFloat.r, paint);
                aFloat.r += 3;
                if (aFloat.r > getHeight()) circles.remove(aFloat);
            }
            c++;
            if (c == 55) {
                c = 0;
                circles.add(new Circle());
            }
        } else {
            circles.add(new Circle());
            cx = getWidth() / 2;
            cy = getHeight() / 2;
            change = getWidth() / 1000f;
            init = true;
        }
        invalidate();
    }

    private static class Circle {
        float r = 0;
    }
}
