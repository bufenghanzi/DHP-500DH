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
import com.mingseal.data.dao.WeldInputDao;
import com.mingseal.data.param.PointConfigParam;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.point.GWOutPort;
import com.mingseal.data.point.IOPort;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.dhp_500dh.R;
import com.mingseal.listener.MaxMinEditWatcher;
import com.mingseal.listener.MaxMinFocusChangeListener;
import com.mingseal.listener.MyPopWindowClickListener;
import com.mingseal.ui.PopupListView;
import com.mingseal.ui.PopupView;
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
public class WeldInputActivity extends AutoLayoutActivity implements OnClickListener {
    private final static String TAG = "WeldInputActivity";
    /**
     * 标题栏的标题
     */
    private TextView tv_title;
    /**
     * IO口
     */
    private ToggleButton[] ioSwitch;

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

    private WeldInputDao inputDao;
    private List<PointWeldInputIOParam> inputIOLists;
    private PointWeldInputIOParam inputIO;
    private boolean[] ioBoolean;
    private int param_id = 1;// / 选取的是几号方案
    /**
     * @Fields goTimePrevInt: 动作前延时的int值
     */
    private int goTimePrevInt = 0;
    /**
     * @Fields goTimeNextInt: 动作后延时的int值
     */
    private int goTimeNextInt = 0;
    /**
     * @Fields isNull: 判断编辑输入框是否为空,false表示为空,true表示不为空
     */
    private boolean isNull = false;
    private boolean flag = false;// 可以与用户交互，初始化完成标志
    /* =================== begin =================== */
    private HashMap<Integer, PointWeldInputIOParam> update_id;// 修改的方案号集合
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
    private RelativeLayout rl_moren;
    private ImageView iv_add;
    private ImageView iv_moren;
    /**
     * @Fields et_input_goTimePrev: 动作前延时
     */
    private EditText et_input_goTimePrev;
    /**
     * @Fields et_input_goTimeNext: 动作后延时
     */
    private EditText et_input_goTimeNext;
    String[] GluePort;
    private TextView title_goTimePrev;
    private TextView title_et_input_goTimePrev;
    private TextView activity_ms;
    private TextView activity_fenghao;
    private TextView title_goTimeNext;
    private TextView title_et_input_goTimeNext;
    private TextView activity_second_ms;
    private TextView activity_second_fenghao;
    private TextView activity_glue_io;
    private TextView title_et_activity_glue_io;
    private TextView tv_goTimePrev;
    private TextView extend_ms;
    private TextView tv_goTimeNext;
    private TextView tv_goTimeNext_ms;
    private TextView extend_io1;
    private TextView extend_io2;
    private TextView extend_io3;
    private TextView extend_io4;
    private TextView extend_default;
    private TextView extend_save;
    private TextView mFanganliebiao;
    private RevHandler handler;
    private UserApplication userApplication;
    /* =================== end =================== */
    private ViewStub stub_glue;
    private int Activity_Init_View = 6;
    private ImageView iv_loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glue_input);
        userApplication = (UserApplication) getApplication();
        handler = new RevHandler();
        update_id = new HashMap<>();
        intent = getIntent();
        point = intent
                .getParcelableExtra(MyPopWindowClickListener.POPWINDOW_KEY);
        mFlag = intent.getIntExtra(MyPopWindowClickListener.FLAG_KEY, 0);
        mType = intent.getIntExtra(MyPopWindowClickListener.TYPE_KEY, 0);
        defaultNum = SharePreferenceUtils.getParamNumberFromPref(
                WeldInputActivity.this,
                SettingParam.DefaultNum.ParamGlueInputNumber);

        inputDao = new WeldInputDao(this);
        inputIOLists = inputDao.findAllWeldInputParams();
        if (inputIOLists == null || inputIOLists.isEmpty()) {
            inputIO = new PointWeldInputIOParam();
            inputIO.set_id(param_id);
            inputDao.insertWeldInput(inputIO);
            // 插入主键id
        }
        inputIOLists = inputDao.findAllWeldInputParams();
        // 初始化数组
        ioBoolean = new boolean[IOPort.IO_NO_ALL.ordinal()];
        popupViews = new ArrayList<>();
        GluePort = new String[4];
        initPicker();
    }

    private void UpdateInfos(PointWeldInputIOParam weldInputIOParam) {
        if (weldInputIOParam == null) {
            et_input_goTimePrev.setText("");
            et_input_goTimeNext.setText("");

        } else {
            et_input_goTimePrev.setText(weldInputIOParam.getGoTimePrev() + "");
            et_input_goTimeNext.setText(weldInputIOParam.getGoTimeNext() + "");

            ioSwitch[0].setChecked(weldInputIOParam.getInputPort()[0]);
            ioSwitch[1].setChecked(weldInputIOParam.getInputPort()[1]);
            ioSwitch[2].setChecked(weldInputIOParam.getInputPort()[2]);
            ioSwitch[3].setChecked(weldInputIOParam.getInputPort()[3]);
        }
    }

    /**
     * 加载组件，并设置NumberPicker的最大最小值
     */
    private void initPicker() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.activity_glue_input));
        mMorenTextView = (TextView) findViewById(R.id.morenfangan);
        mFanganliebiao = (TextView) findViewById(R.id.fanganliebiao);
        mFanganliebiao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(50));
        mMorenTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        mMorenTextView.setText("当前默认方案号(" + defaultNum + ")");
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        iv_loading.setVisibility(View.VISIBLE);
        stub_glue = (ViewStub) findViewById(R.id.stub_glue_input);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = Activity_Init_View;
                handler.sendMessage(msg);
            }
        }, 50);// 延迟1毫秒,然后加载


    }

    protected void setTitleInfos(List<PointWeldInputIOParam> inputIOLists,
                                 View view, int p) {
        title_goTimePrev = (TextView) view.findViewById(R.id.title_goTimePrev);
        title_et_input_goTimePrev = (TextView) view.findViewById(R.id.title_et_input_goTimePrev);
        activity_ms = (TextView) view.findViewById(R.id.activity_ms);
        activity_fenghao = (TextView) view.findViewById(R.id.activity_fenghao);
        title_goTimeNext = (TextView) view.findViewById(R.id.title_goTimeNext);
        title_et_input_goTimeNext = (TextView) view.findViewById(R.id.title_et_input_goTimeNext);
        activity_second_ms = (TextView) view.findViewById(R.id.activity_second_ms);
        activity_second_fenghao = (TextView) view.findViewById(R.id.activity_second_fenghao);
        activity_glue_io = (TextView) view.findViewById(R.id.activity_glue_io);
        title_et_activity_glue_io = (TextView) view.findViewById(R.id.title_et_activity_glue_io);

        for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
            if (p == pointWeldInputIOParam.get_id()) {
                /*===================== begin =====================*/
                title_goTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                title_et_input_goTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                activity_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                activity_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                title_goTimeNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                title_et_input_goTimeNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                activity_second_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                activity_second_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                activity_glue_io.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
                title_et_activity_glue_io.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
				/*=====================  end =====================*/

                activity_ms.setText(getResources().getString(
                        R.string.activity_ms));
                activity_second_ms.setText(getResources().getString(
                        R.string.activity_ms));
                activity_fenghao.setText(getResources().getString(
                        R.string.activity_fenghao)
                        + " ");
                activity_second_fenghao.setText(getResources().getString(
                        R.string.activity_fenghao)
                        + " ");
                title_goTimePrev.setText(getResources().getString(
                        R.string.activity_glue_goTimePrev)
                        + " ");
                title_goTimeNext.setText(getResources().getString(
                        R.string.activity_glue_goTimeNext)
                        + " ");
                activity_glue_io.setText(getResources().getString(
                        R.string.activity_glue_io)
                        + " ");

                title_et_input_goTimePrev.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                title_et_input_goTimePrev.getPaint()
                        .setAntiAlias(true); // 抗锯齿
                title_et_input_goTimeNext.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                title_et_input_goTimeNext.getPaint().setAntiAlias(true); // 抗锯齿
                title_et_activity_glue_io.getPaint().setFlags(
                        Paint.UNDERLINE_TEXT_FLAG); // 下划线
                title_et_activity_glue_io.getPaint().setAntiAlias(true); // 抗锯齿

                title_et_input_goTimePrev.setText(pointWeldInputIOParam
                        .getGoTimePrev() + "");
                title_et_input_goTimeNext.setText(pointWeldInputIOParam
                        .getGoTimeNext() + "");
                for (int j = 0; j < 4; j++) {
                    if (pointWeldInputIOParam.getInputPort()[j]) {
                        GluePort[j] = "开";
                    } else {
                        GluePort[j] = "关";
                    }
                }
                title_et_activity_glue_io.setText(GluePort[0] + GluePort[1]
                        + GluePort[2] + GluePort[3]);
            }
        }
    }

    /**
     * @Title SetDateAndRefreshUI
     * @Description 打开extendview的时候设置界面内容，显示最新的方案数据而不是没有保存的数据,没有得到保存的方案
     * @author wj
     */
    protected void SetDateAndRefreshUI() {
        inputIOLists = inputDao.findAllWeldInputParams();
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
            list.add(pointWeldInputIOParam.get_id());
        }
        System.out.println("存放主键id的集合---->" + list);
        System.out.println("当前选择的方案号---->" + currentTaskNum);
        System.out.println("list是否存在------------》"
                + list.contains(currentTaskNum));
        if (list.contains(currentTaskNum)) {
            // 已经保存在数据库中的数据
            for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                if (currentTaskNum == pointWeldInputIOParam.get_id()) {
                    View extendView = popupListView.getItemViews()
                            .get(currentClickNum).getExtendView();
                    initView(extendView);
                    UpdateInfos(pointWeldInputIOParam);
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
        inputIOLists = inputDao.findAllWeldInputParams();
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
            list.add(pointWeldInputIOParam.get_id());
        }
        // 判空
        isOk = isEditClean(extendView);
        if (isOk) {

            PointWeldInputIOParam upInputIOParam = getInputIOParam(extendView);
            if (inputIOLists.contains(upInputIOParam)) {
                // 默认已经存在的方案但是不能创建方案只能改变默认方案号

                if (list.contains(currentTaskNum)) {
                    isExist = true;
                }
                // 保存的方案已经存在但不是当前编辑的方案
                if (currentTaskNum != inputIOLists.get(
                        inputIOLists.indexOf(upInputIOParam)).get_id()) {
                    ToastUtil.displayPromptInfo(WeldInputActivity.this,
                            getResources()
                                    .getString(R.string.task_is_exist_yes));
                }
            } else {
                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                    if (currentTaskNum == pointWeldInputIOParam.get_id()) {// 说明之前插入过
                        flag = true;
                    }
                }
                if (flag) {
                    // 更新数据
                    int rowid = inputDao.upDateGlueInput(upInputIOParam);
                    // System.out.println("影响的行数"+rowid);
                    update_id.put(upInputIOParam.get_id(), upInputIOParam);
                    // mPMap.map.put(upglueAlone.get_id(), upglueAlone);
                    System.out.println("修改的方案号为：" + upInputIOParam.get_id());
                    // System.out.println(glueAloneDao.getPointGlueAloneParamById(currentTaskNum).toString());
                } else {
                    // 插入一条数据
                    long rowid = inputDao.insertWeldInput(upInputIOParam);
                    firstExist = true;
                    inputIOLists = inputDao.findAllWeldInputParams();
                    Log.i(TAG, "保存之后新方案-->" + inputIOLists.toString());
                    ToastUtil.displayPromptInfo(WeldInputActivity.this,
                            getResources().getString(R.string.save_success));
                    list.clear();
                    for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                        list.add(pointWeldInputIOParam.get_id());
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

    private void refreshTitle() {
        inputIOLists = inputDao.findAllWeldInputParams();
        // popupListView->pupupview->title
        for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {

            if (currentTaskNum == pointWeldInputIOParam.get_id()) {
                // 需要设置两个view，因为view内容相同但是parent不同
                View titleViewItem = popupListView.getItemViews()
                        .get(currentClickNum).getPopupView();
                View titleViewExtend = popupListView.getItemViews()
                        .get(currentClickNum).getExtendPopupView();
                setTitleInfos(inputIOLists, titleViewItem, currentTaskNum);
                setTitleInfos(inputIOLists, titleViewExtend, currentTaskNum);
            }
        }
    }

    private boolean isEditClean(View extendView) {
        et_input_goTimePrev = (EditText) extendView
                .findViewById(R.id.et_input_goTimePrev);
        et_input_goTimeNext = (EditText) extendView
                .findViewById(R.id.et_input_goTimeNext);
        if ("".equals(et_input_goTimeNext.getText().toString())) {
            return false;
        } else if ("".equals(et_input_goTimePrev.getText().toString())) {
            return false;
        }
        return true;
    }

    protected void initView(View extendView) {
        et_input_goTimePrev = (EditText) extendView
                .findViewById(R.id.et_input_goTimePrev);
        et_input_goTimeNext = (EditText) extendView
                .findViewById(R.id.et_input_goTimeNext);

        ioSwitch = new ToggleButton[GWOutPort.USER_O_NO_ALL.ordinal()];
        ioSwitch[0] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport1);
        ioSwitch[1] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport2);
        ioSwitch[2] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport3);
        ioSwitch[3] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport4);
        // rl_moren = (RelativeLayout) findViewById(R.id.rl_moren);
        // iv_add = (ImageView) findViewById(R.id.iv_add);
        // rl_save = (RelativeLayout) findViewById(R.id.rl_save);
        // iv_moren = (ImageView) findViewById(R.id.iv_moren);
        tv_goTimePrev = (TextView) extendView.findViewById(R.id.tv_goTimePrev);
        extend_ms = (TextView) extendView.findViewById(R.id.extend_ms);
        tv_goTimeNext = (TextView) extendView.findViewById(R.id.tv_goTimeNext);
        tv_goTimeNext_ms = (TextView) extendView.findViewById(R.id.tv_goTimeNext_ms);
        extend_io1 = (TextView) extendView.findViewById(R.id.extend_io1);
        extend_io2 = (TextView) extendView.findViewById(R.id.extend_io2);
        extend_io3 = (TextView) extendView.findViewById(R.id.extend_io3);
        extend_io4 = (TextView) extendView.findViewById(R.id.extend_io4);
        extend_default = (TextView) extendView.findViewById(R.id.extend_default);
        extend_save = (TextView) extendView.findViewById(R.id.extend_save);
		/*===================== begin =====================*/
        et_input_goTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_input_goTimeNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_goTimePrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_goTimeNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_goTimeNext_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_io1.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_io2.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_io3.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_io4.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_default.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		/*=====================  end =====================*/

    }

    /**
     * 将页面上的数据保存到PointGlueOutputIOParam对象中
     *
     * @param extendView
     * @return PointGlueInputIOParam
     */
    private PointWeldInputIOParam getInputIOParam(View extendView) {
        inputIO = new PointWeldInputIOParam();
        et_input_goTimePrev = (EditText) extendView
                .findViewById(R.id.et_input_goTimePrev);
        et_input_goTimeNext = (EditText) extendView
                .findViewById(R.id.et_input_goTimeNext);
        ioSwitch = new ToggleButton[GWOutPort.USER_O_NO_ALL.ordinal()];
        ioSwitch[0] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport1);
        ioSwitch[1] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport2);
        ioSwitch[2] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport3);
        ioSwitch[3] = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport4);
        try {
            goTimePrevInt = Integer.parseInt(et_input_goTimePrev.getText()
                    .toString());
        } catch (NumberFormatException e) {
            goTimePrevInt = 0;
        }
        try {
            goTimeNextInt = Integer.parseInt(et_input_goTimeNext.getText()
                    .toString());
        } catch (NumberFormatException e) {
            goTimeNextInt = 0;
        }
        inputIO.setGoTimePrev(goTimePrevInt);
        inputIO.setGoTimeNext(goTimeNextInt);
        ioBoolean[0] = ioSwitch[0].isChecked();
        ioBoolean[1] = ioSwitch[1].isChecked();
        ioBoolean[2] = ioSwitch[2].isChecked();
        ioBoolean[3] = ioSwitch[3].isChecked();
        inputIO.setInputPort(ioBoolean);
        inputIO.set_id(currentTaskNum);

        return inputIO;
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
        System.out.println("返回的方案号为================》" + mIndex);
        point.setPointParam(inputDao.getInputPointByID(mIndex));
        System.out.println("返回的Point为================》" + point);

        List<Map<Integer, PointWeldInputIOParam>> list = new ArrayList<Map<Integer, PointWeldInputIOParam>>();
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
                ToastUtil.displayPromptInfo(WeldInputActivity.this,"wifi连接断开。。");
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
            PopupView popupView = new PopupView(this, R.layout.popup_view_item_input) {

                @Override
                public void setViewsElements(View view) {
                    inputIOLists = inputDao.findAllWeldInputParams();
                    ImageView title_num = (ImageView) view
                            .findViewById(R.id.title_num);
                    if (p == 1) {// 方案列表第一位对应一号方案
                        title_num.setImageResource(R.drawable.green1);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 2) {
                        title_num.setImageResource(R.drawable.green2);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 3) {
                        title_num.setImageResource(R.drawable.green3);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 4) {
                        title_num.setImageResource(R.drawable.green4);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 5) {
                        title_num.setImageResource(R.drawable.green5);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 6) {
                        title_num.setImageResource(R.drawable.green6);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 7) {
                        title_num.setImageResource(R.drawable.green7);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 8) {
                        title_num.setImageResource(R.drawable.green8);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 9) {
                        title_num.setImageResource(R.drawable.green9);
//                        setTitleInfos(inputIOLists, view, p);
                    } else if (p == 10) {
                        title_num.setImageResource(R.drawable.green10);
//                        setTitleInfos(inputIOLists, view, p);
                    }
                }

                @Override
                public View setExtendView(View view) {
                    if (view == null) {
                        extendView = LayoutInflater.from(
                                getApplicationContext()).inflate(
                                R.layout.glue_input_extend_view, null);
                        int size = inputIOLists.size();
                        while (size > 0) {
                            size--;
                            if (p == 1) {// 方案列表第一位对应一号方案
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 2) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 3) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 4) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 5) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 6) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 7) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 8) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 9) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
                                    }
                                }
                            } else if (p == 10) {
                                initView(extendView);
                                for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
                                    if (p == pointWeldInputIOParam.get_id()) {
                                        UpdateInfos(pointWeldInputIOParam);
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
                    et_input_goTimePrev = (EditText) extendView
                            .findViewById(R.id.et_input_goTimePrev);
                    et_input_goTimeNext = (EditText) extendView
                            .findViewById(R.id.et_input_goTimeNext);

                    ioSwitch = new ToggleButton[GWOutPort.USER_O_NO_ALL
                            .ordinal()];
                    ioSwitch[0] = (ToggleButton) extendView
                            .findViewById(R.id.switch_glueport1);
                    ioSwitch[1] = (ToggleButton) extendView
                            .findViewById(R.id.switch_glueport2);
                    ioSwitch[2] = (ToggleButton) extendView
                            .findViewById(R.id.switch_glueport3);
                    ioSwitch[3] = (ToggleButton) extendView
                            .findViewById(R.id.switch_glueport4);

                    // 设置动作前延时的最大最小值
                    et_input_goTimePrev
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueInput.GoTimePrevMax,
                                    PointConfigParam.GlueInput.GlueInputMin, et_input_goTimePrev));
                    et_input_goTimePrev
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueInput.GoTimePrevMax,
                                    PointConfigParam.GlueInput.GlueInputMin, et_input_goTimePrev));
                    et_input_goTimePrev.setSelectAllOnFocus(true);

                    // 设置动作后延时的最大最小值
                    et_input_goTimeNext
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueInput.GoTimeNextMax,
                                    PointConfigParam.GlueInput.GlueInputMin, et_input_goTimeNext));
                    et_input_goTimeNext
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueInput.GoTimeNextMax,
                                    PointConfigParam.GlueInput.GlueInputMin, et_input_goTimeNext));
                    et_input_goTimeNext.setSelectAllOnFocus(true);
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
                                SharePreferenceUtils
                                        .saveParamNumberToPref(
                                                WeldInputActivity.this,
                                                SettingParam.DefaultNum.ParamGlueInputNumber,
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
        for (PointWeldInputIOParam pointWeldInputIOParam : inputIOLists) {
            list.add(pointWeldInputIOParam.get_id());
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

}
