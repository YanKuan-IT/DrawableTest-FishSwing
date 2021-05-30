package com.example.drawabletest;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FishDrawable extends Drawable {

    private Paint mPaint;
    private Path mPath;

    private int OTHER_ALPHA = 110;
    private int BODY_ALPHA = 160;

    // 鱼的重心
    private PointF middlePoint;
    // 鱼朝向的角度
    private float fishMainAngle = 60;

    // 鱼头的半径
    private float HEAD_RADIUS = 50;
    // 鱼身的长度
    private float BODY_LENGTH = 3.2f * HEAD_RADIUS;
    // 鱼鳍的起始点至鱼头重心的距离
    private float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;
    // 鱼鳍的长度
    private float FINS_LENGTH = 1.3f * HEAD_RADIUS;
    // 大圆的半径
    private float BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADIUS;
    // 中圆的半径
    private float MIDDLE_CIRCLE_RADIUS = 0.6f * BIG_CIRCLE_RADIUS;
    // 小圆半径
    private float SMALL_CIRCLE_RADIUS = 0.4f * MIDDLE_CIRCLE_RADIUS;
    // --寻找尾部中圆圆心的线长
    private final float FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS * (0.6f + 1);
    // --寻找尾部小圆圆心的线长
    private final float FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f);
    // --寻找大三角形底边中心点的线长
    private final float FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f;

    // 鱼眼睛的半径
    private float EYE_RADIUS = 10;

    float currentValue = 0;


    public FishDrawable(){
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setARGB(OTHER_ALPHA, 244, 92, 71);

        mPath = new Path();

        // 鱼的重心
        middlePoint = new PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS);

        // 循环获取 0 - 360之间的值
        final ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(0, 3600f);
        valueAnimator2.setDuration(15 * 1000);
        valueAnimator2.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.setInterpolator(new LinearInterpolator());
        valueAnimator2.start();
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float num = (float) valueAnimator.getAnimatedValue();
                Log.d("BBBBBBBB", "循环打印0 - 3600之间的值: "+num);

                currentValue = num;

                invalidateSelf();
            }
        });

    }


    @Override
    public void draw(@NonNull Canvas canvas) {

//        float fishAngle = fishMainAngle;
        // 整个鱼摆动的角度
        float fishAngle = (float) (fishMainAngle + Math.sin(Math.toRadians(currentValue * 1.2))*10);

        // 鱼头的圆心坐标
        PointF headPoint = calculatePoint(middlePoint, BODY_LENGTH/2, fishAngle);
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint);

        // 右鱼鳍
        PointF rightFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle-110);
        makeFins(canvas, rightFinsPoint, fishAngle, true);

        // 左鱼鳍
        PointF leftFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle+110);
        makeFins(canvas, leftFinsPoint, fishAngle, false);

        PointF bodyBottomCenterPoint = calculatePoint(headPoint, BODY_LENGTH, fishAngle-180);
        // 节肢1
        PointF middleCenterPoint = makeSegment(canvas, bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS, FIND_MIDDLE_CIRCLE_LENGTH, fishAngle, true);
        // 节肢2
        makeSegment(canvas, middleCenterPoint, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS, FIND_SMALL_CIRCLE_LENGTH, fishAngle, false);

        // 尾巴
        makeTriangel(canvas, middleCenterPoint, FIND_TRIANGLE_LENGTH, BIG_CIRCLE_RADIUS, fishAngle);
        makeTriangel(canvas, middleCenterPoint, FIND_TRIANGLE_LENGTH-10, BIG_CIRCLE_RADIUS-20, fishAngle);

        // 身体
        makeBody(canvas, headPoint, bodyBottomCenterPoint, fishAngle);

        // 鱼眼睛
        makeEye(canvas, headPoint, EYE_RADIUS, fishAngle, mPaint, true);
        makeEye(canvas, headPoint, EYE_RADIUS, fishAngle, mPaint, false);
    }

    private void makeEye(Canvas canvas, PointF headPoint, float eye_radius, float fishAngle, Paint mPaint, boolean isLeft) {
        PointF point;
        if (isLeft) {
            point = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 36);
        } else {
            point = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 36);
        }
        mPaint.setAlpha(OTHER_ALPHA);
        canvas.drawCircle(point.x, point.y, eye_radius, mPaint);
    }

    private void makeBody(Canvas canvas, PointF headPoint, PointF bodyBottomCenterPoint, float fishAngle) {
        // 身体的四个点
        PointF topLeftPoint = calculatePoint(headPoint,HEAD_RADIUS, fishAngle+90);
        PointF topRightPoint = calculatePoint(headPoint,HEAD_RADIUS, fishAngle-90);
        PointF bottomLeftPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, fishAngle+90);
        PointF bottomRightPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, fishAngle-90);

        // 控制点
        PointF controlLeft = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle+130);
        PointF controlRight = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle-130);

        mPath.reset();
        mPath.moveTo(topLeftPoint.x, topLeftPoint.y);
        mPath.quadTo(controlLeft.x, controlLeft.y, bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.quadTo(controlRight.x, controlRight.y, topRightPoint.x, topRightPoint.y);
        mPaint.setAlpha(BODY_ALPHA);
        canvas.drawPath(mPath, mPaint);
    }

    // 尾巴
    private void makeTriangel(Canvas canvas, PointF startPoint, float findCenterLength, float findEdgeLength, float fishAngle) {

        // 尾巴旋转
        fishAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * 1.5))*35);

        // 三角形底边的中心坐标
        PointF centerPoint = calculatePoint(startPoint, findCenterLength, fishAngle-180);
        // 三角形底边两点
        PointF leftPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle+90);
        PointF rightPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle-90);

        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.lineTo(leftPoint.x, leftPoint.y);
        mPath.lineTo(rightPoint.x, rightPoint.y);
        canvas.drawPath(mPath,mPaint);
    }

    private PointF makeSegment(Canvas canvas, PointF bodyBottomCenterPoint, float bigRadius, float smallRadius, float findSmallCircleLength, float fishAngle, boolean hasBigCircle) {


        // 鱼的分肢旋转
        if(hasBigCircle){
            // 分肢1
            fishAngle = (float) (fishAngle + Math.cos(Math.toRadians(currentValue * 1.5))*15);
        } else {
            // 分肢2
            fishAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * 1.5))*35);
        }


        // 梯形上 底圆的圆心
        PointF upperCenterPoint = calculatePoint(bodyBottomCenterPoint, findSmallCircleLength, fishAngle-180);

        if (hasBigCircle){
            // 画大圆
            canvas.drawCircle(bodyBottomCenterPoint.x, bodyBottomCenterPoint.y, bigRadius, mPaint);
        }
        // 画小圆
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint);

        // 画梯形
        // 梯形的四个点
        PointF bottomLeftPoint = calculatePoint(bodyBottomCenterPoint, bigRadius, fishAngle+90);
        PointF bottomRightPoint = calculatePoint(bodyBottomCenterPoint, bigRadius, fishAngle-90);
        PointF upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius, fishAngle+90);
        PointF upperRightPoint = calculatePoint(upperCenterPoint, smallRadius, fishAngle-90);
        mPath.reset();
        mPath.moveTo(upperLeftPoint.x, upperLeftPoint.y);
        mPath.lineTo(upperRightPoint.x, upperRightPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.lineTo(bottomLeftPoint.x, bottomLeftPoint.y);
        canvas.drawPath(mPath, mPaint);

        return upperCenterPoint;
    }

    private void makeFins(Canvas canvas, PointF startPoint, float fishAngle, boolean isRight) {
        float controlAngle = 115;

        // 鱼鳍的重点
        PointF endPoint = calculatePoint(startPoint, FINS_LENGTH, fishAngle-180);
        // 控制点
        PointF controlPoint = calculatePoint(startPoint, FINS_LENGTH * 1.8f, isRight? fishAngle - controlAngle:fishAngle+controlAngle);

        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x,endPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 根据长度、角度、及起始点，求助当前点的坐标值x / y
     * @param startPoint 起始点
     * @param length 距离起始点的直线距离
     * @param angle 角度
     * @return
     */
    private PointF calculatePoint(PointF startPoint, float length, float angle) {
        float deltaX = (float) (Math.cos(Math.toRadians(angle))*length);
        float deltaY = (float) (Math.sin(Math.toRadians(angle-180))*length);
        return new PointF(startPoint.x+deltaX, startPoint.y+deltaY);
    }

    // 设置透明度
    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    // 颜色过滤器
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    //
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int)(8.38f * HEAD_RADIUS);
    }

    @Override
    public int getIntrinsicHeight() {
        return (int)(8.38f * HEAD_RADIUS);
    }
}
