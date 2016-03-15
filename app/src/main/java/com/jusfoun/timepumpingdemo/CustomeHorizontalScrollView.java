package com.jusfoun.timepumpingdemo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @Description ${TODO}
 */
public class CustomeHorizontalScrollView extends View {
    private Context mContext;
    private Paint graduationPaint,paint;
    public CustomeHorizontalScrollView(Context context) {
        super(context);
        this.mContext = context;

        graduationPaint = new Paint();
        graduationPaint.setColor(Color.WHITE);
        graduationPaint.setAntiAlias(true);
        graduationPaint.setStrokeWidth(1);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        setWillNotDraw(false);
    }

    public CustomeHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        graduationPaint = new Paint();
        graduationPaint.setColor(Color.WHITE);
        graduationPaint.setAntiAlias(true);
        graduationPaint.setStrokeWidth(1);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        setWillNotDraw(false);
    }

    public CustomeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        graduationPaint = new Paint();
        graduationPaint.setColor(Color.WHITE);
        graduationPaint.setAntiAlias(true);
        graduationPaint.setStrokeWidth(1);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("tag", "ondraw");
        canvas.save();
        canvas.drawColor(Color.parseColor("#00000000"));



        for (int i = 0; i < 10; i++) {
            float startX = Utils.dip2px(mContext, 5) * (i + 1);
            float startY = 1080 - Utils.dip2px(mContext, 7);
            if (i % 5 == 0) {
                startY = 1080 - Utils.dip2px(mContext, 14);
            }
            canvas.drawLine(startX, startY, startX, 1080, graduationPaint);
        }

        for (int i = 0; i < 10; i++) {



            canvas.drawBitmap( BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.img_biaoji), i*100, Utils.dip2px(mContext,50)/2, paint);
        }

        canvas.restore();
    }
}
