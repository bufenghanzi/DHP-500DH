/**
 *
 */
package com.mingseal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kyleduo.switchbutton.SwitchButton;
import com.mingseal.application.UserApplication;
import com.mingseal.communicate.SocketInputThread;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.dao.WeldLineStartDao;
import com.mingseal.data.param.PointConfigParam;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.dhp_500dh.R;
import com.mingseal.listener.MaxMinEditWatcher;
import com.mingseal.listener.MaxMinFocusChangeListener;
import com.mingseal.listener.MyPopWindowClickListener;
import com.mingseal.ui.PopupListView;
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

/**
 * @author wangjian
 */
public class WeldLineStartActivity extends AutoLayoutActivity implements OnClickListener {

    private final static String TAG = "WeldLineStartActivity";
    /**
     * 标题栏的标题
     */
    private TextView tv_title;

    /**
     * 线起始点Spinner
     */
    private Spinner lineStartSpinner;

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

    private WeldLineStartDao weldStartDao;
    private List<PointWeldLineStartParam> weldStartLists;// 保存的方案,用来维护从数据库中读出来的方案列表的编号
    private PointWeldLineStartParam weldStart;
    private boolean[] glueBoolean;
    private int param_id = 1;// / 选取的是几号方案
    /**
     * @Fields sendSnSpeed: 送锡速度
     */
    private int sendSnSpeed = 0;
    /**
     * @Fields preSendSnSum: 预送锡量
     */
    private float preSendSnSum = 0;
    /**
     * @Fields moveSpeedInt: 轨迹速度的int值
     */
    private int moveSpeedInt = 0;

    /**
     * @Fields stopGlueTimeInt: 停胶后延时的int值
     */
    private boolean flag = false;// 可以与用户交互，初始化完成标志
    /* =================== begin =================== */
    private HashMap<Integer, PointWeldLineStartParam> update_id;// 修改的方案号集合
    private int defaultNum = 1;// 默认号
    ArrayList<PopupView> popupViews;
    private TextView mMorenTextView;
    PopupListView popupListView;
    int p = 0;
    View extendView;

    /**
     * 滞后出胶时间
     */
    private EditText et_preSendSnSum;
    /**
     * 轨迹速度
     */
    private EditText et_preSendSnSpeed;
    private SwitchButton switch_isSn;

    private RelativeLayout rl_moren;
    /**
     * 点胶口
     */
    private ToggleButton[] isGluePort;
    private boolean isOk;
    private boolean isExist = false;// 是否存在
    private boolean firstExist = false;// 是否存在
    /**
     * 当前任务号
     */
    private int currentTaskNum;
    private int currentClickNum;// 当前点击的序号
    private int mIndex;// 对应方案号

    private TextView tv_outGlueTimePrev;
    private TextView extend_line_start_ms;
    private TextView tv_outGlueTime;
    private TextView tv_outGlueTime_ms;
    private TextView tv_moveSpeed;
    private TextView extend_line_start_mms;

    private TextView extend_default;
    private TextView extend_save;
    private TextView mFanganliebiao;
    private RevHandler handler;
    private UserApplication userApplication;
    /* =================== end =================== */
    private ViewStub stub_glue;
    private int Activity_Init_View = 2;
    private ImageView iv_loading;
    private EditText et_sendSnSpeed;
    private TextView tv_moveSpeed2;
    private TextView tv_movespeed_ms;
    private EditText et_preHeatTime;
    private TextView tv_preHeatTime;
    private TextView extend_preHeatTime_ms;
    private EditText et_moveSpeed;
    private int preSendSnSpeed;
    private int moveSpeed;
    private int preHeatTime;
    private ImageView iv_add;
    private ImageView iv_moren;
    private static final int DECIMAL_DIGITS = 1;//小数的位数
    private String taskname;
    private TextView extend_isSn;
    private TextView mTitle_sendSnSpeed;
    private TextView mTitle_et_linestart_sendSnSpeed;
    private TextView mActivity_mm_s;
    private TextView mActivity_fenghao;
    private TextView mTitle_preSendSnSum;
    private TextView mTitle_et_preSendSnSum;
    private TextView mActivity_mm;
    private TextView mActivity_second_fenghao;
    private TextView mTitle_preSendSnSpeed;
    private TextView mTitle_et_preSendSnSpeed;
    private TextView mActivity_sec_mm_s;
    private TextView mActivity_third_fenghao;
    private TextView mTitle_moveSpeed;
    private TextView mTitle_et_linestart_moveSpeed;
    private TextView mActivity_third_mm_s;
    private TextView mActivity_four_fenghao;
    private TextView mTitle_preHeatTime;
    private TextView mTitle_et_preHeatTime;
    private TextView mActivity_ms;
    private TextView mActivity_five_fenghao;
    private TextView mTitle_isSn;
    private TextView mTitle_et_isSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_glue_line_start);
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
                WeldLineStartActivity.this,
                SettingParam.DefaultNum.ParamGlueLineStartNumber);
        L.d(TAG, point.toString());

        weldStartDao = new WeldLineStartDao(WeldLineStartActivity.this);
        weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
        if (weldStartLists == null || weldStartLists.isEmpty()) {
            weldStart = new PointWeldLineStartParam();
            weldStart.set_id(param_id);
            weldStartDao.insertWeldLineStart(weldStart,taskname);
        }
        weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
//        // 初始化数组
//        glueBoolean = new boolean[GWOutPort.USER_O_NO_ALL.ordinal()];
//        GluePort = new String[5];
        popupViews = new ArrayList<>();
        initPicker();

    }

    /**
     * @param weldLineStartParam
     * @Title UpdateInfos
     * @Description 更新extendView数据（保存的数据）
     * @author wj
     */
    private void UpdateInfos(PointWeldLineStartParam weldLineStartParam) {
        if (weldLineStartParam == null) {
            et_sendSnSpeed.setText("");
            et_preSendSnSum.setText("");
            et_preSendSnSpeed.setText("");
            et_moveSpeed.setText("");
            et_preHeatTime.setText("");

        } else {
            et_sendSnSpeed.setText(weldLineStartParam.getSnSpeed() + "");
            et_preSendSnSum.setText((float)weldLineStartParam.getPreSendSnSum()/10 + "");
            et_preSendSnSpeed.setText(weldLineStartParam.getPreSendSnSpeed() + "");
            et_moveSpeed.setText(weldLineStartParam.getMoveSpeed() + "");
            et_preHeatTime.setText(weldLineStartParam.getPreHeatTime() + "");

            switch_isSn.setChecked(weldLineStartParam.isSn());
        }
    }

    /**
     * 加载自定义的组件，并设置NumberPicker的最大最小和默认值
     */
    private void initPicker() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(
                R.string.activity_glue_line_start));
        mMorenTextView = (TextView) findViewById(R.id.morenfangan);
        mFanganliebiao = (TextView) findViewById(R.id.fanganliebiao);
        mFanganliebiao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(50));
        mMorenTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        mMorenTextView.setText("当前默认方案号(" + defaultNum + ")");
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        iv_loading.setVisibility(View.VISIBLE);
        stub_glue = (ViewStub) findViewById(R.id.stub_glue_line_start);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = Activity_Init_View;
                handler.sendMessage(msg);
            }
        }, 50);// 延迟1毫秒,然后加载


    }

    protected void setTitleInfos(List<PointWeldLineStartParam> weldStartLists,
                                 View view, int p) {
        mTitle_sendSnSpeed = (TextView) view.findViewById(R.id.title_sendSnSpeed);
        mTitle_et_linestart_sendSnSpeed = (TextView) view.findViewById(R.id.title_et_linestart_sendSnSpeed);
        mActivity_mm_s = (TextView) view.findViewById(R.id.activity_mm_s);
        mActivity_fenghao = (TextView) view.findViewById(R.id.activity_fenghao);
        mTitle_preSendSnSum = (TextView) view.findViewById(R.id.title_preSendSnSum);
        mTitle_et_preSendSnSum = (TextView) view.findViewById(R.id.title_et_preSendSnSum);
        mActivity_mm = (TextView) view.findViewById(R.id.activity_mm);
        mActivity_second_fenghao = (TextView) view.findViewById(R.id.activity_second_fenghao);
        mTitle_preSendSnSpeed = (TextView) view.findViewById(R.id.title_preSendSnSpeed);
        mTitle_et_preSendSnSpeed = (TextView) view.findViewById(R.id.title_et_preSendSnSpeed);
        mActivity_sec_mm_s = (TextView) view.findViewById(R.id.activity_sec_mm_s);
        mActivity_third_fenghao = (TextView) view.findViewById(R.id.activity_third_fenghao);
        mTitle_moveSpeed = (TextView) view.findViewById(R.id.title_moveSpeed);
        mTitle_et_linestart_moveSpeed = (TextView) view.findViewById(R.id.title_et_linestart_moveSpeed);
        mActivity_third_mm_s = (TextView) view.findViewById(R.id.activity_third_mm_s);
        mActivity_four_fenghao = (TextView) view.findViewById(R.id.activity_four_fenghao);
        mTitle_preHeatTime = (TextView) view.findViewById(R.id.title_preHeatTime);
        mTitle_et_preHeatTime = (TextView) view.findViewById(R.id.title_et_preHeatTime);
        mActivity_ms = (TextView)  view.findViewById(R.id.activity_ms);
        mActivity_five_fenghao = (TextView)  view.findViewById(R.id.activity_five_fenghao);
        mTitle_isSn = (TextView)  view.findViewById(R.id.title_isSn);
        mTitle_et_isSn = (TextView)  view.findViewById(R.id.title_et_isSn);
        for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
            if (p == pointWeldLineStartParam.get_id()) {
                /*===================== begin =====================*/
                mTitle_sendSnSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_preSendSnSum.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_preSendSnSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_moveSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_preHeatTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_isSn.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_et_linestart_sendSnSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_et_linestart_moveSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_et_preHeatTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_et_preSendSnSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_et_preSendSnSum.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mTitle_et_isSn.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_mm_s.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_sec_mm_s.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_third_mm_s.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_second_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_third_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_four_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
                mActivity_five_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));

				/*=====================  end =====================*/
                mTitle_sendSnSpeed.setText(getResources().getString(R.string.activity_weld_work_sendSnSpeed) + " ");
                mTitle_preSendSnSum.setText(getResources().getString(R.string.activity_weld_work_preSendSnSum) + " ");
                mTitle_preSendSnSpeed.setText(getResources().getString(R.string.activity_weld_work_preSendSnSpeed) + " ");
                mTitle_moveSpeed.setText(getResources().getString(R.string.activity_weld_work_moveSpeed) + " ");
                mTitle_preHeatTime.setText(getResources().getString(R.string.activity_weld_work_preHeatTime) + " ");
                mTitle_isSn.setText(getResources().getString(R.string.activity_glue_alone_isSn) + " ");

                mActivity_mm_s.setText(getResources().getString(R.string.activity_mm_s) + " ");
                mActivity_mm.setText(getResources().getString(R.string.activity_mm) + " ");
                mActivity_sec_mm_s.setText(getResources().getString(R.string.activity_mm_s) + " ");
                mActivity_ms.setText(getResources().getString(R.string.activity_ms) + " ");
                mActivity_third_mm_s.setText(getResources().getString(R.string.activity_mm_s) + " ");
                mActivity_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_second_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_third_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_four_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_five_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");



                mTitle_et_linestart_sendSnSpeed.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_linestart_sendSnSpeed.getPaint()
                        .setAntiAlias(true); // 抗锯齿
                mTitle_et_linestart_moveSpeed.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_linestart_moveSpeed.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_preHeatTime.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_preHeatTime.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_preSendSnSpeed.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_preSendSnSpeed.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_preSendSnSum.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_preSendSnSum.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_isSn.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_isSn.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_linestart_sendSnSpeed.setText(pointWeldLineStartParam.getSnSpeed()+"");
                mTitle_et_linestart_moveSpeed.setText(pointWeldLineStartParam.getMoveSpeed()+"");
                mTitle_et_preHeatTime.setText(pointWeldLineStartParam.getPreHeatTime()+"");
                mTitle_et_preSendSnSpeed.setText(pointWeldLineStartParam.getPreSendSnSpeed()+"");
                mTitle_et_preSendSnSum.setText(pointWeldLineStartParam.getPreSendSnSum()/10+"");
                if (pointWeldLineStartParam.isSn()) {
                    mTitle_et_isSn.setText("是");
                } else {
                    mTitle_et_isSn.setText("否");
                }
            }
        }
    }

    /**
     * @Title SetDateAndRefreshUI
     * @Description 打开extendview的时候设置界面内容，显示最新的方案数据而不是没有保存的数据,没有得到保存的方案
     * @author wj
     */
    protected void SetDateAndRefreshUI() {
        weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
            list.add(pointWeldLineStartParam.get_id());
        }
        L.d("存放主键id的集合---->" + list);
        L.d("当前选择的方案号---->" + currentTaskNum);
        L.d("list是否存在------------》"
                + list.contains(currentTaskNum));
        if (list.contains(currentTaskNum)) {
            // 已经保存在数据库中的数据
            for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                if (currentTaskNum == pointWeldLineStartParam.get_id()) {
                    View extendView = popupListView.getItemViews()
                            .get(currentClickNum).getExtendView();
                    initView(extendView);
                    UpdateInfos(pointWeldLineStartParam);
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

    /**
     * @Title save
     * @Description 保存信息到Param的一个对象中，并更新数据库数据
     * @author wj
     */
    protected void save() {
        View extendView = popupListView.getItemViews().get(currentClickNum)
                .getExtendView();
        weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
            list.add(pointWeldLineStartParam.get_id());
        }
        // 判空
        isOk = isEditClean(extendView);
        if (isOk) {

            PointWeldLineStartParam upglueStartParam = getLineStart(extendView);
            if (weldStartLists.contains(upglueStartParam)) {
                // 默认已经存在的方案但是不能创建方案只能改变默认方案号
                if (list.contains(currentTaskNum)) {
                    isExist = true;
                }
                // 保存的方案已经存在但不是当前编辑的方案
                if (currentTaskNum != weldStartLists.get(
                        weldStartLists.indexOf(upglueStartParam)).get_id()) {
                    ToastUtil.displayPromptInfo(WeldLineStartActivity.this,
                            getResources()
                                    .getString(R.string.task_is_exist_yes));
                }
            } else {

                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                    if (currentTaskNum == pointWeldLineStartParam.get_id()) {// 说明之前插入过
                        flag = true;
                    }
                }
                if (flag) {
                    // 更新数据
                    int rowid = weldStartDao
                            .upDateWeldLineStart(upglueStartParam,taskname);
                    update_id.put(upglueStartParam.get_id(), upglueStartParam);
                    // System.out.println("修改的方案号为："+upglueAlone.get_id());
                } else {
                    // 插入一条数据
                    long rowid = weldStartDao
                            .insertWeldLineStart(upglueStartParam,taskname);
                    firstExist = true;
                    weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
                    L.d(TAG, "保存之后新方案-->" + weldStartLists.toString());
                    ToastUtil.displayPromptInfo(WeldLineStartActivity.this,
                            getResources().getString(R.string.save_success));
                    list.clear();
                    for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                        list.add(pointWeldLineStartParam.get_id());
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
        weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
        // popupListView->pupupview->title
        for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {

            if (currentTaskNum == pointWeldLineStartParam.get_id()) {
                // 需要设置两个view，因为view内容相同但是parent不同
                View titleViewItem = popupListView.getItemViews()
                        .get(currentClickNum).getPopupView();
                View titleViewExtend = popupListView.getItemViews()
                        .get(currentClickNum).getExtendPopupView();
                setTitleInfos(weldStartLists, titleViewItem, currentTaskNum);
                setTitleInfos(weldStartLists, titleViewExtend, currentTaskNum);
            }
        }
    }

    /**
     * @param extendView
     * @Title initView
     * @Description 初始化当前extendView视图
     * @author wj
     */
    protected void initView(View extendView) {
        et_sendSnSpeed = (EditText) extendView.findViewById(R.id.et_sendSnSpeed);
        et_preSendSnSum = (EditText) extendView.findViewById(R.id.et_preSendSnSum);
        et_preSendSnSpeed = (EditText) extendView.findViewById(R.id.et_preSendSnSpeed);
        et_moveSpeed = (EditText) extendView.findViewById(R.id.et_moveSpeed);
        et_preHeatTime = (EditText) extendView.findViewById(R.id.et_preHeatTime);
        switch_isSn = (SwitchButton) extendView.findViewById(R.id.switch_isSn);

        tv_outGlueTimePrev = (TextView) extendView.findViewById(R.id.tv_outGlueTimePrev);
        tv_preHeatTime = (TextView) extendView.findViewById(R.id.tv_preHeatTime);
        extend_line_start_ms = (TextView) extendView.findViewById(R.id.extend_line_start_ms);
        tv_outGlueTime = (TextView) extendView.findViewById(R.id.tv_outGlueTime);
        tv_outGlueTime_ms = (TextView) extendView.findViewById(R.id.tv_outGlueTime_ms);
        tv_movespeed_ms = (TextView) extendView.findViewById(R.id.tv_movespeed_ms);
        tv_moveSpeed = (TextView) extendView.findViewById(R.id.tv_moveSpeed);
        tv_moveSpeed2 = (TextView) extendView.findViewById(R.id.tv_moveSpeed2);
        extend_isSn = (TextView) extendView.findViewById(R.id.extend_isSn);
        extend_line_start_mms = (TextView) extendView.findViewById(R.id.extend_line_start_mms);
        extend_preHeatTime_ms = (TextView) extendView.findViewById(R.id.extend_preHeatTime_ms);
        extend_default = (TextView) extendView.findViewById(R.id.extend_default);
        extend_save = (TextView) extendView.findViewById(R.id.extend_save);
        /*===================== begin =====================*/

        et_sendSnSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_preSendSnSum.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_preSendSnSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_moveSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_preHeatTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_outGlueTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_line_start_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_outGlueTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_outGlueTime_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_moveSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_movespeed_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_moveSpeed2.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_isSn.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_preHeatTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_line_start_mms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_preHeatTime_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_default.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		/*=====================  end =====================*/
    }

    /**
     * 将页面上的数据保存到一个PointGlueLineStartParam对象中
     *
     * @return PointGlueLineStartParam
     */
    private PointWeldLineStartParam getLineStart(View extendView) {
        weldStart = new PointWeldLineStartParam();
        et_sendSnSpeed = (EditText) extendView
                .findViewById(R.id.et_sendSnSpeed);
        et_preSendSnSum = (EditText) extendView
                .findViewById(R.id.et_preSendSnSum);
        et_preSendSnSpeed = (EditText) extendView
                .findViewById(R.id.et_preSendSnSpeed);
        et_moveSpeed = (EditText) extendView
                .findViewById(R.id.et_moveSpeed);
        et_preHeatTime = (EditText) extendView
                .findViewById(R.id.et_preHeatTime);
        switch_isSn = (SwitchButton) extendView
                .findViewById(R.id.switch_isSn);

        try {
            sendSnSpeed = Integer.parseInt(et_sendSnSpeed
                    .getText().toString());
        } catch (NumberFormatException e) {
            sendSnSpeed = 0;
        }
        try {
            preSendSnSum = Float.parseFloat(et_preSendSnSum
                    .getText().toString());
        } catch (NumberFormatException e) {
            preSendSnSum = 0;
        }
        try {
            preSendSnSpeed = Integer.parseInt(et_preSendSnSpeed.getText()
                    .toString());

        } catch (NumberFormatException e) {
            preSendSnSpeed = 0;
        }
        try {
            moveSpeed= Integer.parseInt(et_moveSpeed.getText()
                    .toString());

        } catch (NumberFormatException e) {
            moveSpeed = 0;
        }
        try {
            preHeatTime = Integer.parseInt(et_preHeatTime.getText().toString());
        } catch (NumberFormatException e) {
            preHeatTime = 0;
        }
        weldStart.setPreSendSnSpeed(sendSnSpeed);
        weldStart.setPreSendSnSum((int) preSendSnSum*10);
        weldStart.setPreSendSnSpeed(preSendSnSpeed);
        weldStart.setMoveSpeed(moveSpeed);
        weldStart.setPreHeatTime(preHeatTime);
        weldStart.setSn(switch_isSn.isChecked());


        weldStart.set_id(currentTaskNum);

        return weldStart;
    }

    /**
     * @param extendView
     * @return false表示为空, true表示都有数据
     * @Title isEditClean
     * @Description 判断输入框是否为空
     * @author wj
     */
    private boolean isEditClean(View extendView) {
        et_sendSnSpeed = (EditText) extendView
                .findViewById(R.id.et_sendSnSpeed);
        et_preSendSnSum = (EditText) extendView
                .findViewById(R.id.et_preSendSnSum);
        et_preSendSnSpeed = (EditText) extendView
                .findViewById(R.id.et_preSendSnSpeed);
        et_moveSpeed = (EditText) extendView
                .findViewById(R.id.et_moveSpeed);
        et_preHeatTime = (EditText) extendView
                .findViewById(R.id.et_preHeatTime);

        if ("".equals(et_sendSnSpeed.getText().toString())) {
            return false;
        } else if ("".equals(et_preSendSnSum.getText().toString())) {
            return false;
        } else if ("".equals(et_preSendSnSpeed.getText().toString())) {
            return false;
        } else if ("".equals(et_moveSpeed.getText().toString())) {
            return false;
        }else if ("".equals(et_preHeatTime.getText().toString())) {
            return false;
        }
        return true;
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

    /**
     * @Title complete
     * @Description 最终完成返回
     * @author wj
     */
    private void complete() {
        // TODO Auto-generated method stub
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
        point.setPointParam(weldStartDao.getPointWeldLineStartParamByID(mIndex,taskname));
        L.d("返回的Point为================》" + point);

        List<Map<Integer, PointWeldLineStartParam>> list = new ArrayList<Map<Integer, PointWeldLineStartParam>>();
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

    private class RevHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SocketInputThread.SocketError) {
                //wifi中断
                L.d("wifi连接断开。。");
                SocketThreadManager.releaseInstance();
                L.d("单例被释放了-----------------------------");
                //设置全局变量，跟新ui
                userApplication.setWifiConnecting(false);
//				WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
                ToastUtil.displayPromptInfo(WeldLineStartActivity.this, "wifi连接断开。。");
            } else if (msg.what == Activity_Init_View) {
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
            PopupView popupView = new PopupView(this,
                    R.layout.popup_view_item_glue_start) {

                @Override
                public void setViewsElements(View view) {
                    // TextView textView = (TextView) view
                    // .findViewById(R.id.title);
                    weldStartLists = weldStartDao.findAllWeldLineStartParams(taskname);
                    ImageView title_num = (ImageView) view
                            .findViewById(R.id.title_num);
                    if (p == 1) {// 方案列表第一位对应一号方案
                        title_num.setImageResource(R.drawable.green1);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 2) {
                        title_num.setImageResource(R.drawable.green2);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 3) {
                        title_num.setImageResource(R.drawable.green3);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 4) {
                        title_num.setImageResource(R.drawable.green4);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 5) {
                        title_num.setImageResource(R.drawable.green5);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 6) {
                        title_num.setImageResource(R.drawable.green6);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 7) {
                        title_num.setImageResource(R.drawable.green7);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 8) {
                        title_num.setImageResource(R.drawable.green8);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 9) {
                        title_num.setImageResource(R.drawable.green9);
                        setTitleInfos(weldStartLists, view, p);
                    } else if (p == 10) {
                        title_num.setImageResource(R.drawable.green10);
                        setTitleInfos(weldStartLists, view, p);
                    }
                }

                @Override
                public View setExtendView(View view) {
                    if (view == null) {
                        extendView = LayoutInflater.from(
                                getApplicationContext()).inflate(
                                R.layout.glue_start_extend_view, null);
                        int size = weldStartLists.size();
                        while (size > 0) {
                            size--;
                            if (p == 1) {// 方案列表第一位对应一号方案
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 2) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 3) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 4) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 5) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 6) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 7) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 8) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 9) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
                                    }
                                }
                            } else if (p == 10) {
                                initView(extendView);
                                for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
                                    if (p == pointWeldLineStartParam.get_id()) {
                                        UpdateInfos(pointWeldLineStartParam);
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
                    et_sendSnSpeed = (EditText) extendView
                            .findViewById(R.id.et_sendSnSpeed);
                    et_preSendSnSum = (EditText) extendView
                            .findViewById(R.id.et_preSendSnSum);
                    et_preSendSnSpeed = (EditText) extendView
                            .findViewById(R.id.et_preSendSnSpeed);
                    et_moveSpeed = (EditText) extendView
                            .findViewById(R.id.et_moveSpeed);
                    et_preHeatTime = (EditText) extendView
                            .findViewById(R.id.et_preHeatTime);
                    switch_isSn = (SwitchButton) extendView
                            .findViewById(R.id.switch_isSn);


                    // 设置出胶前延时的默认值和最大最小值
                    et_sendSnSpeed
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueLineStart.SENDSNSPEED,
                                    PointConfigParam.GlueLineStart.GlueLineStartMin,
                                    et_sendSnSpeed));
                    et_sendSnSpeed
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueLineStart.SENDSNSPEED,
                                    PointConfigParam.GlueLineStart.GlueLineStartMin,
                                    et_sendSnSpeed));
                    et_sendSnSpeed.setSelectAllOnFocus(true);

                    // 设置出胶后延时的默认值和最大最小值
                    et_preSendSnSum
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueLineStart.SENDSNSPEED,
                                    PointConfigParam.GlueLineStart.GlueLineStartMin,
                                    et_preSendSnSum));
                    setPoint(et_preSendSnSum);//限制小数
                    et_preSendSnSum
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueLineStart.SENDSNSPEED,
                                    PointConfigParam.GlueLineStart.GlueLineStartMin,
                                    et_preSendSnSum));
                    et_preSendSnSum.setSelectAllOnFocus(true);

                    // 设置轨迹速度的默认值和最大最小值
                    et_preSendSnSpeed
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueLineStart.MoveSpeedMax,
                                    PointConfigParam.GlueLineStart.MoveSpeedMin,
                                    et_preSendSnSpeed));
                    et_preSendSnSpeed
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueLineStart.MoveSpeedMax,
                                    PointConfigParam.GlueLineStart.MoveSpeedMin,
                                    et_preSendSnSpeed));
                    et_preSendSnSpeed.setSelectAllOnFocus(true);
                    et_moveSpeed
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueLineStart.SENDSNSPEED,
                                    PointConfigParam.GlueLineStart.MoveSpeedMin,
                                    et_moveSpeed));
                    et_moveSpeed
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueLineStart.SENDSNSPEED,
                                    PointConfigParam.GlueLineStart.MoveSpeedMin,
                                    et_moveSpeed));
                    et_moveSpeed.setSelectAllOnFocus(true);
                    et_preHeatTime
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueLineStart.OutGlueTimePrevMax,
                                    PointConfigParam.GlueLineStart.MoveSpeedMin,
                                    et_preHeatTime));
                    et_preHeatTime
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueLineStart.OutGlueTimePrevMax,
                                    PointConfigParam.GlueLineStart.MoveSpeedMin,
                                    et_preHeatTime));
                    et_preHeatTime.setSelectAllOnFocus(true);

                    rl_moren = (RelativeLayout) extendView
                            .findViewById(R.id.rl_moren);
                    iv_add = (ImageView) extendView.findViewById(R.id.iv_add);
                    rl_save = (RelativeLayout) extendView
                            .findViewById(R.id.rl_save);// 保存按钮
                    iv_moren = (ImageView) extendView
                            .findViewById(R.id.iv_moren);// 默认按钮
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
                                SharePreferenceUtils.saveParamNumberToPref(
                                                WeldLineStartActivity.this,
                                                SettingParam.DefaultNum.ParamGlueLineStartNumber,
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
        for (PointWeldLineStartParam pointWeldLineStartParam : weldStartLists) {
            list.add(pointWeldLineStartParam.get_id());
        }
        popupListView.setSelectedEnable(list);
        popupListView.setOnClickPositionChanged(new PopupListView.OnClickPositionChanged() {
            @Override
            public void getCurrentPositon(int position) {
                currentTaskNum = position + 1;
                currentClickNum = position;
            }
        });
        popupListView.setOnZoomInListener(new PopupListView.OnZoomInChanged() {

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

    /**
     * 限制1位小数
     * @param editText
     */
    public  void setPoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS+1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
