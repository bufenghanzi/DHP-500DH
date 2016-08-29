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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mingseal.application.UserApplication;
import com.mingseal.communicate.SocketInputThread;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.dao.WeldWorkDao;
import com.mingseal.data.param.PointConfigParam;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;
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
 * @description 焊锡作业点
 */
public class WeldWorkActivity extends AutoLayoutActivity implements OnClickListener {

    private final static String TAG = "WeldWorkActivity";
    private TextView tv_title;// 标题栏的标题

    /**
     * @Fields et_yure: 预送锡量
     */
    private EditText et_yure;
    /**
     * @Fields et_sendSnSumFir: 一次送锡量
     */
    private EditText et_sendSnSumFir;
    /**
     * @Fields et_sendSnSumSec: 二次送锡量
     */
    private EditText et_sendSnSumSec;

    private ToggleButton switch_isSn;// 是否出胶
    private ToggleButton[] isGluePort;// 点胶口

    private RelativeLayout rl_back;// 返回上级的按钮

    private List<PointWeldWorkParam> weldWorkLists;// 保存的方案,用来维护从数据库中读出来的方案列表的编号
    private PointWeldWorkParam weldWork;

    private Point point;// 从taskActivity中传值传过来的point
    private Intent intent;
    private WeldWorkDao weldWorkDao;

    private int param_id = 1;//
    private int mFlag;// 0代表增加数据，1代表更新数据
    private int mType;// 1表示要更新数据
    private int preHeatTime = 0;
    private float sendSnSumFir = 0;
    private float sendSnSumSec = 0;

    /**
     * @Fields isNull: 判断数据库中是否有次方案，默认没有
     */
    private boolean flag = false;
    PopupListView popupListView;
    ArrayList<PopupView> popupViews;
    int actionBarHeight;
    int p = 0;
    View extendView;
    private TextView mMorenTextView;
    /**
     * 当前任务号
     */
    private int currentTaskNum;
    private int currentClickNum;// 当前点击的序号
    // Content View Elements

    private RelativeLayout rl_moren;
    private ImageView iv_add;
    private RelativeLayout rl_save;
    private ImageView iv_moren;
    private int dianjiao;
    private int defaultNum = 1;// 默认号
    private boolean isOk;
    private boolean isExist = false;// 是否存在
    private boolean firstExist = false;// 是否存在
    private int mIndex;// 对应方案号
    private HashMap<Integer, PointWeldWorkParam> update_id;// 修改的方案号集合

    private TextView tv_dianjiao;
    private TextView extend_ms;
    private TextView extend_isSn;
    private TextView extend_ms2;
    private TextView tv_taiqidaodu;
    private TextView extend_mm;
    private TextView extend_default;
    private TextView extend_save;
    private TextView tv_tingjiao;
    private TextView mFanganliebiao;
    private RevHandler handler;
    private static UserApplication userApplication;
    private EditText et_sendSnSpeedFir;
    private EditText et_sendSnSpeedSec;
    private EditText et_stopSnStimeSec;
    private TextView tv_y_xiecha;
    private TextView tv_z_xiecha;
    private TextView tv_speed_xiecha;
    private TextView extend_mm2;
    private TextView extend_mm3;
    private TextView extend_mms;
    // End Of Content View Elements

    private ViewStub stub_glue;
    private  int Activity_Init_View=1;
    private ImageView iv_loading;
    private EditText et_sendSnSpeedThird;
    private TextView extend_mms2;
    private TextView tv_sendSnSpeedThird;
    private EditText et_sendSnSumThird;
    private TextView extend_mm4;
    private EditText et_stopSnTimeThird;
    private TextView tv_stopSnTimeThird;
    private TextView extend_ms3;
    private EditText et_sendSnSpeedFourth;
    private TextView tv_sendSnSpeedFourth;
    private TextView extend_mms3;
    private EditText et_sendSnSumFourth;
    private TextView tv_sendSnSumFourth;
    private TextView extend_mm5;
    private EditText et_stopSnTimeFourth;
    private TextView tv_stopSnTimeFourth;
    private TextView extend_ms4;
    private EditText et_dipDistance;
    private TextView tv_dipDistance;
    private TextView extend_mm6;
    private EditText et_upHeight;
    private TextView tv_upHeight;
    private TextView extend_mm7;
    private TextView extend_isSus;
    private ToggleButton switch_isSus;
    private TextView extend_isPause;
    private ToggleButton switch_isPause;
    private float sendSnSumThird;
    private float sendSnSumFourth;
    private int dipDistance;
    private int upHeight;
    private int sendSnSpeedFir;
    private int sendSnSpeedSec;
    private int stopSnStimeSec;
    private int stopSnTimeThird;
    private int stopSnTimeFourth;
    private int sendSnSpeedThird;
    private int sendSnSpeedFourth;
    private static final int DECIMAL_DIGITS = 1;//小数的位数
    private String taskname;
    private TextView tv_sendSnSumThird;
    // Content View Elements
    private TextView mTitle_yure;
    private TextView mTitle_et_yure;
    private TextView mActivity_ms;
    private TextView mActivity_fenghao;
    private TextView mTitle_sendSnSpeedFir;
    private TextView mTitle_et_sendSnSpeedFir;
    private TextView mActivity_second_ms;
    private TextView mActivity_second_fenghao;
    private TextView mTitle_sendSnSumFir;
    private TextView mTitle_et_sendSnSumFir;
    private TextView mActivity_mm;
    private TextView mActivity_third_fenghao;
    private TextView mTitle_sendSnSpeedSec;
    private TextView mTitle_et_sendSnSpeedSec;
    private TextView mActivity_four_mm;
    private TextView mTitle_sendSnSumSec;
    private TextView mTitle_et_sendSnSumSec;
    private TextView mActivity_four_fenghao;
    private TextView mTitle_stopSnStimeSec;
    private TextView mTitle_et_stopSnStimeSec;
    private TextView mActivity_five_fenghao;
    private TextView mTitle_sendSnSpeedThird;
    private TextView mTitle_et_sendSnSpeedThird;
    private TextView mActivity_six_fenghao;
    private TextView mTitle_sendSnSumThird;
    private TextView mTitle_et_sendSnSumThird;
    private TextView mActivity_five_mm;
    private TextView mActivity_seven_fenghao;
    private TextView mTitle_stopSnTimeThird;
    private TextView mTitle_et_stopSnTimeThird;
    private TextView mActivity_mm_s;
    private TextView mTitle_sendSnSpeedFourth;
    private TextView mTitle_et_sendSnSpeedFourth;
    private TextView mActivity_four_mms;
    private TextView mActivity_eight_fenghao;
    private TextView mTitle_sendSnSumFourth;
    private TextView mTitle_et_sendSnSumFourth;
    private TextView mActivity_six_mm;
    private TextView mActivity_nine_fenghao;
    private TextView mTitle_stopSnTimeFourth;
    private TextView mTitle_et_stopSnTimeFourth;
    private TextView mActivity_six_ms;
    private TextView mActivity_ten_fenghao;
    private TextView mTitle_dipDistance;
    private TextView mTitle_et_dipDistance;
    private TextView mActivity_seven_mm;
    private TextView mActivity_eleven_fenghao;
    private TextView mTitle_upHeight;
    private TextView mTitle_et_upHeight;
    private TextView mActivity_eight_mm;
    private TextView mTitle_xiecha_angle;
    private TextView mTitle_et_xiecha_angle;
    private TextView mActivity_jiaodu;
    private TextView mActivity_twelve_fenghao;
    private TextView mTitle_isSn;
    private TextView mTitle_et_isSn;
    private TextView mTitle_isSus;
    private TextView mTitle_et_isSus;
    private TextView mTitle_isPause;
    private TextView mTitle_et_isPause;
    private ImageView mIv_selected;
    private TextView extend_dipDistance_angle;
    private TextView extend_jiaodu;
    private EditText et_dipDistance_angle;
    private int dipDistance_angle;
    private TextView mActivity_nine_mm;
    private TextView mActivity_third_ms;
    private TextView mActivity_third_mms;

    // End Of Content View Elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_glue_alone);
        userApplication = (UserApplication) getApplication();
        handler = new RevHandler();
        update_id = new HashMap<>();
        intent = getIntent();
        // point携带的参数方案[_id=1, pointType=POINT_GLUE_FACE_START]
        point = intent
                .getParcelableExtra(MyPopWindowClickListener.POPWINDOW_KEY);
        mFlag = intent.getIntExtra(MyPopWindowClickListener.FLAG_KEY, 0);
        mType = intent.getIntExtra(MyPopWindowClickListener.TYPE_KEY, 0);
        taskname=intent.getStringExtra("taskname");
        L.d("taskname"+taskname);
        Log.d(TAG, point.toString() + " FLAG:" + mFlag);
        defaultNum = SharePreferenceUtils.getParamNumberFromPref(
                WeldWorkActivity.this,
                SettingParam.DefaultNum.ParamGlueAloneNumber);
        weldWorkDao = new WeldWorkDao(WeldWorkActivity.this);
        weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
        if (weldWorkLists == null || weldWorkLists.isEmpty()) {
            weldWork = new PointWeldWorkParam();
            // 插入主键id
            weldWork.set_id(param_id);
            weldWorkDao.insertWeldWork(weldWork,taskname);
        }
        weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
        popupViews = new ArrayList<>();
        initPicker();
    }

    /**
     * @Title initPicker
     * @Description 初始化视图界面，创建10个popupView
     * @author wj
     */
    private void initPicker() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(55));
        mFanganliebiao = (TextView) findViewById(R.id.fanganliebiao);
        mFanganliebiao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(50));

        tv_title.setText(getResources().getString(R.string.activity_glue_alone));
        mMorenTextView = (TextView) findViewById(R.id.morenfangan);
        mMorenTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(30));
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        mMorenTextView.setText("当前默认方案号(" + defaultNum + ")");
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        iv_loading.setVisibility(View.VISIBLE);
        stub_glue = (ViewStub) findViewById(R.id.stub_glue);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = Activity_Init_View;
                handler.sendMessage(msg);
            }
        }, 50);// 延迟1毫秒,然后加载
    }

    protected void setTitleInfos(List<PointWeldWorkParam> weldWorkLists,
                                 View view, int p) {
        mTitle_yure = (TextView) view.findViewById(R.id.title_yure);
        mTitle_et_yure = (TextView) view.findViewById(R.id.title_et_yure);
        mActivity_ms = (TextView) view.findViewById(R.id.activity_ms);
        mActivity_fenghao = (TextView) view.findViewById(R.id.activity_fenghao);
        mTitle_sendSnSpeedFir = (TextView) view.findViewById(R.id.title_sendSnSpeedFir);
        mTitle_et_sendSnSpeedFir = (TextView) view.findViewById(R.id.title_et_sendSnSpeedFir);
        mActivity_second_ms = (TextView) view.findViewById(R.id.activity_second_ms);
        mActivity_second_fenghao = (TextView) view.findViewById(R.id.activity_second_fenghao);
        mTitle_sendSnSumFir = (TextView) view.findViewById(R.id.title_sendSnSumFir);
        mTitle_et_sendSnSumFir = (TextView) view.findViewById(R.id.title_et_sendSnSumFir);
        mActivity_mm = (TextView) view.findViewById(R.id.activity_mm);
        mActivity_third_fenghao = (TextView) view.findViewById(R.id.activity_third_fenghao);
        mTitle_sendSnSpeedSec = (TextView) view.findViewById(R.id.title_sendSnSpeedSec);
        mTitle_et_sendSnSpeedSec = (TextView) view.findViewById(R.id.title_et_sendSnSpeedSec);
        mActivity_four_mm = (TextView) view.findViewById(R.id.activity_four_mm);
        mActivity_third_mms = (TextView) view.findViewById(R.id.activity_third_mms);
        mActivity_third_ms = (TextView) view.findViewById(R.id.activity_third_ms);
        mActivity_nine_mm = (TextView) view.findViewById(R.id.activity_nine_mm);
        mTitle_sendSnSumSec = (TextView) view.findViewById(R.id.title_sendSnSumSec);
        mTitle_et_sendSnSumSec = (TextView) view.findViewById(R.id.title_et_sendSnSumSec);
        mActivity_four_fenghao = (TextView) view.findViewById(R.id.activity_four_fenghao);
        mTitle_stopSnStimeSec = (TextView) view.findViewById(R.id.title_stopSnStimeSec);
        mTitle_et_stopSnStimeSec = (TextView) view.findViewById(R.id.title_et_stopSnStimeSec);
        mActivity_five_fenghao = (TextView) view.findViewById(R.id.activity_five_fenghao);
        mTitle_sendSnSpeedThird = (TextView) view.findViewById(R.id.title_sendSnSpeedThird);
        mTitle_et_sendSnSpeedThird = (TextView) view.findViewById(R.id.title_et_sendSnSpeedThird);
        mActivity_six_fenghao = (TextView) view.findViewById(R.id.activity_six_fenghao);
        mTitle_sendSnSumThird = (TextView) view.findViewById(R.id.title_sendSnSumThird);
        mTitle_et_sendSnSumThird = (TextView) view.findViewById(R.id.title_et_sendSnSumThird);
        mActivity_five_mm = (TextView) view.findViewById(R.id.activity_five_mm);
        mActivity_seven_fenghao = (TextView) view.findViewById(R.id.activity_seven_fenghao);
        mTitle_stopSnTimeThird = (TextView) view.findViewById(R.id.title_stopSnTimeThird);
        mTitle_et_stopSnTimeThird = (TextView) view.findViewById(R.id.title_et_stopSnTimeThird);
        mActivity_mm_s = (TextView) view.findViewById(R.id.activity_mm_s);
        mTitle_sendSnSpeedFourth = (TextView) view.findViewById(R.id.title_sendSnSpeedFourth);
        mTitle_et_sendSnSpeedFourth = (TextView) view.findViewById(R.id.title_et_sendSnSpeedFourth);
        mActivity_four_mms = (TextView) view.findViewById(R.id.activity_four_mms);
        mActivity_eight_fenghao = (TextView) view.findViewById(R.id.activity_eight_fenghao);
        mTitle_sendSnSumFourth = (TextView) view.findViewById(R.id.title_sendSnSumFourth);
        mTitle_et_sendSnSumFourth = (TextView) view.findViewById(R.id.title_et_sendSnSumFourth);
        mActivity_six_mm = (TextView) view.findViewById(R.id.activity_six_mm);
        mActivity_nine_fenghao = (TextView) view.findViewById(R.id.activity_nine_fenghao);
        mTitle_stopSnTimeFourth = (TextView) view.findViewById(R.id.title_stopSnTimeFourth);
        mTitle_et_stopSnTimeFourth = (TextView) view.findViewById(R.id.title_et_stopSnTimeFourth);
        mActivity_six_ms = (TextView) view.findViewById(R.id.activity_six_ms);
        mActivity_ten_fenghao = (TextView) view.findViewById(R.id.activity_ten_fenghao);
        mTitle_dipDistance = (TextView) view.findViewById(R.id.title_dipDistance);
        mTitle_et_dipDistance = (TextView) view.findViewById(R.id.title_et_dipDistance);
        mActivity_seven_mm = (TextView) view.findViewById(R.id.activity_seven_mm);
        mActivity_eleven_fenghao = (TextView) view.findViewById(R.id.activity_eleven_fenghao);
        mTitle_upHeight = (TextView) view.findViewById(R.id.title_upHeight);
        mTitle_et_upHeight = (TextView) view.findViewById(R.id.title_et_upHeight);
        mActivity_eight_mm = (TextView) view.findViewById(R.id.activity_eight_mm);
        mTitle_xiecha_angle = (TextView) view.findViewById(R.id.title_xiecha_angle);
        mTitle_et_xiecha_angle = (TextView) view.findViewById(R.id.title_et_xiecha_angle);
        mActivity_jiaodu = (TextView) view.findViewById(R.id.activity_jiaodu);
        mActivity_twelve_fenghao = (TextView) view.findViewById(R.id.activity_twelve_fenghao);
        mTitle_isSn = (TextView) view.findViewById(R.id.title_isSn);
        mTitle_et_isSn = (TextView) view.findViewById(R.id.title_et_isSn);
        mTitle_isSus = (TextView) view.findViewById(R.id.title_isSus);
        mTitle_et_isSus = (TextView) view.findViewById(R.id.title_et_isSus);
        mTitle_isPause = (TextView) view.findViewById(R.id.title_isPause);
        mTitle_et_isPause = (TextView) view.findViewById(R.id.title_et_isPause);
        /*===================  end  ===================*/
        for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
            if (p == pointWeldWorkParam.get_id()) {
                /*===================== begin =====================*/
                mTitle_yure.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSpeedFir.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSumFir.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSpeedSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSumSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_stopSnStimeSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSpeedThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSumThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_stopSnTimeThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSpeedFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_sendSnSumFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_stopSnTimeFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_dipDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_xiecha_angle.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_isSn.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_isSus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_isPause.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_yure.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSpeedFir.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSumFir.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSpeedSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSumSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_stopSnStimeSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSpeedThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSumThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_stopSnTimeThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSpeedFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_sendSnSumFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_stopSnTimeFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_dipDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_xiecha_angle.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_isSn.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_isSus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mTitle_et_isPause.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_mm_s.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_eight_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_second_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_four_mms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_seven_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_six_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_six_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_four_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_third_mms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_third_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_nine_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_five_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_second_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_third_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_four_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_five_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_six_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_seven_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_eight_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_nine_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_ten_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_eleven_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_twelve_fenghao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                mActivity_jiaodu.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(20));
                /*=====================  end =====================*/

                mTitle_yure.setText(getResources().getString(R.string.activity_weld_work_yureshijian) + " ");
                mTitle_sendSnSpeedFir.setText(getResources().getString(R.string.activity_weld_work_sendSnSpeedFir) + " ");
                mTitle_sendSnSumFir.setText(getResources().getString(R.string.activity_weld_work_sendSnSumFir) + " ");
                mTitle_sendSnSpeedSec.setText(getResources().getString(R.string.activity_weld_work_sendSnSpeedSec) + " ");
                mTitle_sendSnSumSec.setText(getResources().getString(R.string.activity_weld_work_sendSnSumSec) + " ");
                mTitle_stopSnStimeSec.setText(getResources().getString(R.string.activity_weld_work_stopSnStimeSec) + " ");
                mTitle_sendSnSpeedThird.setText(getResources().getString(R.string.activity_weld_work_sendSnSpeedThird) + " ");
                mTitle_sendSnSumThird.setText(getResources().getString(R.string.activity_weld_work_sendSnSumThird) + " ");
                mTitle_stopSnTimeThird.setText(getResources().getString(R.string.activity_weld_work_stopSnTimeThird) + " ");
                mTitle_sendSnSpeedFourth.setText(getResources().getString(R.string.activity_weld_work_sendSnSpeedFourth) + " ");
                mTitle_sendSnSumFourth.setText(getResources().getString(R.string.activity_weld_work_sendSnSumFourth) + " ");
                mTitle_stopSnTimeFourth.setText(getResources().getString(R.string.activity_weld_work_stopSnTimeFourth) + " ");
                mTitle_dipDistance.setText(getResources().getString(R.string.activity_weld_work_dipDistance) + " ");
                mTitle_upHeight.setText(getResources().getString(R.string.activity_weld_work_upHeight) + " ");
                mTitle_xiecha_angle.setText(getResources().getString(R.string.activity_weld_work_xiecha_angle) + " ");
                mTitle_isSn.setText(getResources().getString(R.string.activity_glue_alone_isSn) + " ");
                mTitle_isSus.setText(getResources().getString(R.string.activity_glue_alone_isSus) + " ");
                mTitle_isPause.setText(getResources().getString(R.string.activity_glue_alone_isPause) + " ");

                mActivity_ms.setText(getResources().getString(R.string.activity_ms));
                mActivity_mm_s.setText(getResources().getString(R.string.activity_ms));
                mActivity_eight_mm.setText(getResources().getString(R.string.activity_mm));
                mActivity_second_ms.setText(getResources().getString(R.string.activity_ms));
                mActivity_four_mms.setText(getResources().getString(R.string.activity_mm_s));
                mActivity_mm.setText(getResources().getString(R.string.activity_mm));
                mActivity_seven_mm.setText(getResources().getString(R.string.activity_mm));
                mActivity_six_mm.setText(getResources().getString(R.string.activity_mm));
                mActivity_six_ms.setText(getResources().getString(R.string.activity_ms));
                mActivity_four_mm.setText(getResources().getString(R.string.activity_mm_s));
                mActivity_third_mms.setText(getResources().getString(R.string.activity_mm_s));
                mActivity_third_ms.setText(getResources().getString(R.string.activity_ms));
                mActivity_nine_mm.setText(getResources().getString(R.string.activity_mm));
                mActivity_five_mm.setText(getResources().getString(R.string.activity_mm));
                mActivity_jiaodu.setText(getResources().getString(R.string.activity_weld_work_angle));
                mActivity_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_second_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_third_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_four_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_five_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_six_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_seven_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_eight_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_nine_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_ten_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_eleven_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");
                mActivity_twelve_fenghao.setText(getResources().getString(R.string.activity_fenghao) + " ");

                mTitle_et_yure.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_yure.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSpeedFir.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSpeedFir.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSumFir.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSumFir.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSpeedSec.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSpeedSec.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSumSec.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSumSec.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_stopSnStimeSec.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_stopSnStimeSec.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSpeedThird.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSpeedThird.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSumThird.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSumThird.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_stopSnTimeThird.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_stopSnTimeThird.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSpeedFourth.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSpeedFourth.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_sendSnSumFourth.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_sendSnSumFourth.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_stopSnTimeFourth.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_stopSnTimeFourth.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_dipDistance.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_dipDistance.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_upHeight.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_upHeight.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_xiecha_angle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_xiecha_angle.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_isSn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_isSn.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_isSus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_isSus.getPaint().setAntiAlias(true); // 抗锯齿
                mTitle_et_isPause.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
                mTitle_et_isPause.getPaint().setAntiAlias(true); // 抗锯齿


                mTitle_et_yure.setText(pointWeldWorkParam.getPreHeatTime() + "");
                mTitle_et_sendSnSpeedFir.setText(pointWeldWorkParam.getSendSnSpeedFir() + "");
                mTitle_et_sendSnSumFir.setText((float)pointWeldWorkParam.getSendSnSumFir()/10 + "");
                mTitle_et_sendSnSpeedSec.setText(pointWeldWorkParam.getSendSnSpeedSec()+"");
                mTitle_et_sendSnSumSec.setText((float)pointWeldWorkParam.getSendSnSumSec()/10+"");
                mTitle_et_stopSnStimeSec.setText(pointWeldWorkParam.getStopSnStimeSec()+"");
                mTitle_et_sendSnSpeedThird.setText(pointWeldWorkParam.getSendSnSpeedThird()+"");
                mTitle_et_sendSnSumThird.setText((float)pointWeldWorkParam.getSendSnSumThird()/10+"");
                mTitle_et_stopSnTimeThird.setText(pointWeldWorkParam.getStopSnTimeThird()+"");
                mTitle_et_sendSnSpeedFourth.setText(pointWeldWorkParam.getSendSnSpeedFourth()+"");
                mTitle_et_sendSnSumFourth.setText((float)pointWeldWorkParam.getSendSnSumFourth()/10+"");
                mTitle_et_stopSnTimeFourth.setText(pointWeldWorkParam.getStopSnTimeFourth()+"");
                mTitle_et_dipDistance.setText(pointWeldWorkParam.getDipDistance()+"");
                mTitle_et_upHeight.setText(pointWeldWorkParam.getUpHeight()+"");
                mTitle_et_xiecha_angle.setText(pointWeldWorkParam.getDipDistance_angle()+"");
                if (pointWeldWorkParam.isSn()) {
                    mTitle_et_isSn.setText("是");
                } else {
                    mTitle_et_isSn.setText("否");
                }
                if (pointWeldWorkParam.isSus()) {
                    mTitle_et_isSus.setText("是");
                } else {
                    mTitle_et_isSus.setText("否");
                }
                  if (pointWeldWorkParam.isPause()) {
                      mTitle_et_isPause.setText("是");
                } else {
                      mTitle_et_isPause.setText("否");
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
        weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
            list.add(pointWeldWorkParam.get_id());
        }
        System.out.println("存放主键id的集合---->" + list);
        System.out.println("当前选择的方案号---->" + currentTaskNum);
        System.out.println("list是否存在------------》" + list.contains(currentTaskNum));
        if (list.contains(currentTaskNum)) {
            // 已经保存在数据库中的数据
            for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                if (currentTaskNum == pointWeldWorkParam.get_id()) {
                    View extendView = popupListView.getItemViews()
                            .get(currentClickNum).getExtendView();
                    initView(extendView);
                    UpdateInfos(pointWeldWorkParam);
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
     * @param pointWeldWorkParam
     * @Title UpdateInfos
     * @Description 更新extendView数据（保存的数据）
     * @author wj
     */
    private void UpdateInfos(PointWeldWorkParam pointWeldWorkParam) {
        if (pointWeldWorkParam == null) {
            et_yure.setText("");
            et_sendSnSumFir.setText("");
            et_sendSnSumSec.setText("");
            et_sendSnSumThird.setText("");
            et_sendSnSumFourth.setText("");
            et_dipDistance.setText("");
            et_upHeight.setText("");
            et_sendSnSpeedFir.setText("");
            et_sendSnSpeedSec.setText("");
            et_stopSnStimeSec.setText("");
            et_stopSnTimeThird.setText("");
            et_stopSnTimeFourth.setText("");
            et_sendSnSpeedThird.setText("");
            et_sendSnSpeedFourth.setText("");
        } else {
            et_yure.setText(pointWeldWorkParam.getPreHeatTime() + "");
            et_sendSnSumFir.setText((float)pointWeldWorkParam.getSendSnSumFir()/10+ "");
            et_sendSnSumSec.setText((float)pointWeldWorkParam.getSendSnSumSec()/10 + "");
            et_sendSnSumThird.setText((float)pointWeldWorkParam.getSendSnSumThird()/10 + "");
            et_sendSnSumFourth.setText((float)pointWeldWorkParam.getSendSnSumFourth()/10 + "");
            et_dipDistance.setText(pointWeldWorkParam.getDipDistance() + "");
            et_upHeight.setText(pointWeldWorkParam.getUpHeight() + "");
            et_sendSnSpeedFir.setText(pointWeldWorkParam.getSendSnSpeedFir() + "");
            et_sendSnSpeedSec.setText(pointWeldWorkParam.getSendSnSpeedSec() + "");
            et_stopSnStimeSec.setText(pointWeldWorkParam.getStopSnStimeSec() + "");
            et_stopSnTimeThird.setText(pointWeldWorkParam.getStopSnTimeThird() + "");
            et_stopSnTimeFourth.setText(pointWeldWorkParam.getStopSnTimeFourth() + "");
            et_sendSnSpeedThird.setText(pointWeldWorkParam.getSendSnSpeedThird() + "");
            et_sendSnSpeedFourth.setText(pointWeldWorkParam.getSendSnSpeedFourth() + "");
            et_dipDistance_angle.setText(pointWeldWorkParam.getDipDistance_angle()+"");

            switch_isSn.setChecked(pointWeldWorkParam.isSn());
            switch_isSus.setChecked(pointWeldWorkParam.isSus());
            switch_isPause.setChecked(pointWeldWorkParam.isPause());

        }
    }

    /**
     * @param extendView
     * @Title initView
     * @Description 初始化当前extendView视图
     * @author wj
     */
    private void initView(View extendView) {
        et_yure = (EditText) extendView.findViewById(R.id.et_yure);
        switch_isSn = (ToggleButton) extendView
                .findViewById(R.id.switch_isSn);
        et_sendSnSumFir = (EditText) extendView
                .findViewById(R.id.et_sendSnSumFir);
        et_dipDistance_angle = (EditText) extendView.findViewById(R.id.et_dipDistance_angle);
        switch_isSus = (ToggleButton) extendView.findViewById(R.id.switch_isSus);
        switch_isPause = (ToggleButton) extendView.findViewById(R.id.switch_isPause);
        et_sendSnSumSec = (EditText) extendView.findViewById(R.id.et_sendSnSumSec);

        et_sendSnSpeedFir = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFir);
        et_sendSnSpeedSec = (EditText) extendView.findViewById(R.id.et_sendSnSpeedSec);
        et_stopSnStimeSec = (EditText) extendView.findViewById(R.id.et_stopSnStimeSec);
        et_stopSnTimeThird = (EditText) extendView.findViewById(R.id.et_stopSnTimeThird);
        et_stopSnTimeFourth = (EditText) extendView.findViewById(R.id.et_stopSnTimeFourth);
        et_sendSnSpeedThird = (EditText) extendView.findViewById(R.id.et_sendSnSpeedThird);
        et_sendSnSpeedFourth = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFourth);
        et_sendSnSumThird = (EditText) extendView.findViewById(R.id.et_sendSnSumThird);
        et_sendSnSumFourth = (EditText) extendView.findViewById(R.id.et_sendSnSumFourth);
        et_dipDistance = (EditText) extendView.findViewById(R.id.et_dipDistance);
        et_upHeight = (EditText) extendView.findViewById(R.id.et_upHeight);
        tv_y_xiecha = (TextView) extendView.findViewById(R.id.tv_y_xiecha);
        tv_z_xiecha = (TextView) extendView.findViewById(R.id.tv_z_xiecha);
        tv_speed_xiecha = (TextView) extendView.findViewById(R.id.tv_speed_xiecha);
        tv_sendSnSpeedThird = (TextView) extendView.findViewById(R.id.tv_sendSnSpeedThird);
        tv_sendSnSpeedFourth = (TextView) extendView.findViewById(R.id.tv_sendSnSpeedFourth);
        tv_stopSnTimeThird = (TextView) extendView.findViewById(R.id.tv_stopSnTimeThird);
        tv_sendSnSumFourth = (TextView) extendView.findViewById(R.id.tv_sendSnSumFourth);
        tv_sendSnSumThird = (TextView) extendView.findViewById(R.id.tv_sendSnSumThird);
        tv_stopSnTimeFourth = (TextView) extendView.findViewById(R.id.tv_stopSnTimeFourth);
        tv_dipDistance = (TextView) extendView.findViewById(R.id.tv_dipDistance);
        tv_upHeight = (TextView) extendView.findViewById(R.id.tv_upHeight);

        extend_mm2 = (TextView) extendView.findViewById(R.id.extend_mm2);
        extend_mm3 = (TextView) extendView.findViewById(R.id.extend_mm3);
        extend_mms = (TextView) extendView.findViewById(R.id.extend_mms);
        extend_mms2 = (TextView) extendView.findViewById(R.id.extend_mms2);
        extend_mm4 = (TextView) extendView.findViewById(R.id.extend_mm4);
        extend_ms3 = (TextView) extendView.findViewById(R.id.extend_ms3);
        extend_mms3 = (TextView) extendView.findViewById(R.id.extend_mms3);
        extend_mm5 = (TextView) extendView.findViewById(R.id.extend_mm5);
        extend_ms4 = (TextView) extendView.findViewById(R.id.extend_ms4);
        extend_mm6 = (TextView) extendView.findViewById(R.id.extend_mm6);
        extend_mm7 = (TextView) extendView.findViewById(R.id.extend_mm7);

        tv_dianjiao = (TextView) extendView.findViewById(R.id.tv_dianjiao);
        tv_tingjiao = (TextView) extendView.findViewById(R.id.tv_tingjiao);
        extend_ms = (TextView) extendView.findViewById(R.id.extend_ms);
        extend_isSn = (TextView) extendView.findViewById(R.id.extend_isSn);
        extend_ms2 = (TextView) extendView.findViewById(R.id.extend_ms2);
        extend_dipDistance_angle = (TextView) extendView.findViewById(R.id.extend_dipDistance_angle);
        extend_jiaodu = (TextView) extendView.findViewById(R.id.extend_jiaodu);
        extend_isSus = (TextView) extendView.findViewById(R.id.extend_isSus);
        extend_isPause = (TextView) extendView.findViewById(R.id.extend_isPause);
        tv_taiqidaodu = (TextView) extendView.findViewById(R.id.tv_taiqidaodu);
        extend_mm = (TextView) extendView.findViewById(R.id.extend_mm);
        extend_default = (TextView) extendView.findViewById(R.id.extend_default);
        extend_save = (TextView) extendView.findViewById(R.id.extend_save);
        /*===================== begin =====================*/
        et_yure.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSumFir.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSumSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSumThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSumFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_dipDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_dipDistance_angle.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_dianjiao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_tingjiao.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_upHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_ms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_isSn.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_ms2.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_dipDistance_angle.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_jiaodu.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_isSus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_isPause.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_taiqidaodu.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_dipDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_default.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSpeedFir.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSpeedSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_stopSnStimeSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_stopSnTimeThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_stopSnTimeFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSpeedThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        et_sendSnSpeedFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_y_xiecha.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_z_xiecha.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_speed_xiecha.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_sendSnSpeedThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_sendSnSpeedFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_stopSnTimeThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_sendSnSumFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_sendSnSumThird.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        tv_stopSnTimeFourth.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm2.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm3.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mms.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mms2.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm4.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_ms3.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mms3.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm5.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_ms4.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm6.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        extend_mm7.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
        /*=====================  end =====================*/
    }

    /**
     * @Title save
     * @Description 保存信息到PointGlueAloneParam的一个对象中，并更新数据库数据
     * @author wj
     */
    protected void save() {
        View extendView = popupListView.getItemViews().get(currentClickNum)
                .getExtendView();
        weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
        ArrayList<Integer> list = new ArrayList<>();
        for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
            list.add(pointWeldWorkParam.get_id());
        }
        // 判空
        isOk = isEditClean(extendView);
        if (isOk) {

            PointWeldWorkParam upglueAlone = getGlueAlone(extendView);
            if (weldWorkLists.contains(upglueAlone)) {
                // 默认已经存在的方案但是不能创建方案只能改变默认方案号
                if (list.contains(currentTaskNum)) {
                    isExist = true;
                }
                // 保存的方案已经存在但不是当前编辑的方案
                if (currentTaskNum != weldWorkLists.get(
                        weldWorkLists.indexOf(upglueAlone)).get_id()) {
                    ToastUtil.displayPromptInfo(WeldWorkActivity.this,
                            getResources()
                                    .getString(R.string.task_is_exist_yes));
                }
            } else {

                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                    if (currentTaskNum == pointWeldWorkParam.get_id()) {// 说明之前插入过
                        flag = true;
                    }
                }
                if (flag) {
                    // 更新数据
                    int rowid = weldWorkDao.upDateGlueAlone(upglueAlone,taskname);
                    // System.out.println("影响的行数"+rowid);
                    update_id.put(upglueAlone.get_id(), upglueAlone);
                    // mPMap.map.put(upglueAlone.get_id(), upglueAlone);
                    System.out.println("修改的方案号为：" + upglueAlone.get_id());
                    // System.out.println(weldWorkDao.getPointGlueAloneParamById(currentTaskNum).toString());
                } else {
                    // 插入一条数据
                    long rowid = weldWorkDao.insertWeldWork(upglueAlone,taskname);
                    firstExist = true;
                    weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
                    Log.i(TAG, "保存之后新方案-->" + weldWorkLists.toString());
                    ToastUtil.displayPromptInfo(WeldWorkActivity.this,
                            getResources().getString(R.string.save_success));
                    list.clear();
                    for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                        list.add(pointWeldWorkParam.get_id());
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
        weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
        // popupListView->pupupview->title
        for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {

            if (currentTaskNum == pointWeldWorkParam.get_id()) {
                // 需要设置两个view，因为view内容相同但是parent不同
                View titleViewItem = popupListView.getItemViews()
                        .get(currentClickNum).getPopupView();
                View titleViewExtend = popupListView.getItemViews()
                        .get(currentClickNum).getExtendPopupView();
                setTitleInfos(weldWorkLists, titleViewItem, currentTaskNum);
                setTitleInfos(weldWorkLists, titleViewExtend, currentTaskNum);
            }
        }
    }

    /**
     * @param extendView 具体内容
     * @param
     * @return
     * @Title getGlueAlone
     * @Description 将页面上显示的数据保存到PointGlueAloneParam的一个对象中
     * @author wj
     */
    private PointWeldWorkParam getGlueAlone(View extendView) {
        weldWork = new PointWeldWorkParam();
        et_yure = (EditText) extendView.findViewById(R.id.et_yure);
        switch_isSn = (ToggleButton) extendView.findViewById(R.id.switch_isSn);
        et_sendSnSumFir = (EditText) extendView.findViewById(R.id.et_sendSnSumFir);
        et_dipDistance_angle = (EditText) extendView.findViewById(R.id.et_dipDistance_angle);
        switch_isSus = (ToggleButton) extendView.findViewById(R.id.switch_isSus);
        switch_isPause = (ToggleButton) extendView.findViewById(R.id.switch_isPause);
        et_sendSnSumSec = (EditText) extendView.findViewById(R.id.et_sendSnSumSec);
        et_sendSnSumThird = (EditText) extendView.findViewById(R.id.et_sendSnSumThird);
        et_sendSnSumFourth = (EditText) extendView.findViewById(R.id.et_sendSnSumFourth);
        et_dipDistance = (EditText) extendView.findViewById(R.id.et_dipDistance);
        et_upHeight = (EditText) extendView.findViewById(R.id.et_upHeight);
        et_sendSnSpeedFir = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFir);
        et_sendSnSpeedSec = (EditText) extendView.findViewById(R.id.et_sendSnSpeedSec);
        et_stopSnStimeSec = (EditText) extendView.findViewById(R.id.et_stopSnStimeSec);
        et_stopSnTimeThird = (EditText) extendView.findViewById(R.id.et_stopSnTimeThird);
        et_stopSnTimeFourth = (EditText) extendView.findViewById(R.id.et_stopSnTimeFourth);
        et_sendSnSpeedThird = (EditText) extendView.findViewById(R.id.et_sendSnSpeedThird);
        et_sendSnSpeedFourth = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFourth);

        try {
            preHeatTime = Integer.parseInt(et_yure.getText().toString());
        } catch (NumberFormatException e) {
            preHeatTime = 0;
        }
        try {
            sendSnSumFir = Float.parseFloat(et_sendSnSumFir.getText()
                    .toString());
        } catch (NumberFormatException e) {
            sendSnSumFir = 0;
        }
        try {
            sendSnSumSec = Float.parseFloat(et_sendSnSumSec.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSumSec = 0;
        }

        try {
            sendSnSumThird = Float.parseFloat(et_sendSnSumThird.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSumThird = 0;
        }
        try {
            sendSnSumFourth = Float.parseFloat(et_sendSnSumFourth.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSumFourth = 0;
        }
        try {
            dipDistance = Integer.parseInt(et_dipDistance.getText().toString());
        } catch (NumberFormatException e) {
            dipDistance = 0;
        }
        try {
            upHeight = Integer.parseInt(et_upHeight.getText().toString());
        } catch (NumberFormatException e) {
            upHeight = 0;
        }
        try {
            sendSnSpeedFir = Integer.parseInt(et_sendSnSpeedFir.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSpeedFir=0;
        }
        try {
            sendSnSpeedSec = Integer.parseInt(et_sendSnSpeedSec.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSpeedSec=0;
        }
        try {
            stopSnStimeSec = Integer.parseInt(et_stopSnStimeSec.getText().toString());
        } catch (NumberFormatException e) {
            stopSnStimeSec=0;
        }
        try {
            stopSnTimeThird = Integer.parseInt(et_stopSnTimeThird.getText().toString());
        } catch (NumberFormatException e) {
            stopSnTimeThird=0;
        }
        try {
            stopSnTimeFourth = Integer.parseInt(et_stopSnTimeFourth.getText().toString());
        } catch (NumberFormatException e) {
            stopSnTimeFourth=0;
        }
        try {
            sendSnSpeedThird = Integer.parseInt(et_sendSnSpeedThird.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSpeedThird=0;
        }
        try {
            sendSnSpeedFourth = Integer.parseInt(et_sendSnSpeedFourth.getText().toString());
        } catch (NumberFormatException e) {
            sendSnSpeedFourth=0;
        }
        try {
            dipDistance_angle = Integer.parseInt(et_dipDistance_angle.getText().toString());
        } catch (NumberFormatException e) {
            dipDistance_angle=0;
        }
        weldWork.setPreHeatTime(preHeatTime);
        weldWork.setSendSnSpeedFir(sendSnSpeedFir);
        weldWork.setSendSnSumFir((int) (sendSnSumFir*10));
        weldWork.setSendSnSpeedSec(sendSnSpeedSec);
        weldWork.setSendSnSumSec((int) (sendSnSumSec*10));
        weldWork.setStopSnStimeSec(stopSnStimeSec);
        weldWork.setSendSnSpeedThird(sendSnSpeedThird);
        weldWork.setSendSnSumThird((int) (sendSnSumThird*10));
        weldWork.setStopSnTimeThird(stopSnTimeThird);
        weldWork.setSendSnSpeedFourth(sendSnSpeedFourth);
        weldWork.setSendSnSumFourth((int) (sendSnSumFourth*10));
        weldWork.setStopSnTimeFourth(stopSnTimeFourth);
        weldWork.setDipDistance(dipDistance);
        weldWork.setUpHeight(upHeight);
        weldWork.setSn(switch_isSn.isChecked());
        weldWork.setDipDistance_angle(dipDistance_angle);
        weldWork.setSus(switch_isSus.isChecked());
        weldWork.setPause(switch_isPause.isChecked());

        weldWork.set_id(currentTaskNum);// 主键与列表的方案号绑定
        L.d("保存的页面更新："+weldWork.toString());
        return weldWork;
    }

    /**
     * @param extendView
     * @return false表示为空, true表示都有数据
     * @Title isEditClean
     * @Description 判断输入框是否为空
     * @author wj
     */
    private boolean isEditClean(View extendView) {
        et_yure = (EditText) extendView.findViewById(R.id.et_yure);
        et_sendSnSumFir = (EditText) extendView.findViewById(R.id.et_sendSnSumFir);
        et_sendSnSumSec = (EditText) extendView.findViewById(R.id.et_sendSnSumSec);
        et_sendSnSumThird = (EditText) extendView.findViewById(R.id.et_sendSnSumThird);
        et_sendSnSumFourth = (EditText) extendView.findViewById(R.id.et_sendSnSumFourth);
        et_dipDistance = (EditText) extendView.findViewById(R.id.et_dipDistance);
        et_upHeight = (EditText) extendView.findViewById(R.id.et_upHeight);
        et_sendSnSpeedFir = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFir);
        et_sendSnSpeedSec = (EditText) extendView.findViewById(R.id.et_sendSnSpeedSec);
        et_stopSnStimeSec = (EditText) extendView.findViewById(R.id.et_stopSnStimeSec);
        et_stopSnTimeThird = (EditText) extendView.findViewById(R.id.et_stopSnTimeThird);
        et_stopSnTimeFourth = (EditText) extendView.findViewById(R.id.et_stopSnTimeFourth);
        et_sendSnSpeedThird = (EditText) extendView.findViewById(R.id.et_sendSnSpeedThird);
        et_sendSnSpeedFourth = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFourth);
        if ("".equals(et_yure.getText().toString())) {
            return false;
        } else if ("".equals(et_sendSnSumFir.getText().toString())) {
            return false;
        } else if ("".equals(et_sendSnSumSec.getText().toString())) {
            return false;
        } else if ("".equals(et_sendSnSumThird.getText().toString())) {
            return false;
        } else if ("".equals(et_sendSnSumFourth.getText().toString())) {
            return false;
        } else if ("".equals(et_dipDistance.getText().toString())) {
            return false;
        }else if ("".equals(et_upHeight.getText().toString())) {
            return false;
        } else if ("".equals(et_sendSnSpeedFir.getText().toString())) {
            return false;
        } else if ("".equals(et_sendSnSpeedSec.getText().toString())) {
            return false;
        } else if ("".equals(et_stopSnStimeSec.getText().toString())) {
            return false;
        } else if ("".equals(et_stopSnTimeThird.getText().toString())) {
            return false;
        } else if ("".equals(et_stopSnTimeFourth.getText().toString())) {
            return false;
        }else if ("".equals(et_sendSnSpeedThird.getText().toString())) {
            return false;
        }else if ("".equals(et_sendSnSpeedFourth.getText().toString())) {
            return false;
        }
        return true;
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

    /**
     * @Title complete
     * @Description 最终完成返回
     * @author wj
     */
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
        point.setPointParam(weldWorkDao.getPointWeldWorkParamById(mIndex,taskname));
        System.out.println("返回的Point为================》" + point);

        List<Map<Integer, PointWeldWorkParam>> list = new ArrayList<Map<Integer, PointWeldWorkParam>>();
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

     class RevHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SocketInputThread.SocketError) {
                //wifi中断
                System.out.println("wifi连接断开。。");
                SocketThreadManager.releaseInstance();
                System.out.println("单例被释放了-----------------------------");
                //设置全局变量，跟新ui
                userApplication.setWifiConnecting(false);
//				WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
                ToastUtil.displayPromptInfo(userApplication, "wifi连接断开。。");
            }else if (msg.what==Activity_Init_View){
                View activity_glue_popuplistview= stub_glue.inflate();
                popupListView= (PopupListView) activity_glue_popuplistview.findViewById(R.id.popupListView);
                popupListView.init(null);
                CreatePopupViews();
            }
        }
    }

    private void CreatePopupViews() {
        // 初始化创建10个popupView
        for (int i = 0; i < 10; i++) {
            p = i + 1;
            PopupView popupView = new PopupView(this, R.layout.popup_view_item) {

                private int mDotGlueTime;

                @Override
                public void setViewsElements(View view) {
                    ImageView title_num = (ImageView) view
                            .findViewById(R.id.title_num);

                    weldWorkLists = weldWorkDao.findAllWeldWorkParams(taskname);
                    if (p == 1) {// 方案列表第一位对应一号方案
                        title_num.setImageResource(R.drawable.green1);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 2) {
                        title_num.setImageResource(R.drawable.green2);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 3) {
                        title_num.setImageResource(R.drawable.green3);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 4) {
                        title_num.setImageResource(R.drawable.green4);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 5) {
                        title_num.setImageResource(R.drawable.green5);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 6) {
                        title_num.setImageResource(R.drawable.green6);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 7) {
                        title_num.setImageResource(R.drawable.green7);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 8) {
                        title_num.setImageResource(R.drawable.green8);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 9) {
                        title_num.setImageResource(R.drawable.green9);
                        setTitleInfos(weldWorkLists, view, p);
                    } else if (p == 10) {
                        title_num.setImageResource(R.drawable.green10);
                        setTitleInfos(weldWorkLists, view, p);
                    }

                }

                @Override
                public View setExtendView(View view) {
                    if (view == null) {
                        extendView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.extend_view, null);
//						extendView =LayoutInflater.from(getApplicationContext()).inflate(R.layout.extend_view,(ViewGroup) view,false);
                        AutoUtils.autoSize(extendView);
                        int size = weldWorkLists.size();
                        while (size > 0) {
                            size--;
                            if (p == 1) {// 方案列表第一位对应一号方案
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 2) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 3) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 4) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 5) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 6) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 7) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 8) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 9) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
                                    }
                                }
                            } else if (p == 10) {
                                initView(extendView);
                                for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
                                    if (p == pointWeldWorkParam.get_id()) {
                                        UpdateInfos(pointWeldWorkParam);
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
                    et_yure = (EditText) extendView.findViewById(R.id.et_yure);
                    switch_isSn = (ToggleButton) extendView.findViewById(R.id.switch_isSn);
                    et_sendSnSumFir = (EditText) extendView.findViewById(R.id.et_sendSnSumFir);
                    et_dipDistance_angle = (EditText) extendView.findViewById(R.id.et_dipDistance_angle);
                    switch_isSus = (ToggleButton) extendView.findViewById(R.id.switch_isSus);
                    switch_isPause = (ToggleButton) extendView.findViewById(R.id.switch_isPause);
                    et_sendSnSumSec = (EditText) extendView.findViewById(R.id.et_sendSnSumSec);
                    et_sendSnSumThird = (EditText) extendView.findViewById(R.id.et_sendSnSumThird);
                    et_sendSnSumFourth = (EditText) extendView.findViewById(R.id.et_sendSnSumFourth);
                    et_dipDistance = (EditText) extendView.findViewById(R.id.et_dipDistance);
                    et_upHeight = (EditText) extendView.findViewById(R.id.et_upHeight);
                    et_sendSnSpeedFir = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFir);
                    et_sendSnSpeedSec = (EditText) extendView.findViewById(R.id.et_sendSnSpeedSec);
                    et_stopSnStimeSec = (EditText) extendView.findViewById(R.id.et_stopSnStimeSec);
                    et_stopSnTimeThird = (EditText) extendView.findViewById(R.id.et_stopSnTimeThird);
                    et_stopSnTimeFourth = (EditText) extendView.findViewById(R.id.et_stopSnTimeFourth);
                    et_sendSnSpeedThird = (EditText) extendView.findViewById(R.id.et_sendSnSpeedThird);
                    et_sendSnSpeedFourth = (EditText) extendView.findViewById(R.id.et_sendSnSpeedFourth);

                    // 设置最大最小值

                    // 设置最大最小值
                    et_yure
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.DotGlueTimeMAX,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_yure));
                    et_sendSnSumFir
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumFir));
                    setPoint(et_sendSnSumFir);//限制小数
                    et_sendSnSumSec
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumSec));
                    setPoint(et_sendSnSumSec);//限制小数
                    et_sendSnSumThird
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumThird));
                    setPoint(et_sendSnSumThird);//限制小数
                    et_sendSnSumFourth
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumFourth));
                    setPoint(et_sendSnSumFourth);//限制小数
                    et_dipDistance
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.DipSpeed,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_dipDistance));
                    et_dipDistance_angle
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    PointConfigParam.GlueAlone.DipAngle,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_dipDistance_angle));
                    et_upHeight
                            .addTextChangedListener(new MaxMinEditWatcher(
                                    RobotParam.INSTANCE.GetZJourney(),
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_upHeight));
                    et_sendSnSpeedFir.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.DipDistanceYMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedFir));
                    et_sendSnSpeedSec.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.DipDistanceYMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedSec));
                    et_sendSnSpeedThird.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.DipDistanceYMAX,
                            PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedThird));
                    et_sendSnSpeedFourth.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.DipDistanceYMAX,
                            PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedFourth));
                    et_stopSnStimeSec.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.StopGlueTimeMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_stopSnStimeSec));
                    et_stopSnTimeThird.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.StopGlueTimeMAX,
                            PointConfigParam.GlueAlone.GlueAloneMIN, et_stopSnTimeThird));
                    et_stopSnTimeFourth.addTextChangedListener(new MaxMinEditWatcher(PointConfigParam.GlueAlone.StopGlueTimeMAX,
                            PointConfigParam.GlueAlone.GlueAloneMIN, et_stopSnTimeFourth));

                    et_yure
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.DotGlueTimeMAX,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_yure));
                    et_sendSnSumFir
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumFir));
                    et_sendSnSumSec
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumSec));
                    et_sendSnSumThird
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumThird));
                    et_sendSnSumFourth
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.sendSnSumFir,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSumFourth));
                    et_dipDistance
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.DipSpeed,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_dipDistance));
                    et_dipDistance_angle
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    PointConfigParam.GlueAlone.DipAngle,
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_dipDistance_angle));
                    et_upHeight
                            .setOnFocusChangeListener(new MaxMinFocusChangeListener(
                                    RobotParam.INSTANCE.GetZJourney(),
                                    PointConfigParam.GlueAlone.GlueAloneMIN, et_upHeight));
                    et_sendSnSpeedFir.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.DipDistanceYMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedFir
                    ));
                    et_sendSnSpeedSec.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.DipDistanceYMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedSec
                    ));
                    et_sendSnSpeedThird.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.DipDistanceYMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedThird
                    ));
                    et_sendSnSpeedFourth.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.DipDistanceYMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_sendSnSpeedFourth
                    ));
                    et_stopSnStimeSec.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.StopGlueTimeMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_stopSnStimeSec
                    ));
                    et_stopSnTimeThird.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.StopGlueTimeMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_stopSnTimeThird
                    ));
                    et_stopSnTimeFourth.setOnFocusChangeListener(new MaxMinFocusChangeListener(
                            PointConfigParam.GlueAlone.StopGlueTimeMAX, PointConfigParam.GlueAlone.GlueAloneMIN, et_stopSnTimeFourth
                    ));
                    rl_moren = (RelativeLayout) extendView
                            .findViewById(R.id.rl_moren);
                    iv_add = (ImageView) extendView.findViewById(R.id.iv_add);
                    rl_save = (RelativeLayout) extendView
                            .findViewById(R.id.rl_save);// 保存按钮
                    iv_moren = (ImageView) extendView
                            .findViewById(R.id.iv_moren);// 默认按钮
                    rl_moren.setOnClickListener(this);
                    rl_save.setOnClickListener(this);
                    et_yure.setSelectAllOnFocus(true);
                    et_sendSnSumFir.setSelectAllOnFocus(true);
                    et_sendSnSumSec.setSelectAllOnFocus(true);
                    et_sendSnSumThird.setSelectAllOnFocus(true);
                    et_sendSnSumFourth.setSelectAllOnFocus(true);
                    et_dipDistance.setSelectAllOnFocus(true);
                    et_dipDistance_angle.setSelectAllOnFocus(true);
                    et_upHeight.setSelectAllOnFocus(true);
                    et_sendSnSpeedFir.setSelectAllOnFocus(true);
                    et_sendSnSpeedSec.setSelectAllOnFocus(true);
                    et_stopSnStimeSec.setSelectAllOnFocus(true);
                    et_stopSnTimeThird.setSelectAllOnFocus(true);
                    et_stopSnTimeFourth.setSelectAllOnFocus(true);
                    et_sendSnSpeedThird.setSelectAllOnFocus(true);
                    et_sendSnSpeedFourth.setSelectAllOnFocus(true);

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
                                                WeldWorkActivity.this,
                                                SettingParam.DefaultNum.ParamGlueAloneNumber,
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
        for (PointWeldWorkParam pointWeldWorkParam : weldWorkLists) {
            list.add(pointWeldWorkParam.get_id());
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
