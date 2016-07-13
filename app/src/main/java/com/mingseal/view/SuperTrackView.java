package com.mingseal.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mingseal.data.dao.PointDao;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointTask;
import com.mingseal.data.point.PointType;
import com.mingseal.data.point.SMatrix1_4;
import com.mingseal.utils.CommonArithmetic;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/25.
 * <p/>
 * Description:
 */
public class SuperTrackView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private MyThread myThread;

    /**
     * 放大或缩小的倍数 需要通过分辨率和行程计算*（行程/分辨率）
     */
    public static int fold = 70;// 放大或缩小的倍数 需要通过分辨率和行程计算*（行程/分辨率）

    private static final String TAG = "TrackView";
    private int backgroundColor = Color.rgb(255, 255, 255);// 背景色
    private int basePointColor = Color.rgb(0, 0, 0);// 起始点的颜色
    private int alonePointColor = Color.rgb(255, 242, 0);// 独立点的颜色
    private int pointColor = Color.rgb(255, 0, 0);// 绘制点的颜色
    private int lineColor = Color.argb(100, 255, 0, 0);// 绘制线的颜色
    private int arcColor = Color.argb(150, 255, 0, 0);// 绘制弧线的颜色
    private int wrongColor = Color.argb(255, 25, 226, 91);// 绘制错误线的颜色
    private int wrongFaceColor = Color.argb(100, 25, 226, 91);// 绘制错误线的颜色
    private Paint paint = new Paint();

    private int circle;
    private int radius = 10;// 圆点

    private PointTask pointTask;
    private List<Integer> pointIds;// 一个任务里面所有点的主键集合

    private List<Point> pointLists;// 一个任务里面所有点的集合
    private SMatrix1_4 sMatrix;
    private PointDao pointDao;

    private Point point_first, point_second, point_third;
    private SMatrix1_4 sMatrix_first, sMatrix_second, sMatrix_third;
    private int left, top, right, bottom;

    public SuperTrackView(Context context) {
        super(context);
        init(context);
    }

    public SuperTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuperTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        holder = this.getHolder();
        holder.addCallback(this);
        pointDao = new PointDao(context);
        pointLists = new ArrayList<Point>();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);

        setMeasuredDimension(measuredHeight, measuredWidth);
    }

    /**
     * 测量宽度
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {
            result = getWidth();
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 测量高度
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 接收从Activity中传递过来的数值
     *
     * @param pointTask
     * @param pointDao
     */
    public void setPointTask(PointTask pointTask, PointDao pointDao) {
//        update = true;
        this.pointTask = pointTask;
        this.pointIds = pointTask.getPointids();
        this.pointLists = pointDao.findALLPointsByIdLists(pointIds);
    }

    public void setCircle(int circle) {
        this.circle = circle;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * 两个参数进行比较
     *
     * @param num1
     * @param num2
     * @return value[0]是大的数，values[1]是小的数
     */
    private float[] compareMaxValue(float num1, float num2) {
        float[] value = new float[2];
        if (num1 > num2) {
            value[0] = num1;
            value[1] = num2;
        } else {
            value[0] = num2;
            value[1] = num1;
        }
        return value;
    }

    /**
     * 获得某一个点的点参数的点类型
     *
     * @param point
     * @return point.getPointParam().getPointType()
     */
    private PointType getPointType(Point point) {
        return point.getPointParam().getPointType();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        myThread = new MyThread(holder);//创建一个绘图线程
//        myThread.isRun = true;
//        myThread.start();
        System.out.println("surfaceCreated---->创建了");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("surfaceChanged---->改变了");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("surfaceDestroyed---->销毁了");
    }

    /**
     * activity调用onResume()是调用创建线程
     */
    public void SurfaceView_OnResume() {
        myThread = new MyThread(holder);//创建一个绘图线程
        myThread.isRun = true;
        myThread.start();
    }

    /**
     * activity调用onPause()是调用停止线程
     */
    public void SurfaceView_OnPause() {
        myThread.isRun = false;
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class MyThread extends Thread {
        private final SurfaceHolder holder;
        public boolean isRun;

        public MyThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        @Override
        public void run() {
            while (isRun) {
                Canvas c = null;
                try {
                    c = holder.lockCanvas(null);//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                    synchronized (holder) {
                        doDraw(c);
//                        if (update) {
//                            listener.notifyEvent(true);
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            }

        }
    }

    private void doDraw(Canvas canvas) {
        if (myThread.isRun) {

            canvas.drawColor(Color.WHITE);//清屏
            paint.setStrokeWidth(AutoUtils.getPercentHeightSize(2));
            paint.setAntiAlias(true);
            for (Point point : pointLists) {
                paint.setColor(pointColor);
                paint.setStyle(Paint.Style.FILL);
                // 绘图区域在900*900的画布中，取中间的800*800为显示绘图区域
            /*===================== 适配 圆点=====================*/
                radius = AutoUtils.getPercentHeightSize(4);
            /*=====================  end =====================*/
                canvas.drawCircle(point.getFoldX(fold), point.getFoldY(fold), radius, paint);
            }
            boolean faceStartFlag = false;
            // 先画出所有的线
            for (int i = 0; i < pointLists.size() - 1; i++) {

                point_first = pointLists.get(i);
                point_second = pointLists.get(i + 1);

                if (getPointType(point_first).equals(PointType.POINT_WELD_BASE)) {
                    // 基准点，单独画
                    paint.setColor(basePointColor);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(point_first.getFoldX(fold), point_first.getFoldY(fold), radius, paint);
                } else if (getPointType(point_first).equals(PointType.POINT_GLUE_ALONE)) {
                    // 如果是独立点，不需要画线段
                    paint.setColor(pointColor);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(point_first.getFoldX(fold), point_first.getFoldY(fold), radius, paint);
                } else if (getPointType(point_first).equals(PointType.POINT_GLUE_FACE_START)) {
                    // 面结束点紧跟面起点
                    if (getPointType(point_second).equals(PointType.POINT_GLUE_FACE_END)) {

                        paint.setColor(lineColor);

                        faceStartFlag = false;
                        paint.setStyle(Paint.Style.FILL);

                        left = (int) compareMaxValue(point_first.getFoldX(fold), point_second.getFoldX(fold))[1];
                        top = (int) compareMaxValue(point_first.getFoldY(fold), point_second.getFoldY(fold))[1];
                        right = (int) compareMaxValue(point_first.getFoldX(fold), point_second.getFoldX(fold))[0];
                        bottom = (int) compareMaxValue(point_first.getFoldY(fold), point_second.getFoldY(fold))[0];

                        RectF rectF = new RectF(left, top, right, bottom);
                        // 在目标图像的顶部绘制源图像
                        // paint.setXfermode(new
                        // PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                        canvas.drawRect(rectF, paint);
                        // paint.setXfermode(null);
                        i = i + 1;
                    }

                } else if (getPointType(point_first).equals(PointType.POINT_GLUE_LINE_START)) {
                    for (int j = i + 1; j < pointLists.size(); j++) {
                        if (getPointType(pointLists.get(j)).equals(PointType.POINT_GLUE_LINE_ARC)) {
                            // 圆弧点，画弧
                            // 圆弧点
                            paint.setColor(arcColor);
                            paint.setStyle(Paint.Style.STROKE);
                            point_third = pointLists.get(i + 2);

                            SMatrix1_4 mp1 = new SMatrix1_4(pointLists.get(j - 1));
                            SMatrix1_4 mp2 = new SMatrix1_4(pointLists.get(j));
                            SMatrix1_4 mp3 = new SMatrix1_4(pointLists.get(j + 1));
                            SMatrix1_4 mp = CommonArithmetic.getCenterPt(mp1, mp2, mp3);
                            SMatrix1_4 m = SMatrix1_4.operator_minus(mp, mp1);
//						Log.d(TAG, "SMatrix1_4:mp->"+mp.toString()+",mp2:"+mp2.toString()+",mp3:"+mp3.toString());

                            double r = SMatrix1_4.operator_mod3(m);

                            double arcLeft = (mp.getX() - r) / fold;// 放大倍数显示在预览上
                            double arcTop = (mp.getY() - r) / fold;
                            double arcRight = (mp.getX() + r) / fold;
                            double arcBottom = (mp.getY() + r) / fold;

                            double start = CommonArithmetic.getAngle(mp, mp1);
                            double mid = CommonArithmetic.getAngle(mp, mp2);
                            double end = CommonArithmetic.getAngle(mp, mp3);

//						Log.d(TAG, "r:" + r +",arcLeft:"+arcLeft+",arcTop:"+arcTop+",arcRight:"+arcRight+",arcBottom:"+arcBottom);
                            double startAngle, sweepAngle;

                            if (start < mid && mid < end) {
                                startAngle = start;
                                sweepAngle = end - start;
                            } else if (start < end && end < mid) {
                                startAngle = end;
                                sweepAngle = 360 - (end - start);
                            } else if (mid < start && start < end) {
                                startAngle = end;
                                sweepAngle = 360 - (end - start);
                            } else if (mid < end && end < start) {
                                startAngle = start;
                                sweepAngle = 360 - (start - end);
                            } else if (end < mid && mid < start) {
                                startAngle = end;
                                sweepAngle = start - end;
                            } else {
                                startAngle = start;
                                sweepAngle = 360 - (start - end);
                            }

                            RectF oval = new RectF((float) arcLeft, (float) arcTop, (float) arcRight, (float) arcBottom);
                            canvas.drawArc(oval, (float) startAngle, (float) sweepAngle, false, paint);

                            j = j + 1;
                            i = j;
                        } else if (getPointType(pointLists.get(j)).equals(PointType.POINT_GLUE_LINE_END)) {
                            paint.setColor(lineColor);
                            canvas.drawLine(pointLists.get(j - 1).getFoldX(fold), pointLists.get(j - 1).getFoldY(fold),
                                    pointLists.get(j).getFoldX(fold), pointLists.get(j).getFoldY(fold), paint);
                            i = j;
                            break;
                        } else if (getPointType(pointLists.get(j)).equals(PointType.POINT_GLUE_LINE_MID)) {
                            paint.setColor(lineColor);
                            canvas.drawLine(pointLists.get(j - 1).getFoldX(fold), pointLists.get(j - 1).getFoldY(fold),
                                    pointLists.get(j).getFoldX(fold), pointLists.get(j).getFoldY(fold), paint);
                            i = j;
                        }

                    }
                }
            }
        }
    }

//    //声明接口对象
//    public INotify listener;
//
//    /**
//     * 定义接口及方法
//     */
//    public interface INotify {
//        void notifyEvent(boolean msg);
//    }
//
//    /**
//     * 注册回调接口的方法，供外部调用
//     *
//     * @param event
//     */
//    public void setOnINotifyListener(INotify event) {
//        listener = event;
//    }
}
