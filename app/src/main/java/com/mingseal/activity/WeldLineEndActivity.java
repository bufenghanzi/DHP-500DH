/**
 *
 */
package com.mingseal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mingseal.application.UserApplication;
import com.mingseal.communicate.SocketInputThread;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.dao.WeldLineEndDao;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.dhp_500dh.R;
import com.mingseal.listener.MaxMinEditWatcher;
import com.mingseal.listener.MaxMinFocusChangeListener;
import com.mingseal.listener.MyPopWindowClickListener;
import com.mingseal.ui.PopupListView;
import com.mingseal.ui.PopupListView.OnClickPositionChanged;
import com.mingseal.ui.PopupListView.OnZoomInChanged;
import com.mingseal.ui.PopupView;
import com.mingseal.utils.L;
import com.mingseal.utils.SharePreferenceUtils;
import com.mingseal.utils.ToastUtil;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.mingseal.data.param.PointConfigParam.GlueLineEnd;

/**
 * @author wangjian
 */
public class WeldLineEndActivity extends AutoLayoutActivity implements OnClickListener {

    private final static String TAG = "WeldLineEndActivity";
    /**
     * 标题栏的标题
     */
    private TextView tv_title;
    /**
     * 返回上级菜单
     */
    private RelativeLayout rl_back;

    /**
     * 保存方案按钮
     */
    private RelativeLayout rl_save;
    /**
     * 完成按钮
     */
    private RelativeLayout rl_complete;
    private Intent intent;
    private Point point;// 从taskActivity中传值传过来的point
    private int mFlag;// 0代表增加数据，1代表更新数据
    private int mType;// 1表示要更新数据

    private WeldLineEndDao weldEndDao;
    private List<PointWeldLineEndParam> weldEndLists;
    private PointWeldLineEndParam weldEnd;
    private int param_id = 1;// 选取的是几号方案

    /**
     * @Fields stopPrevInt: 停锡前延时的int值
     */
    private int stopTimePrevInt = 0;

    /**
     * @Fields upHeightInt: 抬起高度的int值
     */
    private int upHeightInt = 0;

    /**
     * @Fields isNull: 判断编辑输入框是否为空,false表示为空,true表示不为空
     */
    private boolean flag = false;// 可以与用户交互，初始化完成标志
    /* =================== begin =================== */
    private HashMap<Integer, PointWeldLineEndParam> update_id;// 修改的方案号集合
    private int defaultNum = 1;// 默认号
    ArrayList<PopupView> popupViews;
    private TextView mMorenTextView;
    PopupListView popupListView;
    int p = 0;
    View extendView;

    private boolean isOk;
    private boolean isExist = false;// 是否存在
    private boolean firstExist = false;// 是否存在
    /**
     * 当前任务号
     */
    private int currentTaskNum;
    private int currentClickNum;// 当前点击的序号
    private int mIndex;// 对应方案号
    /**
     * @Fields et_lineend_stopPrev: 停胶前延时
     */
    private EditText et_lineend_stopGlueTimePrev;
    /**
     * @Fields et_lineend_stop: 停胶后延时
     */
    private EditText et_upHeight;

    /**
     * 是否暂停
     */
    private ToggleButton switch_isPause;
    private RelativeLayout rl_moren;

    private TextView tv_stopGlueTimePrev;
    private TextView extend_ms;
    private TextView tv_stopGlueTime_ms;
    private TextView tv_upHeight;
    private TextView extend_mm;
    private TextView extend_mm2;
    private TextView extend_isPause;
    private TextView extend_default;
    private TextView extend_save;
    private TextView mFanganliebiao;
    private RevHandler handler;
    private UserApplication userApplication;
    /* =================== end =================== */
    private ViewStub stub_glue;
    private int Activity_Init_View = 4;
    private ImageView iv_loading;
    private String taskname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_glue_line_end);
        userApplication = (UserApplication) getApplication();
        handler = new RevHandler();
        update_id = new HashMap<>();
        intent = getIntent();
        point = intent
                .getParcelableExtra(MyPopWindowClickListener.POPWINDOW_KEY);
        mFlag = intent.getIntExtra(MyPopWindowClickListener.FLAG_KEY, 0);
        mType = intent.getIntExtra(MyPopWindowClickListener.TYPE_KEY, 0);
        taskname=intent.getStringExtra("taskname");
        defaultNum = SharePreferenceUtils.getParamNumberFromPref(
                WeldLineEndActivity.this,
                SettingParam.DefaultNum.ParamGlueLineEndNumber);

        weldEndDao = new WeldLineEndDao(this);
        weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
        if (weldEndLists == null || weldEndLists.isEmpty()) {
            weldEnd = new PointWeldLineEndParam();
            weldEnd.set_id(param_id);
            weldEndDao.insertWeldLineEnd(weldEnd,taskname);
        }
        weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
        popupViews = new ArrayList<>();
        initPicker();

    }

    /**
     * @param weldLineEndParam
     * @Title UpdateInfos
     * @Description 更新extendView数据（保存的数据）
     * @author wj
     */
    private void UpdateInfos(PointWeldLineEndParam weldLineEndParam) {
        if (weldLineEndParam == null) {
            et_lineend_stopGlueTimePrev.setText("");
            et_upHeight.setText("");
        } else {
            et_lineend_stopGlueTimePrev.setText(weldLineEndParam
                    .getStopSnTime() + "");
            et_upHeight.setText(weldLineEndParam.getUpHeight()
                    + "");
            switch_isPause.setChecked(weldLineEndParam.isPause());
        }
    }

    /**
     * 加载页面组件并设置NumberPicker的最大最小值
     */
    private void initPicker() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(
                R.string.activity_glue_line_end));
        mMorenTextView = (TextView) findViewById(R.id.morenfangan);
        mFanganliebiao = (TextView) findViewById(R.id.fanganliebiao);
        mFanganliebiao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(50));
        mMorenTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        mMorenTextView.setText("当前默认方案号(" + defaultNum + ")");
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        iv_loading.setVisibility(View.VISIBLE);
        stub_glue = (ViewStub) findViewById(R.id.stub_glue_line_end);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = Activity_Init_View;
                handler.sendMessage(msg);
            }
        }, 50);// 延迟1毫秒,然后加载
    }

    protected void setTitleInfos(List<PointWeldLineEndParam> glueEndLists,
                                 View view, int p) {
//                title_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_et_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_third_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_breakGlueLen.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_et_breakGlueLen.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_second_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_four_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_drawDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_et_drawDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_third_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_five_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_drawSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_et_drawSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_mm_s.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                activity_six_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_switch_isPause.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//                title_et_switch_isPause.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
//				/*=====================  end =====================*/
//                title_stopGlueTimePrev.setText(getResources().getString(
//                        R.string.activity_glue_stopGlueTimePrev)
//                        + " ");
//                activity_ms.setText(getResources().getString(
//                        R.string.activity_ms));
//                activity_fenghao.setText(getResources().getString(
//                        R.string.activity_fenghao)
//                        + " ");
//                title_stopGlueTime.setText(getResources().getString(
//                        R.string.activity_glue_outGlueTime)
//                        + " ");
//                activity_second_ms.setText(getResources().getString(
//                        R.string.activity_ms));
//                activity_second_fenghao.setText(getResources().getString(
//                        R.string.activity_fenghao)
//                        + " ");
//                title_upHeight.setText(getResources().getString(
//                        R.string.activity_glue_alone_upHeight)
//                        + " ");
//                activity_mm.setText(getResources().getString(
//                        R.string.activity_mm));
//                activity_third_fenghao.setText(getResources().getString(
//                        R.string.activity_fenghao)
//                        + " ");
//                title_breakGlueLen.setText(getResources()
//                        .getString(R.string.activity_glue_breakGlueLen)
//                        + " ");
//                activity_second_mm.setText(getResources().getString(R.string.activity_mm));
//                activity_third_mm.setText(getResources().getString(R.string.activity_mm));
//                activity_four_fenghao.setText(getResources().getString(
//                        R.string.activity_fenghao)
//                        + " ");
//                title_drawDistance.setText(getResources().getString(
//                        R.string.activity_glue_drawDistance)
//                        + " ");
//                activity_five_fenghao.setText(getResources().getString(
//                        R.string.activity_fenghao)
//                        + " ");
//                title_drawSpeed.setText(getResources().getString(
//                        R.string.activity_glue_drawSpeed)
//                        + " ");
//                activity_mm_s.setText(getResources().getString(R.string.activity_mm_s));
//                activity_six_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
//                title_switch_isPause.setText(getResources().getString(R.string.activity_glue_alone_isPause));
//
//                title_et_stopGlueTimePrev.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_stopGlueTimePrev.getPaint()
//                        .setAntiAlias(true); // 抗锯齿
//                title_et_stopGlueTime.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_stopGlueTime.getPaint().setAntiAlias(true); // 抗锯齿
//                title_et_upHeight.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_upHeight.getPaint().setAntiAlias(true); // 抗锯齿
//                title_et_breakGlueLen.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_breakGlueLen.getPaint().setAntiAlias(true); // 抗锯齿
//                title_et_drawDistance.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_drawDistance.getPaint().setAntiAlias(true); // 抗锯齿
//                title_et_drawSpeed.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_drawSpeed.getPaint().setAntiAlias(true); // 抗锯齿
//                title_et_switch_isPause.getPaint().setFlags(
//                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
//                title_et_switch_isPause.getPaint().setAntiAlias(true); // 抗锯齿
//
//                title_et_stopGlueTimePrev.setText(pointWeldLineEndParam
//                        .getStopGlueTimePrev() + "");
//                title_et_stopGlueTime.setText(pointWeldLineEndParam
//                        .getStopGlueTime() + "");
//                title_et_upHeight.setText(pointWeldLineEndParam.getUpHeight()
//                        + "");
//                title_et_breakGlueLen.setText(pointWeldLineEndParam.getBreakGlueLen() + "");
//                title_et_drawDistance.setText(pointWeldLineEndParam.getDrawDistance() + "");
//                title_et_drawSpeed.setText(pointWeldLineEndParam.getDrawSpeed() + "");
//
//                if (pointWeldLineEndParam.isPause()) {
//                    title_et_switch_isPause.setText("是");
//                } else {
//                    title_et_switch_isPause.setText("否");
//                }
//            }
//        }
    }

    /**
     * @Title SetDateAndRefreshUI
     * @Description 打开extendview的时候设置界面内容，显示最新的方案数据而不是没有保存的数据,没有得到保存的方案
     * @author wj
     */
    protected void SetDateAndRefreshUI() {
        weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
            list.add(pointWeldLineEndParam.get_id());
        }
        L.d("存放主键id的集合---->" + list);
        L.d("当前选择的方案号---->" + currentTaskNum);
        L.d("list是否存在------------》"
                + list.contains(currentTaskNum));
        if (list.contains(currentTaskNum)) {
            // 已经保存在数据库中的数据
            for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                if (currentTaskNum == pointWeldLineEndParam.get_id()) {
                    View extendView = popupListView.getItemViews()
                            .get(currentClickNum).getExtendView();
                    initView(extendView);
                    UpdateInfos(pointWeldLineEndParam);
                }
            }
        } else {
            // 对所有数据进行置空
            View allextendView = popupListView.getItemViews()
                    .get(currentClickNum).getExtendView();
            initView(allextendView);
            UpdateInfos(null);
        }
    }

    protected void save() {
        View extendView = popupListView.getItemViews().get(currentClickNum)
                .getExtendView();
        weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
            list.add(pointWeldLineEndParam.get_id());
        }
        // 判空
        isOk = isEditClean(extendView);
        if (isOk) {

            PointWeldLineEndParam upLineEndParam = getLineEnd(extendView);
            if (weldEndLists.contains(upLineEndParam)) {
                // 默认已经存在的方案但是不能创建方案只能改变默认方案号
                if (list.contains(currentTaskNum)) {
                    isExist = true;
                }
                // 保存的方案已经存在但不是当前编辑的方案
                if (currentTaskNum != weldEndLists.get(
                        weldEndLists.indexOf(upLineEndParam)).get_id()) {
                    ToastUtil.displayPromptInfo(WeldLineEndActivity.this,
                            getResources()
                                    .getString(R.string.task_is_exist_yes));
                }
            } else {
                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                    if (currentTaskNum == pointWeldLineEndParam.get_id()) {// 说明之前插入过
                        flag = true;
                    }
                }
                if (flag) {
                    // 更新数据
                    int rowid = weldEndDao.upDateWeldLineEnd(upLineEndParam,taskname);
                    // System.out.println("影响的行数"+rowid);
                    update_id.put(upLineEndParam.get_id(), upLineEndParam);
                    // mPMap.map.put(upglueAlone.get_id(), upglueAlone);
                    L.d("修改的方案号为：" + upLineEndParam.get_id());
                    // System.out.println(glueAloneDao.getPointGlueAloneParamById(currentTaskNum).toString());
                } else {
                    // 插入一条数据
                    long rowid = weldEndDao.insertWeldLineEnd(upLineEndParam,taskname);
                    firstExist = true;
                    weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
                    L.d(TAG, "保存之后新方案-->" + weldEndLists.toString());
                    ToastUtil.displayPromptInfo(WeldLineEndActivity.this,
                            getResources().getString(R.string.save_success));
                    list.clear();
                    for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                        list.add(pointWeldLineEndParam.get_id());
                    }
                    popupListView.setSelectedEnable(list);
                }
            }
            if (popupListView.isItemZoomIn()) {
                popupListView.zoomOut();
            }
            // 更新title
            refreshTitle();
            flag = false;
        } else {
            ToastUtil.displayPromptInfo(this,
                    getResources().getString(R.string.data_is_null));
        }

    }

    /**
     * @Title refreshTitle
     * @Description 按下保存之后刷新title
     * @author wj
     */
    private void refreshTitle() {
        weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
        // popupListView->pupupview->title
        for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {

            if (currentTaskNum == pointWeldLineEndParam.get_id()) {
                // 需要设置两个view，因为view内容相同但是parent不同
                View titleViewItem = popupListView.getItemViews()
                        .get(currentClickNum).getPopupView();
                View titleViewExtend = popupListView.getItemViews()
                        .get(currentClickNum).getExtendPopupView();
                setTitleInfos(weldEndLists, titleViewItem, currentTaskNum);
                setTitleInfos(weldEndLists, titleViewExtend, currentTaskNum);

            }
        }
    }

    /**
     * @param extendView
     * @return false表示为空, true表示都有数据
     * @Title isEditClean
     * @Description 判断输入框是否为空
     * @author wj
     */
    private boolean isEditClean(View extendView) {
        et_lineend_stopGlueTimePrev = (EditText) extendView
                .findViewById(R.id.et_lineend_stopGlueTimePrev);
        et_upHeight = (EditText) extendView
                .findViewById(R.id.et_upHeight);

        if ("".equals(et_lineend_stopGlueTimePrev.getText().toString())) {
            return false;
        } else if ("".equals(et_upHeight.getText().toString())) {
            return false;
        }
        return true;
    }

    /**
     * @param extendView
     * @Title initView
     * @Description 初始化当前extendView视图
     * @author wj
     */
    protected void initView(View extendView) {
        et_lineend_stopGlueTimePrev = (EditText) extendView
                .findViewById(R.id.et_lineend_stopGlueTimePrev);
        et_upHeight = (EditText) extendView
                .findViewById(R.id.et_upHeight);
        switch_isPause = (ToggleButton) extendView.findViewById(R.id.switch_isPause);

        tv_stopGlueTimePrev = (TextView) extendView.findViewById(R.id.tv_stopGlueTimePrev);
        extend_ms = (TextView) extendView.findViewById(R.id.extend_ms);
        tv_upHeight = (TextView) extendView.findViewById(R.id.tv_upHeight);
        tv_stopGlueTime_ms = (TextView) extendView.findViewById(R.id.tv_stopGlueTime_ms);
        tv_upHeight = (TextView) extendView.findViewById(R.id.tv_upHeight);
        extend_mm = (TextView) extendView.findViewById(R.id.extend_mm);
        extend_mm2 = (TextView) extendView.findViewById(R.id.extend_mm2);
        extend_isPause = (TextView) extendView.findViewById(R.id.extend_isPause);
        extend_default = (TextView) extendView.findViewById(R.id.extend_default);
        extend_save = (TextView) extendView.findViewById(R.id.extend_save);
		/*===================== begin =====================*/
        et_lineend_stopGlueTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_stopGlueTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_stopGlueTime_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm2.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_isPause.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_default.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		/*=====================  end =====================*/
    }

    /**
     * 将页面上的数据保存到PointGlueLineEndParam对象中
     *
     * @param extendView
     * @return PointGlueLineEndParam
     */
    private PointWeldLineEndParam getLineEnd(View extendView) {
        weldEnd = new PointWeldLineEndParam();
        et_lineend_stopGlueTimePrev = (EditText) extendView
                .findViewById(R.id.et_lineend_stopGlueTimePrev);
        et_upHeight = (EditText) extendView
                .findViewById(R.id.et_upHeight);
        switch_isPause = (ToggleButton) extendView
                .findViewById(R.id.switch_isPause);
        try {
            stopTimePrevInt = Integer.parseInt(et_lineend_stopGlueTimePrev
                    .getText().toString());
        } catch (NumberFormatException e) {
            stopTimePrevInt = 0;
        }
        try {
            upHeightInt = Integer.parseInt(et_upHeight.getText()
                    .toString());
        } catch (NumberFormatException e) {
            upHeightInt = 0;
        }

        weldEnd.setUpHeight(upHeightInt);
        weldEnd.setStopSnTime(stopTimePrevInt);
        weldEnd.setPause(switch_isPause.isChecked());

        weldEnd.set_id(currentTaskNum);
        return weldEnd;
    }

    @Override
    public void onBackPressed() {
        // 不想保存只想回退，不保存数据
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
        } else {
            complete();
            super.onBackPressed();
            overridePendingTransition(R.anim.in_from_left,
                    R.anim.out_from_right);
        }
    }

    private void complete() {
        ArrayList<? extends PopupView> itemPopuViews = popupListView
                .getItemViews();
        for (PopupView popupView : itemPopuViews) {
            ImageView iv_selected = (ImageView) popupView.getPopupView()
                    .findViewById(R.id.iv_selected);
            if (iv_selected.getVisibility() == View.VISIBLE) {
                mIndex = itemPopuViews.indexOf(popupView) + 1;
            }
        }
        L.d("返回的方案号为================》" + mIndex);
        point.setPointParam(weldEndDao.getPointWeldLineEndParamByID(mIndex,taskname));
        L.d("返回的Point为================》" + point);

        List<Map<Integer, PointWeldLineEndParam>> list = new ArrayList<Map<Integer, PointWeldLineEndParam>>();
        list.add(update_id);
        Log.i(TAG, point.toString());
        Bundle extras = new Bundle();
        extras.putParcelable(MyPopWindowClickListener.POPWINDOW_KEY, point);
        extras.putInt(MyPopWindowClickListener.FLAG_KEY, mFlag);
        // 须定义一个list用于在budnle中传递需要传递的ArrayList<Object>,这个是必须要的
        ArrayList bundlelist = new ArrayList();
        bundlelist.add(list);
        extras.putParcelableArrayList(MyPopWindowClickListener.TYPE_UPDATE,
                bundlelist);
        intent.putExtras(extras);

        setResult(TaskActivity.resultCode, intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:// 返回按钮的响应事件
                if (popupListView.isItemZoomIn()) {
                    popupListView.zoomOut();
                } else {
                    complete();
                    super.onBackPressed();
                    overridePendingTransition(R.anim.in_from_left,
                            R.anim.out_from_right);
                }
                break;
            default:
                break;
        }
    }
    private class RevHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what== SocketInputThread.SocketError){
                //wifi中断
                System.out.println("wifi连接断开。。");
                SocketThreadManager.releaseInstance();
                System.out.println("单例被释放了-----------------------------");
                //设置全局变量，跟新ui
                userApplication.setWifiConnecting(false);
//				WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
                ToastUtil.displayPromptInfo(WeldLineEndActivity.this,"wifi连接断开。。");
            }else if (msg.what == Activity_Init_View) {
                View activity_glue_popuplistview = stub_glue.inflate();
                popupListView = (PopupListView) activity_glue_popuplistview.findViewById(R.id.popupListView);
                popupListView.init(null);
                CreatePopupViews();
            }
        }
    }

    private void CreatePopupViews() {
        // 初始化创建10个popupView
        for (int i = 0; i < 10; i++) {
            p = i + 1;
            PopupView popupView = new PopupView(this, R.layout.popup_view_item_glue_end) {

                @Override
                public void setViewsElements(View view) {
                    weldEndLists = weldEndDao.findAllWeldLineEndParams(taskname);
                    ImageView title_num = (ImageView) view
                            .findViewById(R.id.title_num);
                    if (p == 1) {// 方案列表第一位对应一号方案
                        title_num.setImageResource(R.drawable.green1);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 2) {
                        title_num.setImageResource(R.drawable.green2);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 3) {
                        title_num.setImageResource(R.drawable.green3);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 4) {
                        title_num.setImageResource(R.drawable.green4);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 5) {
                        title_num.setImageResource(R.drawable.green5);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 6) {
                        title_num.setImageResource(R.drawable.green6);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 7) {
                        title_num.setImageResource(R.drawable.green7);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 8) {
                        title_num.setImageResource(R.drawable.green8);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 9) {
                        title_num.setImageResource(R.drawable.green9);
//                        setTitleInfos(weldEndLists, view, p);
                    } else if (p == 10) {
                        title_num.setImageResource(R.drawable.green10);
//                        setTitleInfos(weldEndLists, view, p);
                    }
                }

                @Override
                public View setExtendView(View view) {
                    if (view == null) {
                        extendView = LayoutInflater.from(
                                getApplicationContext()).inflate(
                                R.layout.glue_end_extend_view, null);
                        int size = weldEndLists.size();
                        while (size > 0) {
                            size--;
                            if (p == 1) {// 方案列表第一位对应一号方案
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 2) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 3) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 4) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 5) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 6) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 7) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 8) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 9) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            } else if (p == 10) {
                                initView(extendView);
                                for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
                                    if (p == pointWeldLineEndParam.get_id()) {
                                        UpdateInfos(pointWeldLineEndParam);
                                    }
                                }
                            }
                        }
                        extendView.setBackgroundColor(Color.WHITE);
                    } else {
                        extendView = view;
                    }
                    return extendView;
                }

                @Override
                public void initViewAndListener(View extendView) {

                    et_lineend_stopGlueTimePrev = (EditText) extendView
                            .findViewById(R.id.et_lineend_stopGlueTimePrev);
                    et_upHeight = (EditText) extendView
                            .findViewById(R.id.et_upHeight);
                    switch_isPause = (ToggleButton) extendView
                            .findViewById(R.id.switch_isPause);

                    // 设置停胶前延时的最大最小值
                    et_lineend_stopGlueTimePrev
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    GlueLineEnd.StopGlueTimePrevMax,
                                    GlueLineEnd.GlueLineEndMin,
                                    et_lineend_stopGlueTimePrev));
                    et_lineend_stopGlueTimePrev
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    GlueLineEnd.StopGlueTimePrevMax,
                                    GlueLineEnd.GlueLineEndMin,
                                    et_lineend_stopGlueTimePrev));
                    et_lineend_stopGlueTimePrev.setSelectAllOnFocus(true);

                    // 设置停胶后延时的最大最小值
                    et_upHeight
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    RobotParam.INSTANCE.GetZJourney(),
                                    GlueLineEnd.GlueLineEndMin,
                                    et_upHeight));
                    et_upHeight
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    RobotParam.INSTANCE.GetZJourney(),
                                    GlueLineEnd.GlueLineEndMin,
                                    et_upHeight));
                    et_upHeight.setSelectAllOnFocus(true);


                    rl_moren = (RelativeLayout) extendView
                            .findViewById(R.id.rl_moren);
                    rl_save = (RelativeLayout) extendView
                            .findViewById(R.id.rl_save);// 保存按钮
                    rl_moren.setOnClickListener(this);
                    rl_save.setOnClickListener(this);
                }

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.rl_moren:// 设为默认
                            // 判断界面
                            save();
                            if ((isOk && isExist) || firstExist) {// 不为空且已经存在或者不存在且插入新的
                                // 刷新ui
                                mMorenTextView.setText("当前默认方案号(" + currentTaskNum
                                        + ")");
                                // 默认号存到sp
                                SharePreferenceUtils
                                        .saveParamNumberToPref(
                                                WeldLineEndActivity.this,
                                                SettingParam.DefaultNum.ParamGlueLineEndNumber,
                                                currentTaskNum);
                            }
                            isExist = false;
                            firstExist = false;
                            // 更新数据
                            break;
                        case R.id.rl_save:// 保存
                            save();
                            // 数据库保存数据
                            break;

                        default:
                            break;
                    }
                }
            };
            popupViews.add(popupView);
        }
        popupListView.setItemViews(popupViews);
        if (mType != 1) {
            popupListView.setPosition(defaultNum - 1);// 第一次默认选中第一个item，后面根据方案号(新建点)
        } else {
            // 显示point的参数方案
            // PointGlueAloneParam glueAloneParam= (PointGlueAloneParam)
            // point.getPointParam();
            // System.out.println("传进来的方案号为----------》"+glueAloneParam.get_id());
            popupListView.setPosition(point.getPointParam().get_id() - 1);
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldLineEndParam pointWeldLineEndParam : weldEndLists) {
            list.add(pointWeldLineEndParam.get_id());
        }
        popupListView.setSelectedEnable(list);
        popupListView.setOnClickPositionChanged(new OnClickPositionChanged() {
            @Override
            public void getCurrentPositon(int position) {
                currentTaskNum = position + 1;
                currentClickNum = position;
            }
        });
        popupListView.setOnZoomInListener(new OnZoomInChanged() {

            @Override
            public void getZoomState(Boolean isZoomIn) {
                if (isZoomIn) {
                    // 设置界面
                    SetDateAndRefreshUI();
                }
            }
        });
        rl_back.setOnClickListener(this);
        iv_loading.setVisibility(View.INVISIBLE);

    }

}
