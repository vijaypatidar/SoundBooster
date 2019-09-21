package com.vkpapps.soundbooster.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleView extends View {
    private final float[] circles = new float[5];
    private final int[] colors = new int[5];
    private final Paint paint;
    private int cnt;
    private boolean init;
    private float ch = .25f, rCh = 2f;
    private float x1, x2, y1, y2;
    private int radius;
    private OnCircleViewListener onCircleViewListener;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(8);
        paint.setStrokeJoin(Paint.Join.ROUND);

        colors[0] = Color.rgb(16, 99, 156);
        colors[1] = Color.rgb(16, 110, 173);
        colors[2] = Color.rgb(14, 125, 199);
        colors[3] = Color.rgb(13, 140, 224);
        colors[4] = Color.rgb(10, 151, 245);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb(240, 159, 29));
        int width = getWidth() / 15;
        if (!init) {
            String TAG = "";
            Log.d(TAG, "CircleView: ================================================ " + width + " " + cnt);
            for (int i = 0; i < circles.length; i++) {
                circles[i] = width * (i + 1);
            }
            radius = (int) (getWidth() / 3.5);
            x1 = getWidth() / 2;
            y1 = getHeight() / 2;
            x2 = x1;
            y2 = y1 - radius;
            init = true;
        }
        for (int i = circles.length - 1; i >= 0; i--) {
            paint.setColor(colors[i]);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, circles[i], paint);
            circles[i] = circles[i] + ch;
        }
        paint.setColor(Color.WHITE);
        canvas.drawCircle(x2, y2, 4, paint);
        canvas.drawCircle(x1, y1, 4, paint);
        canvas.drawLine(x1, y1, x2, y2, paint);
        x2 = x2 - rCh;
        y2 = (float) Math.sqrt(radius * radius - Math.pow(x1 - x2, 2)) + y1;
        cnt++;
        if (cnt == width * 4) {
            cnt = 0;
            ch = ch > 0 ? -.25f : .25f;
        }

        if (rCh < 0) y2 = y2 - (y2 - y1) * 2;
        if (x2 > radius + x1 || x2 < x1 - radius) {
            rCh = rCh * (-1);
            if (onCircleViewListener != null) onCircleViewListener.onRoundComplete();
        }
        if (x1 == x2) {
            if (onCircleViewListener != null) onCircleViewListener.onRoundComplete();
        }

        paint.setColor(Color.WHITE);
        invalidate();
    }

    public void setOnCircleViewListener(OnCircleViewListener onCircleViewListener) {
        this.onCircleViewListener = onCircleViewListener;
    }

    public interface OnCircleViewListener {
        void onRoundComplete();
    }
}
