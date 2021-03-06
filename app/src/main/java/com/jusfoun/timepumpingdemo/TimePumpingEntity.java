package com.jusfoun.timepumpingdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;

/**
 * @Description ${TODO}
 */
public class TimePumpingEntity {

    public static final int TYPE_LINE = 0; // 类型-年份 线
    public static final int TYPE_IMAGE = 1;// 类型-实体
    private int imgWidth = 0;// 图片宽度
    private int imgHeight = 0;// 图片高度
    private Context mContext;
    private int offsetHeight = 0;// 偏移高度
    private int screenWidth = 0;//屏幕宽度
    private int screenHeight = 0;//屏幕高度
    private TimePumpingView layout;

    private float currentY = 0;
    private float currentX = 0;
    private int width, height;


    private int type;

    private String year;

    private int saveWidth, saveHeight; // 保存 当前 图片的宽高度

    private float ratio;
    private ValueAnimator valueAnimatorScale;


    public TimePumpingEntity(Context mContext, TimePumpingView layout) {
        this.mContext = mContext;
        offsetHeight = Utils.dip2px(mContext, 80);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.img_time_down);
        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.img_time_up);
        ratio = (bitmap1.getHeight() + bitmap.getHeight() / 2) / (float) (bitmap.getHeight() + bitmap1.getHeight());
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        imgWidth = width / 8;
        imgHeight = height / 8;
        screenWidth = Utils.getDisplayWidth(mContext);
        screenHeight = Utils.getDisplayHeight(mContext);
        saveWidth = imgWidth;
        saveHeight = imgHeight;
        valueAnimatorScale = ValueAnimator.ofFloat(0.0F, 0.8f);

        this.layout = layout;

    }

    private int index;
    public void setIndex(int index){
        this.index=index;
    }
    private View imageView;
    private TextView titleText, contentText, timeText;
    private TextView txt1, txt2;

    public void createView(int type) {
        switch (type) {
            case TYPE_LINE:
                imageView = LayoutInflater.from(mContext).inflate(R.layout.time_line_layout, null);
                txt1 = (TextView) imageView.findViewById(R.id.txt1);
                txt2 = (TextView) imageView.findViewById(R.id.txt2);
                txt1.setText(year);
                txt2.setText(year);
                break;
            case TYPE_IMAGE:
                imageView = new TImeItemView(mContext);
                titleText = (TextView) imageView.findViewById(R.id.text_title);
                timeText = (TextView) imageView.findViewById(R.id.text_time);
                contentText = (TextView) imageView.findViewById(R.id.text_content);
                titleText.setText(timeText.getText().toString()+index);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(layout.getAnimaIsRunning()){
                            return;
                        }
                        Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();

                        if (layout.getBitEntity() != null && layout.getBitEntity().isRunning()) {
                            return;
                        }

                        if (layout.getBitEntity() != null) {
                            if (layout.getBitEntity().imageView!=null&&layout.getBitEntity().imageView.getTag() != null && (boolean) layout.getBitEntity().imageView.getTag()) {
                                layout.getBitEntity().startEnlargeAnimation();
                            }
                            if (layout.getBitEntity() != TimePumpingEntity.this) {
                                handler.sendEmptyMessageDelayed(1, 1100);
                            }
                        }
                        else {
                                moveAnimation();
                        }
                    }
                });

                break;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(saveWidth, saveHeight);
        imageView.setLayoutParams(params);
        imageView.setAlpha(0f);

    }

    private float lastMove;

    public int getHeight() {
        return offsetHeight - imgHeight;
    }

    public void setImageViewParams(float moveY, int angle) {

        if ((currentY + moveY - (offsetHeight - imgHeight)) > 0 && (currentY + moveY - (offsetHeight - imgHeight)) < screenHeight) {

            if (imageView == null) {
                createView(type);
                if (lastMove - currentY < 0)
                    layout.addView(imageView, 0);
                else
                    layout.addView(imageView);
            }
            switch (type) {
                case TYPE_LINE:
                    changeLine(moveY);
                    break;
                case TYPE_IMAGE:
                    changeImage(moveY, angle);
                    break;
            }
        } else {
            if (imageView != null) {
                layout.removeView(imageView);
                imageView = null;
            }
        }
        lastMove = currentY;
        currentY+=moveY;
    }

    /**
     * 改变 年份线 相关
     * */
    private void changeLine(float moveY) {

        currentX = screenWidth / 2;
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        imageView.setX((float) (currentX - (currentY + moveY) * Math.tan(Math.PI * (60 / 180.0f))));
        imageView.setY(currentY + moveY - params.height / 2);
        float size = ((currentY + moveY) / screenHeight) * 25;
        txt1.setTextSize(size);
        txt2.setTextSize(size);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = (int) (screenWidth - imageView.getX() * 2);
        imageView.requestLayout();
        if (currentY + moveY < screenHeight / 4) {
            float alpha = (currentY + moveY) / (screenHeight / 2.5f);
            imageView.setAlpha(alpha);
        } else if (currentY + moveY < screenHeight / 3) {
            imageView.setAlpha(1f);
        } else {
            float alpha = 1.0f - (currentY + moveY - screenHeight / 3) / (screenHeight / 3);
            imageView.setAlpha(alpha);
        }
    }

    private void changeImage(float moveY, float angle) {

        /**
         * 修改图片大小相关
         * */
        if (moveY + currentY - offsetHeight <= 0)
            return;
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        float scale = 1;
        saveWidth = (int) (imgHeight * (1 + (imageView.getY() - offsetHeight + imgWidth) / Utils.dip2px(mContext, 26)));
        saveHeight = (int) (imgHeight * (1 + (imageView.getY() - offsetHeight + imgHeight) / Utils.dip2px(mContext, 14)));

        params.height = (int) (imgHeight * (1 + (moveY + currentY - offsetHeight) / Utils.dip2px(mContext, 14)));
        params.width = (int) (imgWidth * (1 + (moveY + currentY - offsetHeight) / Utils.dip2px(mContext, 26)));

        /**
         * 修改 字体 大小相关
         * */
        float scaleSize = ((currentY + moveY - offsetHeight / 3) / screenHeight);

        titleText.setTextSize(scaleSize * 20);
        titleText.setPadding((int) (scaleSize * 30), (int) (scaleSize * 20), 0, 0);

        timeText.setTextSize(scaleSize * 14);
        timeText.setPadding((int) (scaleSize * 30), 0, 0, (int) (scaleSize * 10));

        contentText.setTextSize(scaleSize * 14);
        contentText.setPadding((int) (scaleSize * 30), (int) (scaleSize * 10), (int) (scaleSize * 30), (int) (scaleSize * 30));

        imageView.requestLayout();

        /**
         * 修改坐标相关
         * */
        float y = (currentY + moveY) * scale - params.height * ratio;
        currentX = screenWidth / 2 - (float) ((currentY + moveY - offsetHeight) * scale * Math.tan(Math.PI * (angle / 180.0f))) - params.width / 2;

        imageView.setX(currentX);
        imageView.setY(y);


        /**
         * 修改透明度相关
         * */
        if (currentY + moveY < screenHeight / 4 + Utils.dip2px(mContext, 20)) {
            float alpha = (currentY + moveY - screenHeight / 4) / (Utils.dip2px(mContext, 20) * 1f);
            imageView.setAlpha(alpha);
        } else if (currentY + moveY > screenHeight / 3 && currentY + moveY < screenHeight * 3 / 5) {
            imageView.setAlpha(1f);
        } else {
            float alpha = 1.0f - (currentY + moveY - screenHeight * 3 / 5) / (screenHeight / 5);
            imageView.setAlpha(alpha);
            if (alpha < 0.2 && (imageView.getTag() != null && (boolean) imageView.getTag())) {
                startEnlargeAnimation();
            }
        }

        imageView.setClickable(true);
    }


    public void setType(int type) {
        this.type = type;
    }

    public void init(int y) {
        currentY = y;
    }

    public void setMoveY(float moveY) {
        if (index==16)
            Log.e("tag","moveYmoveY="+moveY);
        this.currentY = currentY + moveY;
    }

    public void setYear(String year) {
        this.year = year;
    }

    private int enlargeWidth = 0;
    private int enlargeHeight = 0;

    private float enlargeX = 0;
    private float enlageY = 0;

    /**
     * 事件 放大，缩小 动画
     */
    protected void startEnlargeAnimation() {


        if (imageView == null) {
            return;
        }
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        enlargeWidth = params.width;
        enlargeHeight = params.height;

        enlargeX = imageView.getX();
        enlageY = imageView.getY();
        if (!valueAnimatorScale.isRunning()) {
            if (imageView.getTag() != null && (boolean) imageView.getTag()) {
                enlargeWidth = (int) imageView.getTag(R.id.tag_first);
                enlargeHeight = (int) imageView.getTag(R.id.tag_second);

                valueAnimatorScale = ValueAnimator.ofFloat(0.8F, 0.0F);

                enlargeX = (float) imageView.getTag(R.id.tag_third);
                enlageY = (float) imageView.getTag(R.id.tag_forth);
            } else {
                valueAnimatorScale = ValueAnimator.ofFloat(0.0F, 0.8F);
                imageView.setTag(R.id.tag_first, params.width);
                imageView.setTag(R.id.tag_second, params.height);
                imageView.setTag(R.id.tag_third, imageView.getX());
                imageView.setTag(R.id.tag_forth, imageView.getY());
            }
        }


        valueAnimatorScale.setDuration(800);
        valueAnimatorScale.setInterpolator(new LinearInterpolator());
        valueAnimatorScale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layout.setAnimaIsRuning(true);
                if (imageView != null) {
                    params.width = (int) (enlargeWidth * (1f + (float) animation.getAnimatedValue()));
                    params.height = (int) (enlargeHeight * (1f + (float) animation.getAnimatedValue()));
                    imageView.setY(Math.abs(enlageY - (params.height - enlargeHeight)));
                    imageView.setX(Math.abs(enlargeX - (params.width / 2 - enlargeWidth / 2)));
                    imageView.requestLayout();
                }


            }
        });
        valueAnimatorScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
               layout.setAnimaIsRuning(false);
                if (imageView == null) {
                    return;
                }
                if (imageView.getTag() != null && (boolean) imageView.getTag()) {
                    imageView.setTag(false);
                    layout.setBitEntity(null);
                } else {
                    layout.setBitEntity(TimePumpingEntity.this);
                    imageView.setTag(true);
                }
            }
        });
        valueAnimatorScale.start();


    }

    private float lastCount=0;
    /**
     * 点击 图片移动至 可 放大位置
     * */
    protected void moveAnimation() {

        lastCount=0;
        final float moveDistance = screenHeight / 5 * 3 - currentY;
        Log.e("moveDistance",moveDistance+"");
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0F, moveDistance);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float move= (Float) animation.getAnimatedValue();
                layout.pointMove(move-lastCount, true);
                lastCount=move;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layout.pointUp(moveDistance, true);
                startEnlargeAnimation();
            }
        });

        valueAnimator.start();
    }


    protected void moveForwardAnimation(){
        final float moveDistance = currentY-screenHeight / 5 * 3 ;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layout.pointMove(-moveDistance * (float) animation.getAnimatedValue(), true);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layout.pointUp( -moveDistance, true);
                startEnlargeAnimation();
            }
        });

        valueAnimator.start();
    }


    /**
     * 当前 动画 是否在执行
     * */
    protected boolean isRunning() {
        if (!valueAnimatorScale.isRunning()) {
            return false;
        } else {
            return true;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (currentY < screenHeight / 5 * 3 && (imageView.getTag() == null || !(boolean) imageView.getTag())) {
                moveAnimation();
            } else {


                if (!isRunning()) {
                    startEnlargeAnimation();
                }
            }
        }
    };


}
