package com.vkpapps.soundbooster.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class CircleAnimation extends View implements View.OnTouchListener {
    ArrayList<Circle> circles = new ArrayList<>();
    Random random;
    Paint paint;

    public CircleAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        random = new Random(500);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(85);
        paint.setStrokeWidth(1.8f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < circles.size(); i++) {
            Circle circle = circles.get(i);
            canvas.drawCircle(circle.cx, circle.cy, circle.r, paint);
            circle.change();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        circles.add(new Circle(event.getX(), event.getY(), 15 + random.nextFloat()));
        return true;
    }

    class Circle {

        float cx, cy, r;
        int x, y;

        Circle(float cx, float cy, float r) {
            this.cx = cx;
            this.cy = cy;
            this.r = r;
            x = random.nextInt() % 7 + 3;
            y = random.nextInt() % 5 + 2;
        }

        void change() {
            cx = cx + x;
            cy = cy + y;
            r = r + .0002f;
            if (cx < 0 || cy < 0 || cx > getWidth() || cy > getHeight()) {
                circles.remove(this);
            }
        }

    }
}
