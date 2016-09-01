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
import com.mingseal.data.dao.WeldBlowDao;
import com.mingseal.data.param.PointConfigParam;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.point.IOPort;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
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
public class WeldBlowActivity extends AutoLayoutActivity implements OnClickListener {
    private final static String TAG = "WeldBlowActivity";
    /**
     * 标题栏的标题
     */
    private TextView tv_title;
    /**
     * IO口
     */
    private ToggleButton ioSwitch;

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

    private WeldBlowDao weldBlowDao;
    private List<PointWeldBlowParam> weldBlowLists;
    private PointWeldBlowParam weldBlow;
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
    private HashMap<Integer, PointWeldBlowParam> update_id;// 修改的方案号集合
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
    private String taskname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weld_blow);
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
                WeldBlowActivity.this,
                SettingParam.DefaultNum.ParamGlueInputNumber);

        weldBlowDao = new WeldBlowDao(this);
        weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
        if (weldBlowLists == null || weldBlowLists.isEmpty()) {
            weldBlow = new PointWeldBlowParam();
            weldBlow.set_id(param_id);
            weldBlowDao.insertWeldOutput(weldBlow,taskname);
            // 插入主键id
        }
        weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
        // 初始化数组
        ioBoolean = new boolean[IOPort.IO_NO_ALL.ordinal()];
        popupViews = new ArrayList<>();
        GluePort = new String[12];
        initPicker();
    }

    private void UpdateInfos(PointWeldBlowParam weldBlowParam) {
        if (weldBlowParam == null) {
            et_input_goTimePrev.setText("");
            et_input_goTimeNext.setText("");

        } else {
            et_input_goTimePrev.setText(weldBlowParam.getGoTimePrev() + "");
            et_input_goTimeNext.setText(weldBlowParam.getGoTimeNext() + "");

            ioSwitch.setChecked(weldBlowParam.getOutputPort()[4]);
        }
    }

    /**
     * 加载组件，并设置NumberPicker的最大最小值
     */
    private void initPicker() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.activity_weld_blow));
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

    protected void setTitleInfos(List<PointWeldBlowParam> weldBlowParamList,
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

        for (PointWeldBlowParam pointWeldBlowParam : weldBlowParamList) {
            if (p == pointWeldBlowParam.get_id()) {
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

                title_et_input_goTimePrev.setText(pointWeldBlowParam
                        .getGoTimePrev() + "");
                title_et_input_goTimeNext.setText(pointWeldBlowParam
                        .getGoTimeNext() + "");
                for (int j = 0; j < 12; j++) {
                    if (pointWeldBlowParam.getOutputPort()[j]) {
                        GluePort[j] = "开";
                    } else {
                        GluePort[j] = "关";
                    }
                }
                title_et_activity_glue_io.setText(GluePort[4]);
            }
        }
    }

    /**
     * @Title SetDateAndRefreshUI
     * @Description 打开extendview的时候设置界面内容，显示最新的方案数据而不是没有保存的数据,没有得到保存的方案
     * @author wj
     */
    protected void SetDateAndRefreshUI() {
        weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
            list.add(pointWeldBlowParam.get_id());
        }
        L.d("存放主键id的集合---->" + list);
        L.d("当前选择的方案号---->" + currentTaskNum);
        L.d("list是否存在------------》"
                + list.contains(currentTaskNum));
        if (list.contains(currentTaskNum)) {
            // 已经保存在数据库中的数据
            for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                if (currentTaskNum == pointWeldBlowParam.get_id()) {
                    View extendView = popupListView.getItemViews()
                            .get(currentClickNum).getExtendView();
                    initView(extendView);
                    UpdateInfos(pointWeldBlowParam);
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
        weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
            list.add(pointWeldBlowParam.get_id());
        }
        // 判空
        isOk = isEditClean(extendView);
        if (isOk) {

            PointWeldBlowParam upInputIOParam = getInputIOParam(extendView);
            if (weldBlowLists.contains(upInputIOParam)) {
                // 默认已经存在的方案但是不能创建方案只能改变默认方案号

                if (list.contains(currentTaskNum)) {
                    isExist = true;
                }
                // 保存的方案已经存在但不是当前编辑的方案
                if (currentTaskNum != weldBlowLists.get(
                        weldBlowLists.indexOf(upInputIOParam)).get_id()) {
                    ToastUtil.displayPromptInfo(WeldBlowActivity.this,
                            getResources()
                                    .getString(R.string.task_is_exist_yes));
                }
            } else {
                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                    if (currentTaskNum == pointWeldBlowParam.get_id()) {// 说明之前插入过
                        flag = true;
                    }
                }
                if (flag) {
                    // 更新数据
                    int rowid = weldBlowDao.upDateWeldOutput(upInputIOParam,taskname);
                    // System.out.println("影响的行数"+rowid);
                    update_id.put(upInputIOParam.get_id(), upInputIOParam);
                    // mPMap.map.put(upglueAlone.get_id(), upglueAlone);
                    L.d("修改的方案号为：" + upInputIOParam.get_id());
                    // System.out.println(glueAloneDao.getPointGlueAloneParamById(currentTaskNum).toString());
                } else {
                    // 插入一条数据
                    long rowid = weldBlowDao.insertWeldOutput(upInputIOParam,taskname);
                    firstExist = true;
                    weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
                    L.d(TAG, "保存之后新方案-->" + weldBlowLists.toString());
                    ToastUtil.displayPromptInfo(WeldBlowActivity.this,
                            getResources().getString(R.string.save_success));
                    list.clear();
                    for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                        list.add(pointWeldBlowParam.get_id());
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
        weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
        // popupListView->pupupview->title
        for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {

            if (currentTaskNum == pointWeldBlowParam.get_id()) {
                // 需要设置两个view，因为view内容相同但是parent不同
                View titleViewItem = popupListView.getItemViews()
                        .get(currentClickNum).getPopupView();
                View titleViewExtend = popupListView.getItemViews()
                        .get(currentClickNum).getExtendPopupView();
                setTitleInfos(weldBlowLists, titleViewItem, currentTaskNum);
                setTitleInfos(weldBlowLists, titleViewExtend, currentTaskNum);
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

        ioSwitch = (ToggleButton) extendView.findViewById(R.id.switch_glueport1);
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
    private PointWeldBlowParam getInputIOParam(View extendView) {
        weldBlow = new PointWeldBlowParam();
        et_input_goTimePrev = (EditText) extendView
                .findViewById(R.id.et_input_goTimePrev);
        et_input_goTimeNext = (EditText) extendView
                .findViewById(R.id.et_input_goTimeNext);
        ioSwitch = (ToggleButton) extendView
                .findViewById(R.id.switch_glueport1);

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
        weldBlow.setGoTimePrev(goTimePrevInt);
        weldBlow.setGoTimeNext(goTimeNextInt);
        ioBoolean[0] = false;
        ioBoolean[1] = false;
        ioBoolean[2] = false;
        ioBoolean[3] = false;
        ioBoolean[4] = ioSwitch.isChecked();
        ioBoolean[5] = false;
        ioBoolean[6] = false;
        ioBoolean[7] = false;
        ioBoolean[8] = false;
        ioBoolean[9] = false;
        ioBoolean[10] = false;
        ioBoolean[11] = false;
        weldBlow.setOutputPort(ioBoolean);
        weldBlow.set_id(currentTaskNum);

        return weldBlow;
    }

    @Override
    public void onBackPressed() {
        // 不想保存只想回退，不保存数据
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
        } else {
            complete();
            super.onBackPressed();
            overridePendingTransition(R.anim.in_from_left,R.anim.out_from_right);
        }
    }

    private void complete() {
        ArrayList<? extends PopupView> itemPopuViews = popupListView.getItemViews();
        for (PopupView popupView : itemPopuViews) {
            ImageView iv_selected = (ImageView) popupView.getPopupView()
                    .findViewById(R.id.iv_selected);
            if (iv_selected.getVisibility() == View.VISIBLE) {
                mIndex = itemPopuViews.indexOf(popupView) + 1;
            }
        }
        L.d("返回的方案号为================》" + mIndex);
        point.setPointParam(weldBlowDao.getOutPutPointByID(mIndex,taskname));
        L.d("返回的Point为================》" + point);

        List<Map<Integer, PointWeldBlowParam>> list = new ArrayList<Map<Integer, PointWeldBlowParam>>();
        list.add(update_id);
        L.d(TAG, point.toString());
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
                L.d("wifi连接断开。。");
                SocketThreadManager.releaseInstance();
                L.d("单例被释放了-----------------------------");
                //设置全局变量，跟新ui
                userApplication.setWifiConnecting(false);
//				WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
                ToastUtil.displayPromptInfo(WeldBlowActivity.this,"wifi连接断开。。");
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
                    weldBlowLists = weldBlowDao.findAllWeldOutputParams(taskname);
                    ImageView title_num = (ImageView) view
                            .findViewById(R.id.title_num);
                    if (p == 1) {// 方案列表第一位对应一号方案
                        title_num.setImageResource(R.drawable.green1);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 2) {
                        title_num.setImageResource(R.drawable.green2);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 3) {
                        title_num.setImageResource(R.drawable.green3);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 4) {
                        title_num.setImageResource(R.drawable.green4);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 5) {
                        title_num.setImageResource(R.drawable.green5);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 6) {
                        title_num.setImageResource(R.drawable.green6);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 7) {
                        title_num.setImageResource(R.drawable.green7);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 8) {
                        title_num.setImageResource(R.drawable.green8);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 9) {
                        title_num.setImageResource(R.drawable.green9);
                        setTitleInfos(weldBlowLists, view, p);
                    } else if (p == 10) {
                        title_num.setImageResource(R.drawable.green10);
                        setTitleInfos(weldBlowLists, view, p);
                    }
                }

                @Override
                public View setExtendView(View view) {
                    if (view == null) {
                        extendView = LayoutInflater.from(
                                getApplicationContext()).inflate(
                                R.layout.weld_blow_extend_view, null);
                        int size = weldBlowLists.size();
                        while (size > 0) {
                            size--;
                            if (p == 1) {// 方案列表第一位对应一号方案
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 2) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 3) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 4) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 5) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 6) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 7) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 8) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 9) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
                                    }
                                }
                            } else if (p == 10) {
                                initView(extendView);
                                for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
                                    if (p == pointWeldBlowParam.get_id()) {
                                        UpdateInfos(pointWeldBlowParam);
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

                    ioSwitch = (ToggleButton) extendView
                            .findViewById(R.id.switch_glueport1);

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
                                                WeldBlowActivity.this,
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
        for (PointWeldBlowParam pointWeldBlowParam : weldBlowLists) {
            list.add(pointWeldBlowParam.get_id());
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
