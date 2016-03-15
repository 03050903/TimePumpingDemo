package com.jusfoun.timepumpingdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @Description ${TODO}
 */
public class TImeItemView extends RelativeLayout {
    private Context mContext;

    public TImeItemView(Context context) {
        super(context);
        this.mContext = context;
        initViews();
    }

    public TImeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initViews();
    }

    public TImeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.time_item_layout, this, true);
    }

    private float donwY;
    private boolean isMove;
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//
////        Log.e("tag","view-onInterceptTouchEvent1");
////        switch (event.getAction()) {
////            case MotionEvent.ACTION_DOWN:
////                donwY = event.getY();
////                getParent().requestDisallowInterceptTouchEvent(true);
////                Log.e("tag", "view-onInterceptTouchEvent2");
////                break;
////            case MotionEvent.ACTION_MOVE:
////
////            case MotionEvent.ACTION_UP:
////                Log.e("tag","view-onInterceptTouchEven3");
////                if (Math.abs(donwY - event.getY()) > 10) {
//////                    Log.e("tag", "TImeItemView-onTouchEvent1");
////                    Log.e("tag","view-onInterceptTouchEvent4");
////                    getParent().requestDisallowInterceptTouchEvent(false);
////
////                } else {
////                    getParent().requestDisallowInterceptTouchEvent(true);
////                    Toast.makeText(mContext, "12345", Toast.LENGTH_SHORT).show();
////                }
//////                if (!isMove){
//////                    Toast.makeText(mContext,"12345",Toast.LENGTH_SHORT).show();
//////                    return true;
//////                }else {
//////                    return false;
//////                }
////
////
////        }
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_UP:
//                Log.e("tag", "onTouchEvent-vie6");
//                break;
//            case MotionEvent.ACTION_DOWN:
//                Log.e("tag", "onTouchEvent-vie1");
//                donwY = event.getY();
//
//
//        }
//
//        return super.onInterceptTouchEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                donwY = event.getY();
//                Log.e("tag", "view-ACTION_DOWN");
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                if (Math.abs(donwY - event.getY()) > 10) {
//                    return false;
//                } else {
//                    return true;
//                }
//
//            case MotionEvent.ACTION_UP:
//                Toast.makeText(mContext, "12345", Toast.LENGTH_SHORT).show();
//
//        }
//        return true;
//    }



}
