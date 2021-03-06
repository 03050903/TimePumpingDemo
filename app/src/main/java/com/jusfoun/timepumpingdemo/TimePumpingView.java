package com.jusfoun.timepumpingdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description ${TODO}
 */
public class TimePumpingView extends RelativeLayout {

    private Context mContext;
    private int screenHeight = 0;//屏幕高度

    private int imgHeight = 200;// 图片的高度
    private int index = 0; // 用以统计 数据的 数量

    private int totalLength;


    private List<HashMap<String, Object>> firstList;// 存放数据

    int initY = 0; // 图片的初始化 y坐标

    private int timeCount = 0;// 用以惯性


    private int offsetHeight = 0;// 顶端偏移 高度

    private VelocityTracker mVelocityTracker;

    private TimePumpingEntity bitEntity; // 上一个 放大的实体变量

    public TimePumpingView(Context context) {
        super(context);
        mContext = context;
        initData();
        initViews();
        initActions();
    }

    public TimePumpingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initData();
        initViews();
        initActions();
    }

    public TimePumpingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initViews();
        initActions();
    }

    private void initData() {
        offsetHeight = Utils.dip2px(mContext, 80);
        screenHeight = Utils.getDisplayHeight(mContext);
        imgHeight = Utils.dip2px(mContext, 10);

        initY = screenHeight / 5 * 3 - imgHeight;

        firstList = new ArrayList<>();
        mMaximumVelocity = ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity();


    }

    private void initViews() {
        setBackgroundResource(R.mipmap.bg);
    }

    private void initActions() {
        addImageView();
    }

    public void addImageView() {
        currentY = 0;
//        addData();
    }

    float downY = 0;
    float moveY;
    private float currentY = 0;

    private boolean isDoublePoint = false;

    private int mMaximumVelocity;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        VelocityTracker velocityTracker = VelocityTracker.obtain();
//        velocityTracker.addMovement(event);
//        velocityTracker.computeCurrentVelocity(1000);

//        currentMoveSpeed = (int)velocityTracker.getYVelocity()/1000;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        if (event.getPointerCount() >= 2) {
            isDoublePoint = true;
            return true;
        }
        if (animaIsRuning) {
            isDoublePoint = false;
            downY = event.getY();
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_DOWN:
                restore();
                isDoublePoint = false;
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isDoublePoint)
                    break;
                /*************超出后禁止滑动***************/
                /**
                 * 第一个点向上滑出屏幕或者最好一个点向下滑出屏幕禁止滑动
                 */

                restore();

                if (event.getY() - downY < 0) {
                    if (currentY+event.getY()-downY<0) {
                        moveY = -currentY;
                        pointMove(moveY, true);
                        currentY = 0;
                        downY=event.getY();
                        return true;
                    }
                }
                else if (event.getY()-downY>0){
                    if (currentY+event.getY()-downY>totalLength){
                        moveY=totalLength-currentY;
                        pointMove(moveY,true);
                        currentY=totalLength;
                        downY=event.getY();
                        return true;
                    }
                }
                /*************超出后禁止滑动***************/

                moveY = -downY + event.getY();
                pointMove(moveY, true);
                downY=event.getY();
                return true;
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                // TODO 暂时屏蔽惯性
//                if (Math.abs(moveY) > 50 && Math.abs(mVelocityTracker.getYVelocity()) > 500) {
////                    handler.sendEmptyMessage(100);
//                    if (moveY < 0) {
//                        inertiaAnimation(true);
//                    } else {
//                        inertiaAnimation(false);
//                    }
//                } else {
//                    pointUp(currentY, moveY, true);
//                }
                pointUp(currentY, moveY, true);
                return true;


            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllViews();
    }

    public interface OnTouchChangeListener {
        void touchUp(float currentY, float moveY);

        void touchMove(float moveY);

        void addData(int count);

    }

    private OnTouchChangeListener listener;

    public void setListener(OnTouchChangeListener listener) {
        this.listener = listener;
    }

    public boolean pointUp(float currentY, float moveY, boolean isHasListener) {

            if (listener != null && isHasListener)
                listener.touchUp(currentY, moveY);
            return true;
    }

    public void pointUp(float moveY, boolean isHasListener) {
        pointUp(currentY, moveY, isHasListener);
    }

    private int count = 0;

    public boolean pointMove(float moveY, boolean isHasListener) {
//        if (Math.abs(moveY) > 10) {
            Log.e("moveXY", moveY + "");
            for (int i = 0; i < firstList.size(); i++) {
                HashMap<String, Object> map = firstList.get(i);
                if ("1".equals(map.get("type"))) {
                    ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, 55);
                } else if ("2".equals(map.get("type"))) {
                    ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, 0);
                } else if ("3".equals(map.get("type"))) {
                    ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, -55);
                } else {
                    ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, -1);
                }
            }
        currentY+=moveY;

            /**
             * 动态添加数据
             * */
//            if (4000 * (count + 1) < Math.abs(currentY + moveY)) {
//                addData();
//                count++;
//                if (listener != null)
//                    listener.addData(18);
//            }

            if (listener != null && isHasListener) {
                listener.touchMove(moveY);
            }
            return true;
//        }
//        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (animaIsRuning) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDoublePoint = false;
                downY = ev.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(downY - ev.getY()) > 20) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                return false;

        }
        return super.onInterceptTouchEvent(ev);
    }


    public void addInitData() {
        index = 0;

        HashMap<String, Object> map1 = new HashMap<>();
        TimePumpingEntity timePumpingEntit1 = new TimePumpingEntity(mContext, this);
        timePumpingEntit1.init(initY - (int) (offsetHeight * 1));
        timePumpingEntit1.setType(TimePumpingEntity.TYPE_IMAGE);
        map1.put("type", "3");
        map1.put("value", timePumpingEntit1);
        firstList.add(map1);
        index++;

        HashMap<String, Object> map = new HashMap<>();
        TimePumpingEntity timePumpingEntity = new TimePumpingEntity(mContext, this);
        timePumpingEntity.init(initY - (int) (offsetHeight * 0));
        timePumpingEntity.setType(TimePumpingEntity.TYPE_IMAGE);
        map.put("type", "2");
        map.put("value", timePumpingEntity);
        firstList.add(map);
        index++;

    }

    public void addData(TimeModel model) {
//        addInitData();
//        index = 0;

        List<TimeItemModel> datas = model.getData();
        for (TimeItemModel data : datas) {

            List<TimeItemValueModel> values = data.getValue();
            for (TimeItemValueModel value : values) {
                HashMap<String, Object> map = new HashMap<>();
                TimePumpingEntity timePumpingEntity = new TimePumpingEntity(mContext, this);
                timePumpingEntity.init(initY - (int) (offsetHeight * index));
                timePumpingEntity.setType(TimePumpingEntity.TYPE_IMAGE);
                timePumpingEntity.setIndex(index);
                map.put("type", value.getType());
                map.put("value", timePumpingEntity);
                firstList.add(map);
                index++;
            }
            HashMap<String, Object> map1 = new HashMap<>();
            TimePumpingEntity timePumpingEntity1 = new TimePumpingEntity(mContext, this);
            timePumpingEntity1.init(initY - (int) (offsetHeight * index));
            timePumpingEntity1.setType(TimePumpingEntity.TYPE_LINE);
            timePumpingEntity1.setYear(data.getYear());
//            timePumpingEntity1.setImageViewParams(0, -1);
            map1.put("type", "-1");
            map1.put("value", timePumpingEntity1);
            firstList.add(map1);
            index++;
        }

        for (int i = 0; i < firstList.size(); i++) {
            HashMap<String, Object> map = firstList.get(i);
            if ("1".equals(map.get("type"))) {
                ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, 55);
            } else if ("2".equals(map.get("type"))) {
                ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, 0);
            } else if ("3".equals(map.get("type"))) {
                ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, -55);
            } else {
                ((TimePumpingEntity) map.get("value")).setImageViewParams(moveY, -1);
            }
        }

        totalLength=firstList.size()*offsetHeight;

    }


    private int j = 1;
    private int inertia = 15;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (moveY > 0) {
                pointMove(moveY + inertia, true);
                moveY = moveY + inertia;
            } else {
                pointMove(moveY - inertia, true);
                moveY = moveY - inertia;
            }
            timeCount += inertia;
            if (timeCount >= 80) {
                j = 1;
                timeCount = 0;
                pointUp(currentY, moveY, true);
            } else {
                if (moveY > 0) {
                    pointMove(moveY + inertia, true);
                    moveY = moveY + inertia;
                } else {
                    pointMove(moveY - inertia, true);
                    moveY = moveY - inertia;
                }

                handler.sendEmptyMessageDelayed(100, 10 + j * 2);
                j++;
            }
        }
    };


    protected void inertiaAnimation(final boolean isUp) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 30f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if (!isUp) {
                    pointMove(moveY + (float) animation.getAnimatedValue(), true);
                    moveY = moveY + (float) animation.getAnimatedValue();
                } else {
                    pointMove(moveY - (float) animation.getAnimatedValue(), true);
                    moveY = moveY - (float) animation.getAnimatedValue();
                }
            }

        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pointUp(currentY, moveY, true);
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(400);
        animator.start();
    }


    public void setBitEntity(TimePumpingEntity entity) {
        bitEntity = entity;
    }

    public TimePumpingEntity getBitEntity() {
        return bitEntity;
    }

    /**
     * 恢复 大小
     */
    protected void restore() {
        if (bitEntity != null && !bitEntity.isRunning()) {
            bitEntity.startEnlargeAnimation();
            bitEntity = null;
        }
    }

    /**
     * 是否 有动画再执行
     * true 有, false没有
     */
    boolean animaIsRuning = false;

    public void setAnimaIsRuning(boolean animaIsRuning) {
        this.animaIsRuning = animaIsRuning;
    }

    public boolean getAnimaIsRunning() {
        return animaIsRuning;
    }


}
