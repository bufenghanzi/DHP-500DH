package com.mingseal.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mingseal.activity.TaskActivity;
import com.mingseal.adapter.TaskMainBaseAdapter;
import com.mingseal.data.dao.WeldBlowDao;
import com.mingseal.data.dao.WeldLineEndDao;
import com.mingseal.data.dao.WeldLineStartDao;
import com.mingseal.data.dao.WeldWorkDao;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointType;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;
import com.mingseal.dhp_500dh.R;
import com.mingseal.utils.L;
import com.mingseal.utils.ToastUtil;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * @author 商炎炳
 */
public class MyPopWindowClickListener implements OnClickListener {
    public static String POPWINDOW_KEY = "com.mingseal.listener.popwindow.key";
    /**
     * 0代表要增加数据，1代表要更新数据
     */
    public final static String FLAG_KEY = "com.mingseal.listener.flag.key";
    /**
     * 获取传过来的type种类(0代表是从MyPopWindowClickListner传过去的,1代表是从TaskActivity中的方案传过去的)
     */
    public final static String TYPE_KEY = "com.mingseal.listener.type.key";

    /**
     * 代表发送过去的key
     */
    public final static String TYPE_UPDATE = "com.mingseal.listener.update.key";
    private PopupWindow popupWindow;
    private TaskActivity mParent;
    private final static String TAG = "MyPopWindowClickListener";
    private List<Point> points;
    private Point point;
    private Point pointBase;
    private Point pointClear;
    private Point pointArc;
    private Intent intent;
    private Bundle extras;
    /**
     * @Fields mFlag: 0代表从TaskActivity传值，1代表从TaskMainBaseAdapter中传值
     */
    private int mFlag = 0;// 0代表从TaskActivity传值，1代表从TaskMainBaseAdapter中传值
    private int selectRadio = 0;
    private TaskMainBaseAdapter mAdapter;
    private String taskname;
    private WeldWorkDao weldWorkDao;
    private List<PointWeldWorkParam> weldWorkLists;
    private PointWeldWorkParam weldWork;
    //声明接口对象
    public InterfacePoint mInterfacePoint;
    private WeldLineStartDao weldStartDao;
    private List<PointWeldLineStartParam> weldStartLists;
    private PointWeldLineStartParam weldStart;
    private WeldLineEndDao weldEndDao;
    private List<PointWeldLineEndParam> weldEndLists;
    private PointWeldLineEndParam weldEnd;
    private WeldBlowDao weldBlowDao;
    private List<PointWeldBlowParam> weldBlowLists;
    private PointWeldBlowParam weldBlow;

    /**
     * 定义接口及方法
     */
    public interface InterfacePoint {
        void listenEvent(Point point,int flag);
    }

    /**
     * 注册回调接口的方法，供外部调用
     *
     * @param event
     */
    public void setOnINotifyServiceListener(InterfacePoint event) {
        mInterfacePoint = event;
    }
    public MyPopWindowClickListener(TaskActivity mParent) {
        this.mParent = mParent;
        View menu = initMenuView(mParent);
        popupWindow = new PopupWindow(menu);
        popupWindow.setWidth(AutoUtils.getPercentWidthSize(200));// 弹出框的宽高
        popupWindow.setHeight(AutoUtils.getPercentHeightSize(370));
    }

    /**
     * 用于activity向自定义的onClickListener传值
     *  @param _points
     * @param _selectRadioID
     * @param _flag          0代表从TaskActivity中传值
     * @param mAdapter
     * @param taskName
     */
    public void setPointLists(List<Point> _points, int _selectRadioID, int _flag, TaskMainBaseAdapter mAdapter, String taskName) {
        this.points = _points;
        this.point = getPointLast(_points, _selectRadioID);
        this.mFlag = _flag;
        this.mAdapter = mAdapter;
        this.taskname=taskName;
    }

    /**
     * TaskMainBaseAdapter中传值，更换点类型
     *  @param pointLists
     * @param _point
     * @param _flag               1代表从TaskMainBaseAdapter中传值
     * @param taskMainBaseAdapter
     * @param taskname
     */
    public void setPoint(List<Point> pointLists, Point _point, int _flag, TaskMainBaseAdapter taskMainBaseAdapter, String taskname) {
        this.points = pointLists;
        this.point = _point;
        this.mFlag = _flag;
        this.mAdapter = taskMainBaseAdapter;
        this.taskname=taskname;
    }

    /**
     * 取得List列表的当前选中的数据，如果没有数据，则添加一个（0,0,0,0,null）的数据
     *
     * @param points
     * @return
     */
    private Point getPointLast(List<Point> points, int _selectRadioID) {
        if (points.size() != 0 && points != null) {
            // int size = points.size() - 1;
            point = points.get(_selectRadioID);
        } else {
            point = new Point(PointType.POINT_WELD_BASE);
        }
        return point;
    }

    /**
     * 设置具体的监听
     *
     * @param mParent
     * @return
     */
    private View initMenuView(Activity mParent) {
        View menuView = mParent.getLayoutInflater().inflate(R.layout.activity_task_main_button_popwindow, null);
        Button but_jieshu = (Button) menuView.findViewById(R.id.but_jieshu);
        Button but_qishi = (Button) menuView.findViewById(R.id.but_qishi);
        Button but_duli = (Button) menuView.findViewById(R.id.but_duli);
//        Button but_zhongjian = (Button) menuView.findViewById(R.id.but_zhongjian);
        Button but_jizhun = (Button) menuView.findViewById(R.id.but_jizhun);
        Button but_chuixi = (Button) menuView.findViewById(R.id.but_chuixi);
        but_jieshu.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        but_qishi.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        but_duli.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//        but_zhongjian.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        but_chuixi.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        but_jizhun.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));

        menuView.findViewById(R.id.but_jieshu).setOnClickListener(this);
        menuView.findViewById(R.id.but_qishi).setOnClickListener(this);
        menuView.findViewById(R.id.but_duli).setOnClickListener(this);
//        menuView.findViewById(R.id.but_zhongjian).setOnClickListener(this);
        menuView.findViewById(R.id.but_chuixi).setOnClickListener(this);
        menuView.findViewById(R.id.but_jizhun).setOnClickListener(this);
        return menuView;
    }

    /**
     * 跳转页面,并将Point类和mFlag标志一起传到跳转的Activity中去
     *
     * @param _intent
     */
    private void saveToActivity(Intent _intent) {
        extras = new Bundle();
        extras.putParcelable(POPWINDOW_KEY, point);
        extras.putInt(FLAG_KEY, mFlag);
        extras.putInt(TYPE_KEY, 0);
        extras.putString("taskname",taskname);
        _intent.putExtras(extras);

        mParent.startActivityForResult(_intent, TaskActivity.requestCode);
        mParent.overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);

        disPopWindow(popupWindow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_jieshu:// 结束点
                weldEndDao = new WeldLineEndDao(mParent);
                weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
                if (weldEndLists == null || weldEndLists.isEmpty()) {
                    weldEnd = new PointWeldLineEndParam();
                    for (int i=1;i<=10;i++){
                        // 插入主键id
                        weldEnd.set_id(i);
                        weldEndDao.insertWeldLineEnd(weldEnd, taskname);
                    }
                }
                CreateMaterialDialog(1);
//                intent = new Intent(mParent, WeldLineEndActivity.class);
//                saveToActivity(intent);

                break;
            case R.id.but_qishi:// 起始点
                weldStartDao = new WeldLineStartDao(mParent);
                weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
                if (weldStartLists == null || weldStartLists.isEmpty()) {
                    weldStart = new PointWeldLineStartParam();
                    for (int i=1;i<=10;i++){
                        // 插入主键id
                        weldStart.set_id(i);
                        weldStartDao.insertWeldLineStart(weldStart,taskname);
                    }
                }
                CreateMaterialDialog(2);
//                intent = new Intent(mParent, WeldLineStartActivity.class);
//                saveToActivity(intent);

                break;
            case R.id.but_duli:// 作业点

                weldWorkDao = new WeldWorkDao(mParent);
                weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
                if (weldWorkLists == null || weldWorkLists.isEmpty()) {
                    weldWork = new PointWeldWorkParam();
                    for (int i=1;i<=10;i++){
                        // 插入主键id
                        weldWork.set_id(i);
                        weldWorkDao.insertWeldWork(weldWork,taskname);
                    }
                }
                CreateMaterialDialog(3);
//                intent = new Intent(mParent, WeldWorkActivity.class);
//                saveToActivity(intent);

                break;
            case R.id.but_jizhun:// 基准点
                if (points.size() != 0) {
                    if (!points.get(0).getPointParam().getPointType().equals(PointType.POINT_WELD_BASE)) {
                        pointBase = new Point(PointType.POINT_WELD_BASE);
                        if (mFlag == 0) {
                            selectRadio = 0;
                            points.add(selectRadio, pointBase);

                            mParent.setSelectRadioID(selectRadio);// Activity需要设置选中id
                            mAdapter.setSelectID(selectRadio);// 选中位置

                        } else if (mFlag == 1) {
                            selectRadio = mParent.getSelectRadioID();
                            if (selectRadio == 0) {
                                points.remove(selectRadio);
                                points.add(selectRadio, pointBase);
                            } else {
                                ToastUtil.displayPromptInfo(mParent, "基准点只允许插在开始位置");
                            }
                        }

                        mAdapter.setData(points);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.displayPromptInfo(mParent, "只允许有一个基准点");
                    }
                } else {
                    pointBase = new Point(PointType.POINT_WELD_BASE);
                    selectRadio = 0;
                    points.add(selectRadio, pointBase);

                    mParent.setSelectRadioID(selectRadio);// Activity需要设置选中id
                    mAdapter.setSelectID(selectRadio);// 选中位置
                    mAdapter.setData(points);
                    mAdapter.notifyDataSetChanged();
                }

                disPopWindow(popupWindow);
                break;
//            case R.id.but_zhongjian:// 中间点
//
//                intent = new Intent(mParent, WeldLineMidActivity.class);
//                saveToActivity(intent);
//
//                break;
            case R.id.but_chuixi:// 吹锡点
                weldBlowDao = new WeldBlowDao(mParent);
                weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
                if (weldBlowLists == null || weldBlowLists.isEmpty()) {
                    weldBlow = new PointWeldBlowParam();
                    for (int i=1;i<=10;i++){
                        // 插入主键id
                        weldBlow.set_id(i);
                        weldBlowDao.insertWeldOutput(weldBlow, taskname);
                    }
                    // 插入主键id
                }
                CreateMaterialDialog(4);
//                intent = new Intent(mParent, WeldBlowActivity.class);
//                saveToActivity(intent);

                break;

        }

    }

    /**
     * 弹框选择方案
     * @param i
     */
    private void CreateMaterialDialog(final int i) {
        new MaterialDialog.Builder(mParent)
                .title(R.string.title)
                .items(R.array.items)
                .alwaysCallSingleChoiceCallback()
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Point point=new Point();
                        if (i==1){
                            point.setPointParam(weldEndDao.getPointWeldLineEndParamByID(which+1,taskname));
                        }else if (i==2){
                            point.setPointParam(weldStartDao.getPointWeldLineStartParamByID(which+1,taskname));
                        }else if (i==3){
                            point.setPointParam(weldWorkDao.getPointWeldWorkParamById(which+1,taskname));
                        }else if (i==4){
                            point.setPointParam(weldBlowDao.getOutPutPointByID(which+1,taskname));
                        }
                        L.d("POINT=="+point.toString());
                        mInterfacePoint.listenEvent(point,mFlag);
                        return true;
                    }
                })
                .widgetColor(mParent.getResources().getColor(R.color.button_pressed))
                .show();
    }

    /**
     * 隐藏弹出框
     *
     * @param popupWindow
     */
    private void disPopWindow(PopupWindow popupWindow) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * @return View
     */
    public PopupWindow getMenu()
    {
        return popupWindow;
    }


}
