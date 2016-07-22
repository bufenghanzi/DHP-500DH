package com.mingseal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mingseal.adapter.TaskMainBaseAdapter;
import com.mingseal.adapter.TaskMainBaseAdapter.onMyCheckboxChangedListener;
import com.mingseal.adapter.TaskMainBaseAdapter.onMyRadioButtonChangedListener;
import com.mingseal.application.UserApplication;
import com.mingseal.communicate.Const;
import com.mingseal.communicate.NetManager;
import com.mingseal.communicate.SocketInputThread;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.dao.PointDao;
import com.mingseal.data.dao.WeldLineEndDao;
import com.mingseal.data.dao.WeldLineMidDao;
import com.mingseal.data.dao.WeldLineStartDao;
import com.mingseal.data.dao.WeldWorkDao;
import com.mingseal.data.manager.MessageMgr;
import com.mingseal.data.param.CmdParam;
import com.mingseal.data.param.OrderParam;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.param.TaskParam;
import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointTask;
import com.mingseal.data.point.PointType;
import com.mingseal.data.point.SMatrix1_4;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldOutputIOParam;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;
import com.mingseal.data.protocol.Protocol_400_1;
import com.mingseal.dhp_500dh.R;
import com.mingseal.listener.MyPopWindowClickListener;
import com.mingseal.ui.SwitchButton;
import com.mingseal.utils.CommonArithmetic;
import com.mingseal.utils.CustomProgressDialog;
import com.mingseal.utils.CustomUploadDialog;
import com.mingseal.utils.MoveUtils;
import com.mingseal.utils.ParamsSetting;
import com.mingseal.utils.PointCopyTools;
import com.mingseal.utils.SharePreferenceUtils;
import com.mingseal.utils.ToastUtil;
import com.mingseal.utils.WifiConnectTools;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Administrator
 *
 */
public class TaskActivity extends AutoLayoutActivity implements OnClickListener {

	private static final String TAG = "TaskActivity";
	/**
	 * 判断Point的List数组的长度,大于这个数时保存在内存中,小于的话用Bundle保存
	 */
	public final static int MAX_SIZE = 3000;

	public final static int requestCode = 0x00000101;
	public final static int resultCode = 0x00000102;
	/**
	 * 视图GlueViewActivity返回的resultCode
	 */
	public final static int resultViewCode = 0x00000103;
	/**
	 * 阵列GlueArrayActivity返回的resultCode
	 */
	public final static int resultArrayCode = 0x00000104;
	/**
	 * 偏移GlueOffsetActivity返回的resultCode
	 */
	public final static int resultOffsetCode = 0x00000105;

	/**
	 * 任务设置GlueTaskSettingActivity返回的resultCode
	 */
	public final static int resultSettingCode = 0x00000106;
	/**
	 * 下载GlueDownloadActivity返回的resultCode
	 */
	public final static int resultDownLoadCode = 0x00000107;
	/**
	 * GlueCameraActivity返回的resultCode
	 */
	public final static int resultCameraCode = 0x00000108;

	/**
	 * 传递时保存的number key,用于判断是大数据还是平常的数据(0代表大数据,1代表平常数据)
	 */
	public final static String KEY_NUMBER = "com.mingseal.taskactivity.number.key";
	/**
	 * 视图保存的key
	 */
	public final static String VIEW_KEY = "com.mingseal.taskactivity.view.key";
	/**
	 * 阵列保存的key
	 */
	public final static String ARRAY_KEY = "com.mingseal.taskactivity.array.key";
	/**
	 * 偏移保存的key
	 */
	public final static String OFFSET_KEY = "com.mingseal.taskactivity.offset.key";
	/**
	 * 保存下载的任务号
	 */
	public final static String DOWNLOAD_NUMBER_KEY = "com.mingseal.taskactivity.download.number.key";
	/**
	 * 下载保存的List<Point> key
	 */
	public final static String DOWNLOAD_KEY = "com.mingseal.taskactivity.downloading.key";
	/**
	 * 下载保存的DownloadParam key
	 */
	public final static String DOWNLOAD_PARAM_KEY = "com.mingseal.taskactivity.downloading.param.key";
	/**
	 * GlueCameraActivity保存的key
	 */
	public final static String CAMERA_KEY = "com.mingseal.taskactivity.camera.key";
	private TextView tv_title;// 标题栏
	private RelativeLayout rl_back;// 返回
	private RelativeLayout rl_title_speed;// 标题栏的速度布局
	private RelativeLayout rl_title_moshi;// 标题栏的模式布局
	private RelativeLayout rl_title_wifi_connecting;// wifi连接情况
	private ImageView iv_wifi_connecting;//wifi连接情况
	private ImageView image_speed;// 标题栏的速度
	private ImageView image_moshi;// 标题栏的模式

	private RelativeLayout rl_sudu;// 速度按钮
	private RelativeLayout rl_dingwei;// 定位按钮
	private RelativeLayout rl_pianyi;// 偏移按钮
	private RelativeLayout rl_moni;// 模拟按钮
	private RelativeLayout rl_shijue;// 视觉按钮

	private RelativeLayout rl_moshi;// 模式按钮
	private RelativeLayout rl_fuwei;// 复位按钮
	private RelativeLayout rl_zhenlie;// 阵列按钮
	private RelativeLayout rl_zhantie;// 粘贴按钮
	private RelativeLayout rl_shitu;// 视图按钮

	private RelativeLayout rl_xiazai;// 下载按钮
	private RelativeLayout rl_shanchu;// 删除按钮
	private RelativeLayout rl_quanxuan;// 全选按钮
	private RelativeLayout rl_shezhi;// 任务设置按钮
	private RelativeLayout rl_fangan;// 方案按钮

	private TextView tv_speed;// 速度按钮对应的文本
	private TextView tv_moshi;// 速度按钮对应的文本
	private SwitchButton singleSwitch;// switch按钮
	private Button but_xuhao;// 序号按钮
	
	/************************ add begin ************************/
	private int orderLength = 0;
	private byte[] buffer;
	private final int ORDER_BUFFER_LENTH = 100;
	private Protocol_400_1 protocol = null;
	/************************ end ******************************/

	/**
	 * @Fields but_x_plus: X+
	 */
	private Button but_x_plus;
	/**
	 * @Fields but_x_minus: X-
	 */
	private Button but_x_minus;
	/**
	 * @Fields but_y_plus: Y+
	 */
	private Button but_y_plus;
	/**
	 * @Fields but_y_minus: Y-
	 */
	private Button but_y_minus;
	/**
	 * @Fields but_z_plus: Z+
	 */
	private Button but_z_plus;
	/**
	 * @Fields but_z_minus: Z-
	 */
	private Button but_z_minus;
	/**
	 * @Fields but_u_plus: U+
	 */
	private Button but_u_plus;
	/**
	 * @Fields but_u_minus: U-
	 */
	private Button but_u_minus;
	private ListView mList;
	private LinearLayout mHeaderView;
	private LinearLayout lin_add;// add按钮
	private LinearLayout lin_pop;// popwindow
	private TextView headUTv;
	private TextView tv_selectAll;//全选文本
	public MyPopWindowClickListener popMenu;// 弹出选择框
	public PopupWindow mPopupWindow; // 弹出框

	private TaskMainBaseAdapter mAdapter;

	/**
	 * 该列表会进行增删改查的操作
	 */
	private List<Point> mPointsCur = new ArrayList<Point>();
	/**
	 * 用于存放刚开始保存的数据列表，程序刚进来时保存，退出activity时会删除
	 */
	private List<Point> mPointStorages = new ArrayList<Point>();
	private Point point;
	/**
	 * @Fields mIsFirst: 是否第一次初始化弹出框
	 */
	private boolean mIsFirst = true; // 是否第一次初始化弹出框
	
	/**
	 * @Fields isSelectAll: 是否全选,true为全选,false为全不选
	 */
	private boolean isSelectAll = true;

	private int speedFlag = 0;// 判断点击了几次，三次一循环，分别为高速33,中速13,低速3
	private int speed;
	private int[] speedXYZ;// 单步的话保存x,y,z的坐标

	/**
	 * 单选框选中的ID
	 */
	private int selectRadioIDCur;// 单选框选中的ID
	/**
	 * 单选框上次选中的ID(判断是否需要定位)
	 */
	private int selectRadioIDPrev = -1;// 单选框上次选中的ID(判断是否需要定位)

	// private String selectCheckboxID = "";// checkbox选中的ID
	/**
	 * checkbox选中的ID
	 */
	private List<Integer> selectCheckboxCur;
	/**
	 * 偏移时保存的selectCheckboxID
	 */
	private List<Integer> offsetCheckBox;

	// private String offsetCheckBoxID = "";// 偏移时保存的selectCheckboxID
	/**
	 * checkbox选中ID的最后一位(阵列回调时添加数据)
	 */
	private int sCheckViewIDLast;// checkbox选中ID的最后一位

	private PointDao pointDao;

	/**
	 * 0代表是从TaskActivity中传值过去的，要加一条数据，1代表是从TaskMainBaseAdapter传值过去的，要更新数据
	 */
	private int mFlag = 0;
	
	/**
	 * 接收从TaskListActivity传过来的Intent
	 */
	private Intent intent;// 接收从之前页面传过来的Intent
	/**
	 * 接收从TaskListActivity传过来的PointTask
	 */
	private PointTask task;// 接收从之前页面传过来的PointTask
	private String task_type;// 接收从之前页面传过来的type
	/**
	 * Point表的主键集合
	 */
	private List<Integer> rowids;// 插入Point表的主键集合
	/**
	 * @Fields m_nAxisNum : 机器轴数
	 */
	private int m_nAxisNum;

	/**
	 * 点击阵列的Intent
	 */
	private Intent arrayIntent;

	private RevHandler handler;

	private SettingParam settingParam;// 任务设置参数
	/**
	 * 连续0(默认),单步是1
	 */
	private int modeFlagCur = 0;// 连续单步判断标志
	/**
	 * 用来改变TaskMainBaseAdapter中的checkBox选中框
	 */
	private LinkedHashMap<Integer, Boolean> isSelected;

	/**
	 * 独立点的数据库操作
	 */
	private WeldWorkDao weldWorkDao;// 独立点的数据库操作
	/**
	 * 起始点的数据库操作
	 */
	private WeldLineStartDao weldLineStartDao;// 起始点的数据库操作
	/**
	 * 中间点的数据库操作
	 */
	private WeldLineMidDao weldLineMidDao;// 中间点的数据库操作
	/**
	 * 结束点的数据库操作
	 */
	private WeldLineEndDao weldLineEndDao;// 结束点的数据库操作

	private UserApplication userApplication;// 保存全局的变量
	private CustomProgressDialog progressDialog = null;
	/**
	 * 没有进度条的对话框
	 */
	private CustomUploadDialog uploadDialog = null;
	/**
	 * @Fields isChange: 判断List是否改变,true为没有变化,false表示有变化
	 */
	private boolean isChange = true;
	private  boolean prepareReset=false;//判断是否是按下复位按键
	private RelativeLayout rl_xuhao;
	private EditText et_Search;
	private ImageView empty_btn;
	private ImageView action_search;
	private TextView mTv_num;
	private TextView mTv_fangan;
	private TextView mTv_type;
	private TextView tv_x;
	private TextView mTv_y;
	private TextView mTv_z;
	private TextView tv_xiazai;
	private TextView tv_dingwei;
	private TextView tv_feiwei;
	private TextView tv_shanchu;
	private TextView mTv_pianyi;
	private TextView mTv_zhenlie;
	private TextView mTv_moni;
	private TextView mTv_niantie;
	private TextView mTv_renwushezhi;
	private TextView mTv_shijue;
	private TextView mTv_shitu;
	private TextView mTv_fangan1;
	boolean StopFlag=false;//是否在重发状态
	boolean StopSuccessFlag=false;//停止成功标记
	private int StopRetryTimes=5;//重传次数
	Timer mTimer;
	TimerTask mTimerTask;

	/**
	 * 判断是否是第一次打开popwindow
	 * 
	 * @return
	 */
	public boolean ismIsFirst() {
		return mIsFirst;
	}

	/**
	 * 设置打开popwindow的开关
	 * 
	 * @param mIsFirst
	 */
	public void setmIsFirst(boolean mIsFirst) {
		this.mIsFirst = mIsFirst;
	}

	/**
	 * 取得选中的那一行的id
	 * 
	 * @return
	 */
	public int getSelectRadioID() {
		return selectRadioIDCur;
	}

	/**
	 * 设置当前选中的radioButton的id
	 * 
	 * @param selectRadioID
	 */
	public void setSelectRadioID(int selectRadioID) {
		this.selectRadioIDCur = selectRadioID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_main);
		/************************ add begin ************************/
		protocol = new Protocol_400_1();
		/************************ end ******************************/

		// 初始化
		userApplication = (UserApplication) getApplication();
		intent = getIntent();
		task_type = intent.getStringExtra(TaskListActivity.TASK_NUMBER_TYPE);
		if ("0".equals(task_type)) {
			// 取内存中的数
			task = userApplication.getPointTask();
		} else {
			task = intent.getParcelableExtra(TaskListActivity.TASK_KEY);
		}
		Log.d(TAG, "接收到的task:" + task.toString());

		settingParam = SharePreferenceUtils.readFromSharedPreference(this);

		mList = (ListView) findViewById(R.id.lv_show);
//		mHeaderView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_task_main_listview_header, null);
		mHeaderView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_task_main_listview_header, mList, false);
		//对于listview，注意添加这一行，即可在item上使用高度
		/*===================== 适配首行文字 =====================*/
		mTv_num = (TextView) mHeaderView.findViewById(R.id.tv_num);
		mTv_fangan = (TextView) mHeaderView.findViewById(R.id.tv_fangan);
		mTv_type = (TextView) mHeaderView.findViewById(R.id.tv_type);
		tv_x = (TextView) mHeaderView.findViewById(R.id.tv_x);
		mTv_y = (TextView) mHeaderView.findViewById(R.id.tv_y);
		mTv_z = (TextView) mHeaderView.findViewById(R.id.tv_z);
		headUTv = (TextView) mHeaderView.findViewById(R.id.tv_u);
		mTv_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));
		mTv_fangan.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));
		mTv_type.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));
		tv_x.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));
		mTv_y.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));
		mTv_z.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));
		headUTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(35));

		/*=====================  end =====================*/

		AutoUtils.autoSize(mHeaderView);
		mList.addHeaderView(mHeaderView);
		pointDao = new PointDao(TaskActivity.this);
		mAdapter = new TaskMainBaseAdapter(this, TaskActivity.this);
		new GetPointsAsynctask().execute(task.getPointids());

		handler = new RevHandler();
		// 线程管理单例初始化
		SocketThreadManager.sharedInstance().setInputThreadHandler(handler);
		NetManager.instance().init(this);

		initComponent();
		initDao();
		tv_title.setText(task.getTaskName());

		singleSwitch.setOnCheckedChangeListener(new myCheckedChangeListener());

		m_nAxisNum = RobotParam.INSTANCE.getM_nAxisNum();
		mAdapter.setM_nAxisNum(m_nAxisNum);
		// 三轴的话，将U轴隐藏掉
		if (m_nAxisNum == 3) {
			headUTv.setVisibility(View.GONE);
			but_u_minus.setEnabled(false);
			but_u_plus.setEnabled(false);
//			myCircleDown.setRow("");
		} else {
			headUTv.setVisibility(View.VISIBLE);
		}
		// MessageMgr.INSTANCE.setUserApplication(userApplication);
		// 初始化
		selectCheckboxCur = new ArrayList<Integer>();
		offsetCheckBox = new ArrayList<Integer>();

		// 监听checkbox的选中事件
		mAdapter.setOnCheckboxChanged(new onMyCheckboxChangedListener() {

			@Override
			public void setCheckBoxSelected(LinkedHashMap<Integer, Boolean> isSelected) {
				Log.d(TAG, "HashMap<Integer, Boolean> isSelected-->" + isSelected);
				// selectCheckboxID = "";
				selectCheckboxCur = new ArrayList<Integer>();
				Iterator<Entry<Integer, Boolean>> iterator = isSelected.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<Integer, Boolean> entry = iterator.next();
					if (entry.getValue()) {// 为true的话取出key值
						Log.d(TAG, "entry.getKey():" + entry.getKey());
						// selectCheckboxID = selectCheckboxID + entry.getKey()
						// + ",";
						selectCheckboxCur.add(entry.getKey());
					}
					mAdapter.setSelectCheckIDS(selectCheckboxCur);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
		// 监听radiobutton的选中事件
		mAdapter.setOnRadioButtonChanged(new onMyRadioButtonChangedListener() {

			@Override
			public void setSelectID(int selectID) {
				mAdapter.setSelectID(selectID);// 选中位置
				selectRadioIDCur = selectID;
				Log.d(TAG, "mRobots.get(selectID):" + mPointsCur.get(selectID).toString());
				mAdapter.notifyDataSetChanged();
			}
		});
		System.out.println("TaskActivity--------->OnCreate()");
		

	}



	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("TaskActivity--------->OnResume()");
		// handler = new RevHandler();
		// // 线程管理单例初始化
		WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
		SocketThreadManager.sharedInstance().setInputThreadHandler(handler);
		NetManager.instance().init(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// Activity结束需要关闭进度条对话框
		stopProgressDialog();
		// Activity结束需要关闭没有进度条对话框
		stopUploadDialog();
		super.onDestroy();
	}

	/**
	 * 加载组件
	 */
	private void initComponent() {
		//搜索框
		rl_xuhao = (RelativeLayout) findViewById(R.id.rl_xuhao);
		et_Search = (EditText) findViewById(R.id.et_Search);
		empty_btn = (ImageView) findViewById(R.id.action_empty_btn);
		action_search = (ImageView) findViewById(R.id.ic_action_action_search);
		
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		rl_title_speed = (RelativeLayout) findViewById(R.id.rl_title_speed);
		rl_title_moshi = (RelativeLayout) findViewById(R.id.rl_title_moshi);
		rl_title_wifi_connecting = (RelativeLayout) findViewById(R.id.rl_title_wifi_connecting);
		iv_wifi_connecting  =(ImageView) findViewById(R.id.iv_title_wifi_connecting);
		WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
		// 让RelativeLayout显示
		rl_title_speed.setVisibility(View.VISIBLE);
		rl_title_moshi.setVisibility(View.VISIBLE);
		image_speed = (ImageView) findViewById(R.id.iv_title_speed);
		image_moshi = (ImageView) findViewById(R.id.iv_title_moshi);
		// 设置初值
		image_speed.setBackgroundResource(R.drawable.icon_speed_high);
		image_moshi.setBackgroundResource(R.drawable.icon_step_serious);
		lin_add = (LinearLayout) findViewById(R.id.lin_add);
		// 弹出对话框
		lin_add.setOnClickListener(this);

		rl_sudu = (RelativeLayout) findViewById(R.id.rl_sudu);
		rl_dingwei = (RelativeLayout) findViewById(R.id.rl_dingwei);
		rl_pianyi = (RelativeLayout) findViewById(R.id.rl_pianyi);
		rl_moni = (RelativeLayout) findViewById(R.id.rl_moni);
		rl_shijue = (RelativeLayout) findViewById(R.id.rl_shijue);

		rl_moshi = (RelativeLayout) findViewById(R.id.rl_moshi);
		rl_fuwei = (RelativeLayout) findViewById(R.id.rl_fuwei);
		rl_zhenlie = (RelativeLayout) findViewById(R.id.rl_zhenlie);
		rl_zhantie = (RelativeLayout) findViewById(R.id.rl_zhantie);
		rl_shitu = (RelativeLayout) findViewById(R.id.rl_shitu);

		rl_xiazai = (RelativeLayout) findViewById(R.id.rl_xiazai);
		rl_shanchu = (RelativeLayout) findViewById(R.id.rl_shanchu);
		rl_quanxuan = (RelativeLayout) findViewById(R.id.rl_xiugai);
		rl_shezhi = (RelativeLayout) findViewById(R.id.rl_shezhi);
		rl_fangan = (RelativeLayout) findViewById(R.id.rl_fangan);

		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		tv_speed = (TextView) rl_sudu.findViewById(R.id.tv_speed);
		tv_moshi = (TextView) findViewById(R.id.tv_moshi);
		tv_xiazai = (TextView) findViewById(R.id.tv_xiazai);
		tv_dingwei = (TextView) findViewById(R.id.tv_dingwei);
		tv_feiwei = (TextView) findViewById(R.id.tv_feiwei);
		tv_shanchu = (TextView) findViewById(R.id.tv_shanchu);
		mTv_pianyi = (TextView) findViewById(R.id.tv_pianyi);
		mTv_zhenlie = (TextView) findViewById(R.id.tv_zhenlie);
		tv_selectAll = (TextView) findViewById(R.id.tv_selectall);
		mTv_moni = (TextView) findViewById(R.id.tv_moni);
		mTv_niantie = (TextView) findViewById(R.id.tv_niantie);
		mTv_renwushezhi = (TextView) findViewById(R.id.tv_renwushezhi);
		mTv_shijue = (TextView) findViewById(R.id.tv_shijue);
		mTv_shitu = (TextView) findViewById(R.id.tv_shitu);
		mTv_fangan1 = (TextView) findViewById(R.id.tv_fangan_bottom);
/*===================== 适配文字 =====================*/
		tv_speed.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_moshi.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_xiazai.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_dingwei.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_feiwei.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_shanchu.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_pianyi.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_zhenlie.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_selectAll.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_moni.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_niantie.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_renwushezhi.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_shijue.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_shitu.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_fangan1.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(55));
		et_Search.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));

/*=====================  end =====================*/

		rl_sudu.setOnClickListener(this);
		rl_dingwei.setOnClickListener(this);
		rl_pianyi.setOnClickListener(this);
		rl_moni.setOnClickListener(this);
		rl_shijue.setOnClickListener(this);

		rl_moshi.setOnClickListener(this);
		rl_fuwei.setOnClickListener(this);
		rl_zhenlie.setOnClickListener(this);
		rl_zhantie.setOnClickListener(this);
		rl_shitu.setOnClickListener(this);

		rl_xiazai.setOnClickListener(this);
		rl_shanchu.setOnClickListener(this);
		rl_quanxuan.setOnClickListener(this);
		rl_shezhi.setOnClickListener(this);
		rl_fangan.setOnClickListener(this);

		rl_back.setOnClickListener(this);


		rl_shanchu.setEnabled(false);// 刚开始设置不能点击
		rl_zhenlie.setEnabled(false);
		rl_pianyi.setEnabled(false);
		rl_zhantie.setEnabled(false);
		rl_quanxuan.setEnabled(false);

	

		singleSwitch = (SwitchButton) findViewById(R.id.sw_single);
		
		but_x_plus = (Button) findViewById(R.id.nav_x_plus);
		but_x_minus = (Button) findViewById(R.id.nav_x_minus);
		but_y_plus = (Button) findViewById(R.id.nav_y_plus);
		but_y_minus = (Button) findViewById(R.id.nav_y_minus);
		but_z_plus = (Button) findViewById(R.id.nav_z_plus);
		but_z_minus = (Button) findViewById(R.id.nav_z_minus);
		but_u_plus = (Button) findViewById(R.id.nav_u_plus);
		but_u_minus = (Button) findViewById(R.id.nav_u_minus);
		but_x_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_x_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_y_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_y_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_z_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_z_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_u_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_u_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));


		MoveListener moveListener = new MoveListener();
		but_x_plus.setOnTouchListener(moveListener);
		but_x_minus.setOnTouchListener(moveListener);
		but_y_plus.setOnTouchListener(moveListener);
		but_y_minus.setOnTouchListener(moveListener);
		but_z_plus.setOnTouchListener(moveListener);
		but_z_minus.setOnTouchListener(moveListener);
		but_u_plus.setOnTouchListener(moveListener);
		but_u_minus.setOnTouchListener(moveListener);

		speed = settingParam.getHighSpeed();
		speedXYZ = new int[3];
		speedXYZ[0] = 4 * settingParam.getxStepDistance();
		speedXYZ[1] = 4 * settingParam.getyStepDistance();
		speedXYZ[2] = 4 * settingParam.getzStepDistance();
		et_Search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (start > 1) {
					int num = Integer.parseInt(s.toString());
					if (num > Const.SEARCH_MAX) {
						s = String.valueOf(Const.SEARCH_MAX);
						et_Search.setText(s);
					} else if (num < Const.SEARCH_MIN) {
						s = String.valueOf(Const.SEARCH_MIN);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s == null || s.equals("")) {
					mList.smoothScrollToPosition(selectRadioIDCur-1);
					mAdapter.notifyDataSetChanged();
				}else {
					int selectID= Const.SEARCH_MIN;
					try {
						selectID= Integer.parseInt(s.toString());
					} catch (NumberFormatException e) {
						selectID = Const.SEARCH_MIN;
					}
					if (selectID > Const.SEARCH_MAX) {
						et_Search.setText(String.valueOf(Const.SEARCH_MAX));
					}
					selectRadioIDCur = selectID-1;
					mList.setFastScrollEnabled(true);
					mList.setSelection(selectRadioIDCur);
					mAdapter.setSelectID(selectRadioIDCur);
					mAdapter.notifyDataSetInvalidated();
				}
			}
		});
		action_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et_Search.setVisibility(View.VISIBLE);
				empty_btn.setVisibility(View.VISIBLE);
				action_search.setVisibility(View.GONE);
			}
		});
		empty_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("隐藏搜索框");
				et_Search.setVisibility(View.GONE);
				empty_btn.setVisibility(View.GONE);
				action_search.setVisibility(View.VISIBLE);
			}
		});
	}


	/**
	 * 加载自定义的Dao
	 */
	private void initDao() {
		weldWorkDao = new WeldWorkDao(this);
		weldLineStartDao = new WeldLineStartDao(this);
		weldLineMidDao = new WeldLineMidDao(this);
		weldLineEndDao = new WeldLineEndDao(this);

	}

	/**
	 * @ClassName MoveListener
	 * @Description 触摸移动x,y,z,u
	 * @author 商炎炳
	 * @date 2016年1月28日 上午9:48:47
	 *
	 */
	private class MoveListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (selectRadioIDCur >= mPointsCur.size()) {
				selectRadioIDCur = mPointsCur.size() - 1;
				mAdapter.setSelectID(selectRadioIDCur);// 选中位置
			}
			if (selectRadioIDPrev == selectRadioIDCur && selectRadioIDCur != -1) {
				if (modeFlagCur == 0) {
					// 连续
					switch (v.getId()) {
					case R.id.nav_x_plus:// x+
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								MoveUtils.move(0, 0, 0, speed);
//								Log.d(TAG,"X+_DOWN被点击了");
								stopTimer();
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								MoveUtils.stop(0);
								prepareStopRetry(0);
//								Log.d(TAG,"X+_up被点击了-->发送了第一次停止指令");
							}
						break;
					case R.id.nav_x_minus:// x-
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(1, 0, 0, speed);
							stopTimer();
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(0);
							prepareStopRetry(0);
						}
						break;
					case R.id.nav_y_plus:// y+
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(0, 0, 1, speed);
							stopTimer();
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(1);
							prepareStopRetry(1);
						}
						break;
					case R.id.nav_y_minus:// y-
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(1, 0, 1, speed);
							stopTimer();
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(1);
							prepareStopRetry(1);
						}
						break;
					case R.id.nav_z_plus:// z+
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(0, 0, 2, speed);
							stopTimer();
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(2);
							prepareStopRetry(2);
						}
						break;
					case R.id.nav_z_minus:// z-
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(1, 0, 2, speed);
							stopTimer();
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(2);
							prepareStopRetry(2);
						}
						break;
					case R.id.nav_u_plus:// u+
						if (m_nAxisNum == 4) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								MoveUtils.move(0, 0, 3, speed);
								stopTimer();
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								MoveUtils.stop(3);
								prepareStopRetry(3);
							}
						}
						break;
					case R.id.nav_u_minus:// u-
						if (m_nAxisNum == 4) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								MoveUtils.move(1, 0, 3, speed);
								stopTimer();
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								MoveUtils.stop(3);
								prepareStopRetry(3);
							}
						}
						break;
					}
				} else if (modeFlagCur == 1) {
					// 单步
					switch (v.getId()) {
					case R.id.nav_x_plus:// x+
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(0, 1, 0, speedXYZ[0]);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(0);
						}
						break;
					case R.id.nav_x_minus:// x-
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(1, 1, 0, speedXYZ[0]);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(0);
						}
						break;
					case R.id.nav_y_plus:// y+
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(0, 1, 1, speedXYZ[1]);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(1);
						}
						break;
					case R.id.nav_y_minus:// y-
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(1, 1, 1, speedXYZ[1]);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(1);
						}
						break;
					case R.id.nav_z_plus:// z+
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(0, 1, 2, speedXYZ[2]);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(2);
						}
						break;
					case R.id.nav_z_minus:// z-
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							MoveUtils.move(1, 1, 2, speedXYZ[2]);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							MoveUtils.stop(2);
						}
					case R.id.nav_u_plus:// u+
						if(m_nAxisNum == 4){
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								MoveUtils.move(0, 1, 3, speedXYZ[0]);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								MoveUtils.stop(3);
							}
						}
						break;
					case R.id.nav_u_minus:// u-
						if(m_nAxisNum == 4){
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								MoveUtils.move(1, 1, 3, speedXYZ[0]);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								MoveUtils.stop(3);
							}
						}
						break;
					}
				}
			} else if (selectRadioIDPrev != selectRadioIDCur && selectRadioIDCur != -1) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下定位
					MoveUtils.locationCoord(mPointsCur.get(selectRadioIDCur));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 抬起设置上次选中的单选框
					selectRadioIDPrev = selectRadioIDCur;
				}
			}
			mAdapter.notifyDataSetChanged();
			return false;
		}
		
	}

	/**
	 * 将之前的定时任务移除队列
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}


	/**
	 * 重发停止指令
	 * @param i
	 */
	private void prepareStopRetry(final int i) {
		StopRetryTimes=5;//重新设置重传次数
		StopSuccessFlag=false;//重置标记为
		StopFlag=false;//非重发停止指令状态
		if (mTimer==null){

			mTimer=new Timer();
		}
		if (mTimerTask == null){
			mTimerTask=new TimerTask() {
				@Override
				public void run() {
					if (StopSuccessFlag==false) {
						if (StopRetryTimes > 0) {
							if (StopSuccessFlag == false) {
								StopRetryTimes--;
								MoveUtils.stop(i);
//								Log.d(TAG, "重发了停止指令");
								StopFlag=true;
							}
						}else{
							//重发失败
//							Log.d(TAG,"重发失败！");
							//关闭timer，重置参数
							StopRetryTimes=5;//重新设置重传次数
							StopSuccessFlag=false;//重置标记为
							StopFlag=false;//非重发停止指令状态
							mTimer.cancel();
							mTimer = null;
							mTimerTask.cancel();
							mTimerTask = null;
						}
					}else {
						if (StopFlag){//重发状态
								//成功
//								Log.d(TAG, "重发了停止指令成功！");
								StopSuccessFlag = false;
							StopRetryTimes=5;//重新设置重传次数
							StopSuccessFlag=false;//重置标记为
							StopFlag=false;//非重发停止指令状态
							mTimer.cancel();
							mTimer = null;
							mTimerTask.cancel();
							mTimerTask = null;
						}
					}
				}
			};
		}
		if(mTimer != null && mTimerTask != null ){
			mTimer.schedule(mTimerTask,220,60);
		}
	}

	/**
	 * 自定义Switch的事件监听
	 *
	 */
	private class myCheckedChangeListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			if (isChecked) {
				// 选中为多，要把序号隐藏
				rl_xuhao.setVisibility(View.GONE);
				rl_shanchu.setEnabled(true);
				rl_zhenlie.setEnabled(true);
				rl_pianyi.setEnabled(true);
				rl_zhantie.setEnabled(true);
				rl_quanxuan.setEnabled(true);
				rl_dingwei.setEnabled(false);
				rl_shijue.setEnabled(false);
				rl_fangan.setEnabled(false);
			} else {
				rl_xuhao.setVisibility(View.VISIBLE);
				rl_shanchu.setEnabled(false);
				rl_zhenlie.setEnabled(false);
				rl_pianyi.setEnabled(false);
				rl_zhantie.setEnabled(false);
				rl_quanxuan.setEnabled(false);
				rl_dingwei.setEnabled(true);
				rl_shijue.setEnabled(true);
				rl_fangan.setEnabled(true);
				// 多选框回到单选框，selectID如果比list集合大的话，需要修改
				if (selectRadioIDCur >= mPointsCur.size()) {
					selectRadioIDCur = mPointsCur.size() - 1;
					mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				}
			}
			mAdapter.setSingleOrMultify(isChecked);
			mAdapter.notifyDataSetChanged();
		}
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

	/**
	 * 检查任务点的合法性
	 * 
	 * @param points
	 * @return true为合法的,false表示有不合法的点存在
	 */
	private boolean checkForValidity(List<Point> points) {
		if (points.size() == 0 || points == null) {
			ToastUtil.displayPromptInfo(this, getResources().getString(R.string.not_null));
			return false;
		}
		for (int i = 0; i < points.size(); i++) {
			 if (getPointType(points.get(i)).equals(PointType.POINT_WELD_LINE_START)) {// 起始点
				if ((i + 1) >= points.size()) {
					ToastUtil.displayPromptInfo(this, "不能以起始点结尾");
					selectRadioIDCur = i;
					return false;
				} else {
					boolean isLineEnd = false;
					for (int j = i + 1; j < points.size(); j++) {
						if (getPointType(points.get(j)).equals(PointType.POINT_WELD_LINE_END)) {
							isLineEnd = true;
							break;
						} else if (!(getPointType(points.get(j)).equals(PointType.POINT_WELD_LINE_MID))) {
							// 不是中间点
							ToastUtil.displayPromptInfo(this, "起始点后面不能跟除中间点之外其他的点");
							selectRadioIDCur = j;
							return false;
						}
					}
					if (!isLineEnd) {
						ToastUtil.displayPromptInfo(this, "起始点必须和结束点相对应出现");
						selectRadioIDCur = i;
						return false;
					}
				}
			} else if (getPointType(points.get(i)).equals(PointType.POINT_WELD_LINE_END)) {// 结束点
				if (i <= 0) {
					ToastUtil.displayPromptInfo(this, "不能以结束点开始");
					selectRadioIDCur = i;
					return false;
				} else {
					boolean isLineStart = false;
					for (int j = i - 1; j >= 0; j--) {
						if (getPointType(points.get(j)).equals(PointType.POINT_WELD_LINE_START)) {
							isLineStart = true;
							break;
						} else if (!(getPointType(points.get(j)).equals(PointType.POINT_WELD_LINE_MID))) {
							// 不是中间点或者是圆弧点
							ToastUtil.displayPromptInfo(this, "结束点前面不能跟除中间点之外其他的点");
							selectRadioIDCur = j;
							return false;
						}
					}
					if (!isLineStart) {
						ToastUtil.displayPromptInfo(this, "起始点必须和结束点相对应出现");
						selectRadioIDCur = i;
						return false;
					}
				}
			} else if (getPointType(points.get(i)).equals(PointType.POINT_WELD_LINE_MID)) {// 中间点
				if (i <= 0) {
					ToastUtil.displayPromptInfo(this, "不能以中间点开始");
					selectRadioIDCur = i;
					return false;
				} else if ((i + 1) >= points.size()) {
					ToastUtil.displayPromptInfo(this, "不能以中间点结尾");
					selectRadioIDCur = i;
					return false;
				} else {
					boolean isLineStart = false;
					boolean isLineEnd = false;
					for (int j = i - 1; j >= 0; j--) {
						if (getPointType(points.get(j)).equals(PointType.POINT_WELD_LINE_START)) {
							isLineStart = true;
							break;
						}
					}
					for (int j = i + 1; j < points.size(); j++) {
						if (getPointType(points.get(j)).equals(PointType.POINT_WELD_LINE_END)) {
							isLineEnd = true;
							break;
						}
					}
					if (!(isLineStart && isLineEnd)) {
						ToastUtil.displayPromptInfo(this, "中间点必须在起始点和结束点之间");
						selectRadioIDCur = i;
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 下载之前要判断独立点,线结束点,面结束点的抬起高度(放到异步线程里去，这里不需要了！！！)
	 * 
	 * @param points
	 * @return false(抬起高度过高),true(一切正常)
	 */
	private boolean checkUpHeightValidity(List<Point> points) {
		// 独立点参数改为作业点
		PointWeldWorkParam pointWeldWorkParam = null;
		// 线结束点参数改为焊锡结束点
		PointWeldLineEndParam pointWeldLineEndParam = null;

		Point point;
		// 类型
		PointType pointType = PointType.POINT_NULL;
		// Point的任务参数序列
		int id = -1;
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			pointType = getPointType(point);
			id = point.getPointParam().get_id();
			if (pointType.equals(PointType.POINT_GLUE_ALONE)) {
				// 如果等于独立点
				pointWeldWorkParam = weldWorkDao.getPointWeldWorkParamById(id);
				if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < pointWeldWorkParam.getUpHeight()) {
					// Z轴行程小于抬起高度
					ToastUtil.displayPromptInfo(this, "作业点的抬起高度过高");
					selectRadioIDCur = i;
					return false;
				}
			} else if (pointType.equals(PointType.POINT_GLUE_LINE_END)) {
				// 如果为线结束点
				pointWeldLineEndParam = weldLineEndDao.getPointWeldLineEndParamByID(id);
				if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < pointWeldLineEndParam.getUpHeight()) {
					// Z轴行程小于抬起高度
					ToastUtil.displayPromptInfo(this, "线结束点的抬起高度过高");
					selectRadioIDCur = i;
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 保存页面中的信息并返回到之前的TaskListActivity
	 */
	private void saveBackActivity() {
		// 要先判断页面中的List是否满足点的条件
		if (checkForValidity(mPointsCur)) {
			// 初始化异步线程
			CheckBackUpHeightAsynctask check = new CheckBackUpHeightAsynctask();
			check.execute(mPointsCur);
		} else {
			singleSwitch.setChecked(false);// 设置为单选框显示
			mAdapter.setSelectID(selectRadioIDCur);// 选中位置
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 点击方案选项，跳转到对应的Activity,打开相对应的任务参数
	 */
	private void processTaskActivity() {
		if (mPointsCur.size()==0) {
			ToastUtil.displayPromptInfo(this, "请先增加一个任务点");
		}else {
			PointType type = mPointsCur.get(selectRadioIDCur).getPointParam().getPointType();
			System.out.println("TaskActivity打开的方案为："+mPointsCur.get(selectRadioIDCur).getPointParam());
			Intent intent = null;
			switch (type) {
				case POINT_WELD_WORK:
				intent = new Intent(this, WeldWorkActivity.class);
				break;
				case POINT_WELD_LINE_START:
				intent = new Intent(this, WeldLineStartActivity.class);
				break;
				case POINT_WELD_LINE_MID:
				intent = new Intent(this, WeldLineMidActivity.class);
				break;
				case POINT_WELD_LINE_END:
				intent = new Intent(this, WeldLineEndActivity.class);
				break;
				case POINT_WELD_BLOW:
				intent = new Intent(this, WeldBlowActivity.class);
				break;
				case POINT_WELD_INPUT:
				intent = new Intent(this, WeldInputActivity.class);
				break;
				case POINT_WELD_OUTPUT:
				intent = new Intent(this, WeldOutputActivity.class);
				break;
				
			default:
				// 其他点没有参数方案
				intent = null;
				ToastUtil.displayPromptInfo(this, "没有参数方案");
				break;
			}
			if (intent != null) {
				Bundle extras = new Bundle();
				extras.putParcelable(MyPopWindowClickListener.POPWINDOW_KEY, mPointsCur.get(selectRadioIDCur));
				extras.putInt(MyPopWindowClickListener.FLAG_KEY, 1);// 1代表更新数据
				extras.putInt(MyPopWindowClickListener.TYPE_KEY, 1);// 1代表要显示方案
				intent.putExtras(extras);
				startActivityForResult(intent, requestCode);
//				overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
			}
		}
	}

	/**
	 * 点击返回按钮响应事件
	 */
	private void showBackDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
		builder.setMessage(getResources().getString(R.string.is_need_save));
		builder.setTitle(getResources().getString(R.string.tip));
		builder.setPositiveButton(getResources().getString(R.string.need_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				saveBackActivity();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.is_need_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				TaskActivity.this.finish();
			}
		});
		builder.setNeutralButton(getResources().getString(R.string.is_need_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/**
	 * 将数据List<Point> 传递到下一个activity中(GlueArrayActivity,GlueOffsetActivity,
	 * GlueViewActivity)
	 * 
	 * @param _intent
	 *            要传递的intent
	 * @param _points
	 *            要传递的数据List
	 */
	private void save2Activity(Intent _intent, List<Point> _points) {
		if (checkForValidity(mPointsCur)) {
			Bundle extras = new Bundle();
			if (_points.size() > MAX_SIZE) {
				extras.putString(KEY_NUMBER, "0");
				userApplication.setPoints(_points);
			} else {
				extras.putString(KEY_NUMBER, "1");
				extras.putParcelableArrayList(TaskActivity.ARRAY_KEY, (ArrayList<? extends Parcelable>) _points);
			}
			_intent.putExtras(extras);
			startActivityForResult(_intent, requestCode);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
			isSelectAll = true;//设置全选
		} else {
			selectCheckboxCur.clear();
			mAdapter.setData(mPointsCur);
			singleSwitch.setChecked(false);// 有错的话设置为单选框显示
			mList.smoothScrollToPosition(selectRadioIDCur);// Listview滚动到指定地方
			mAdapter.setSelectID(selectRadioIDCur);// 选中位置
			mAdapter.notifyDataSetChanged();
		}
	}

	/*
	 * Activity传值的回调方法
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		Log.d(TAG,"onActivityResult被调用了");
		if (_requestCode == requestCode) {
			if (_resultCode == resultCode) {
				// point = (Point)
				// _data.getSerializableExtra(MyPopWindowClickListener.POPWINDOW_KEY);
				mFlag = _data.getIntExtra(MyPopWindowClickListener.FLAG_KEY, 0);
				point = (Point) _data.getParcelableExtra(MyPopWindowClickListener.POPWINDOW_KEY);
				//准备更新的方案
				ArrayList list= _data.getParcelableArrayListExtra(MyPopWindowClickListener.TYPE_UPDATE);
				System.out.println(TAG+point.toString());
				Log.d(TAG ,point.toString());
//				Log.d(TAG + ":onActivityResult", "ParcelableMap:" + list.get(0));
				if (mFlag == 0) {
					if (mPointsCur.size() != 0) {
						selectRadioIDCur = selectRadioIDCur + 1;
					} else {
						selectRadioIDCur = 0;
					}
					mPointsCur.add(selectRadioIDCur, point);
					mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				} else if (mFlag == 1) {
					mPointsCur.remove(selectRadioIDCur);
					mPointsCur.add(selectRadioIDCur, point);
				}
				selectCheckboxCur.clear();
				singleSwitch.setChecked(false);
				startUpdatePointParam(point,list);
//				Log.d(TAG + ":onActivityResult-->", mPointsCur.toString());
			} else if (_resultCode == resultViewCode) {
				// 视图保存回来的点
				String type = _data.getStringExtra(KEY_NUMBER);
				if ("0".equals(type)) {
					// 大数据
					mPointsCur = userApplication.getPoints();
				} else if ("1".equals(type)) {
					mPointsCur = _data.getParcelableArrayListExtra(VIEW_KEY);
				}
				System.out.println("视图回来的点的长度："+mPointsCur.size());
			} else if (_resultCode == resultArrayCode) {
				List<Point> pLists = new ArrayList<>();
					String array_type = _data.getStringExtra(KEY_NUMBER);
					if ("0".equals(array_type)) {
						// 大数据,从内存读取
						pLists = userApplication.getPoints();
					} else if ("1".equals(array_type)) {
					pLists = _data.getParcelableArrayListExtra(ARRAY_KEY);
				}
				Log.d(TAG, "阵列回来的点的长度："+pLists.size());
				mPointsCur.addAll(sCheckViewIDLast + 1, pLists);
			} else if (_resultCode == resultOffsetCode) {
				// 偏移
				String type = _data.getStringExtra(KEY_NUMBER);
				List<Point> pLists = new ArrayList<>();
				if ("0".equals(type)) {
					pLists = userApplication.getPoints();
				} else {
					pLists = _data.getParcelableArrayListExtra(OFFSET_KEY);
				}
				Log.i(TAG, "偏移：" + offsetCheckBox.toString());
				for (int i = 0; i < offsetCheckBox.size(); i++) {
					mPointsCur.remove((int) offsetCheckBox.get(i));
					mPointsCur.add((int) offsetCheckBox.get(i), pLists.get(i));
				}
				offsetCheckBox.clear();
				Log.d(TAG, "偏移保存回来的点:" + pLists.toString());
			} else if (_resultCode == resultSettingCode) {
				// 任务参数设置
				settingParam = SharePreferenceUtils.readFromSharedPreference(this);
			} else if (_resultCode == resultDownLoadCode) {
				// ToastUtil.showToast(this, "下载完成");
				List<Point> pLists = new ArrayList<>();
				String array_type = _data.getStringExtra(KEY_NUMBER);
				if ("0".equals(array_type)) {
					// 大数据,从内存读取
					mPointsCur = userApplication.getPoints();
				} else if ("1".equals(array_type)) {
					mPointsCur = _data.getParcelableArrayListExtra(DOWNLOAD_KEY);
				}
				Log.d(TAG, "下载回来的点的长度："+mPointsCur.size());
				selectRadioIDCur=0;
			} else if (_resultCode == resultCameraCode) {
				// 视觉
				point = _data.getParcelableExtra(CAMERA_KEY);
				mPointsCur.remove(selectRadioIDCur);
				mPointsCur.add(selectRadioIDCur, point);
			}

		}
		// 返回时需要将上次选中id置为初始值
		selectRadioIDPrev = -1;
		Log.d(TAG,"mpoints:"+mPointsCur.toString());
		mAdapter.setData(mPointsCur);
		mAdapter.notifyDataSetChanged();
	}

	

	/**
	 * @Title  startUpdatePointParam
	 * @Description 更新所有使用该方案的点的参数方案
	 * @author wj
	 * @param point 
	 * @param list 存放list<map<方案号，方案>>的list集合
	 */
	private void startUpdatePointParam(Point point, ArrayList list) {
		
		switch (point.getPointParam().getPointType()) {
			case POINT_WELD_WORK:
			 ArrayList<Map<Integer, PointWeldWorkParam>> maplist=(ArrayList<Map<Integer, PointWeldWorkParam>>) list.get(0);
			 HashMap<Integer, PointWeldWorkParam> map=(HashMap<Integer, PointWeldWorkParam>) maplist.get(0);
//			 Log.d(TAG + ":onActivityResult", "ParcelableMap:" + map);
			 for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : map.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldWorkParam param=(PointWeldWorkParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}
			break;
			case POINT_WELD_LINE_START:
			ArrayList<Map<Integer, PointWeldLineStartParam>> line_startList=(ArrayList<Map<Integer, PointWeldLineStartParam>>) list.get(0);
			HashMap<Integer, PointWeldLineStartParam> gluestartmap=(HashMap<Integer, PointWeldLineStartParam>) line_startList.get(0);
//			Log.d(TAG + ":onActivityResult", "ParcelableMap:" + gluestartmap);
			for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : gluestartmap.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldLineStartParam param=(PointWeldLineStartParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}

			break;
			case POINT_WELD_LINE_MID:
			ArrayList<Map<Integer, PointWeldLineMidParam>> line_midList=(ArrayList<Map<Integer, PointWeldLineMidParam>>) list.get(0);
			HashMap<Integer, PointWeldLineMidParam> glueMidMap=(HashMap<Integer, PointWeldLineMidParam>) line_midList.get(0);
//			Log.d(TAG + ":onActivityResult", "ParcelableMap:" + glueMidMap);
			for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : glueMidMap.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldLineMidParam param=(PointWeldLineMidParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}

			break;
			case POINT_WELD_LINE_END:
			ArrayList<Map<Integer, PointWeldLineEndParam>> line_endList=(ArrayList<Map<Integer, PointWeldLineEndParam>>) list.get(0);
			HashMap<Integer, PointWeldLineEndParam> glueEndMap=(HashMap<Integer, PointWeldLineEndParam>) line_endList.get(0);
//			Log.d(TAG + ":onActivityResult", "ParcelableMap:" + glueEndMap);
			for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : glueEndMap.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldLineEndParam param=(PointWeldLineEndParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}

			break;
			case POINT_WELD_BLOW:
			ArrayList<Map<Integer, PointWeldBlowParam>> clearlList=(ArrayList<Map<Integer, PointWeldBlowParam>>) list.get(0);
			HashMap<Integer, PointWeldBlowParam> clearMap=(HashMap<Integer, PointWeldBlowParam>) clearlList.get(0);
//			Log.d(TAG + ":onActivityResult", "ParcelableMap:" + clearMap);
			for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : clearMap.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldBlowParam param=(PointWeldBlowParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}

			break;
			case POINT_WELD_INPUT:
			ArrayList<Map<Integer, PointWeldInputIOParam>> inputList=(ArrayList<Map<Integer, PointWeldInputIOParam>>) list.get(0);
			HashMap<Integer, PointWeldInputIOParam> inputMap=(HashMap<Integer, PointWeldInputIOParam>) inputList.get(0);
//			Log.d(TAG + ":onActivityResult", "ParcelableMap:" + inputMap);
			for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : inputMap.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldInputIOParam param=(PointWeldInputIOParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}

			break;
			case POINT_WELD_OUTPUT:
			ArrayList<Map<Integer, PointWeldOutputIOParam>> outputlList=(ArrayList<Map<Integer, PointWeldOutputIOParam>>) list.get(0);
			HashMap<Integer, PointWeldOutputIOParam> outputMap=(HashMap<Integer, PointWeldOutputIOParam>) outputlList.get(0);
//			Log.d(TAG + ":onActivityResult", "ParcelableMap:" + outputMap);
			for (Point pointCur : mPointsCur) {
				if (pointCur.getPointParam().getPointType().equals(point.getPointParam().getPointType())) {
					for (Map.Entry entry : outputMap.entrySet()) {
						int key_id=(int) entry.getKey();
						PointWeldOutputIOParam param=(PointWeldOutputIOParam) entry.getValue();
						if (pointCur.getPointParam().get_id()==key_id) {
							pointCur.setPointParam(param);
						}
					}
				}
			}
			break;
		default:
			break;
		}	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			// 触摸其它地方，让popwindow消失
			mPopupWindow.dismiss();
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isChange  = PointCopyTools.comparePoints(mPointStorages, mPointsCur);
			if(isChange){
//				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.data_not_changed));
				TaskActivity.this.finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
			}else{
				showBackDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @Title saveMediumSpeed
	 * @Description 单步和连续的速度都改成中速的，且将文本提示设置成中速
	 * @param settingParam
	 */
	private void saveMediumSpeed(SettingParam settingParam) {
		speed = settingParam.getMediumSpeed();
		speedXYZ[0] = 2 * settingParam.getxStepDistance();
		speedXYZ[1] = 2 * settingParam.getyStepDistance();
		speedXYZ[2] = 2 * settingParam.getzStepDistance();
		ToastUtil.displayPromptInfo(this, getResources().getString(R.string.activity_medium));
		image_speed.setBackgroundResource(R.drawable.icon_speed_medium);
		tv_speed.setText(getResources().getString(R.string.activity_medium));
	}

	/**
	 * @Title saveLowSpeed
	 * @Description 单步和连续的速度都改成低速的，且将文本提示设置成低速
	 * @param settingParam
	 */
	private void saveLowSpeed(SettingParam settingParam) {
		speed = settingParam.getLowSpeed();
		speedXYZ[0] = 1 * settingParam.getxStepDistance();
		speedXYZ[1] = 1 * settingParam.getyStepDistance();
		speedXYZ[2] = 1 * settingParam.getzStepDistance();
		ToastUtil.displayPromptInfo(this, getResources().getString(R.string.activity_low));
		image_speed.setBackgroundResource(R.drawable.icon_speed_low);
		tv_speed.setText(getResources().getString(R.string.activity_low));
	}

	/**
	 * @Title saveHighSpeed
	 * @Description 单步和连续的速度都改成高速的，且将文本提示设置成高速
	 * @param settingParam
	 */
	private void saveHighSpeed(SettingParam settingParam) {
		speed = settingParam.getHighSpeed();
		speedXYZ[0] = 4 * settingParam.getxStepDistance();
		speedXYZ[1] = 4 * settingParam.getyStepDistance();
		speedXYZ[2] = 4 * settingParam.getzStepDistance();
		ToastUtil.displayPromptInfo(this, getResources().getString(R.string.activity_high));
		image_speed.setBackgroundResource(R.drawable.icon_speed_high);
		tv_speed.setText(getResources().getString(R.string.activity_high));
	}

	/*
	 * 点击事件 (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_back:// 点击头文件的返回按钮
			isChange  = PointCopyTools.comparePoints(mPointStorages, mPointsCur);
			if(isChange){
//				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.data_not_changed));
				TaskActivity.this.finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
			}else{
				showBackDialog();
			}
			break;

		case R.id.lin_add:// 点击+弹出选择框
			if (mIsFirst) {
				mIsFirst = false;
				// int height = v.getHeight();
				popMenu = new MyPopWindowClickListener(TaskActivity.this);
				mPopupWindow = popMenu.getMenu();
			}
			popMenu.setPointLists(mPointsCur, selectRadioIDCur, 0, mAdapter);
			// mPopupWindow.setFocusable(true);
			mPopupWindow.setOutsideTouchable(true); // 设置点击屏幕其它地方弹出框消失
			/*=================== begin ===================*/
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			/*===================  add  ===================*/
			if (mPopupWindow == null) {
				return;
			}
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				return;
			}
			// 设置popwindow的显示和消失动画
			mPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
			// 改变后面的数值就能上下平移
			mPopupWindow.showAtLocation(lin_add, Gravity.LEFT, 0, 0);
			break;
		case R.id.rl_sudu:// 速度

			speedFlag++;
			if (speedFlag % 3 == 1) {
				saveMediumSpeed(settingParam);
			} else if (speedFlag % 3 == 2) {
				saveLowSpeed(settingParam);
			} else if (speedFlag % 3 == 0) {
				saveHighSpeed(settingParam);
			}

			break;
		case R.id.rl_dingwei:// 定位
			Log.d(TAG, "--------->定位");
			if (!mPointsCur.isEmpty()) {
				OrderParam.INSTANCE.setnXCoord(mPointsCur.get(selectRadioIDCur).getX());
				OrderParam.INSTANCE.setnYCoord(mPointsCur.get(selectRadioIDCur).getY());
				OrderParam.INSTANCE.setnZCoord(mPointsCur.get(selectRadioIDCur).getZ());
				OrderParam.INSTANCE.setnSpeed(200);
				MessageMgr.INSTANCE.setCurCoord();
			} else {
				ToastUtil.displayPromptInfo(this, "列表为空,不能定位！");
			}
			break;
		case R.id.rl_pianyi:// 偏移
			Log.d(TAG, "--------->偏移" + selectCheckboxCur.toString());
			if (selectCheckboxCur.size() != 0 && selectCheckboxCur != null) {
				List<Point> pointArrays = new ArrayList<Point>();
				
				for (int i = 0; i < selectCheckboxCur.size(); i++) {
					point = mPointsCur.get((int) selectCheckboxCur.get(i));
					pointArrays.add(point);// 要偏移的先加到一个List里
				}
				arrayIntent = new Intent(this, GlueOffsetActivity.class);
				save2Activity(arrayIntent, pointArrays);
				offsetCheckBox.clear();
				// 将选中的ID保存到一个自定义的string里去，然后再删除
				for (Integer number : selectCheckboxCur) {
					offsetCheckBox.add(number);
				}
				selectCheckboxCur.clear();

			} else {
				ToastUtil.displayPromptInfo(TaskActivity.this, "请选择要偏移的点");
			}

			break;
		case R.id.rl_moni:// 模拟
			Log.d(TAG, "--------->模拟");
			if (checkForValidity(mPointsCur)) {
				CheckSimulationUpHeightAsynctask check = new CheckSimulationUpHeightAsynctask();
				check.execute(mPointsCur);
			} else {
				selectCheckboxCur.clear();
				mAdapter.setData(mPointsCur);
				singleSwitch.setChecked(false);// 有错的话设置为单选框显示
				mList.smoothScrollToPosition(selectRadioIDCur);// Listview滚动到指定地方
				mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.rl_shijue:// 视觉
			Log.d(TAG, "--------->视觉");
			if (!mPointsCur.isEmpty()) {
				arrayIntent = new Intent(this, GlueCameraActivity.class);
				Bundle extras = new Bundle();
				extras.putParcelable(TaskActivity.ARRAY_KEY, mPointsCur.get(selectRadioIDCur));
				arrayIntent.putExtras(extras);
				startActivityForResult(arrayIntent, requestCode);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
			} else {
				ToastUtil.displayPromptInfo(this, "请先增加一个任务点！");
			}
			break;
		case R.id.rl_moshi:// 模式
			Log.d(TAG, "--------->模式");
			if (modeFlagCur == 0) {
				modeFlagCur = 1;// 点击一次变为单步
				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.step_single));
				image_moshi.setBackgroundResource(R.drawable.icon_step_single);
				tv_moshi.setText(getResources().getString(R.string.step_single));
			} else {
				modeFlagCur = 0;// 默认为0
				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.step_serious));
				image_moshi.setBackgroundResource(R.drawable.icon_step_serious);
				tv_moshi.setText(getResources().getString(R.string.step_serious));
			}
			break;
		case R.id.rl_fuwei:// 复位
//			prepareReset=true;
//			MessageMgr.INSTANCE.resetCoord();
			MessageMgr.INSTANCE.resetCoordDirect();
//			sendResetCommand();
			break;
		case R.id.rl_zhenlie:// 阵列
			Log.d(TAG, "--------->阵列");

			if (selectCheckboxCur.size() != 0 && selectCheckboxCur != null) {
				List<Point> pointArrays = new ArrayList<Point>();
				for (int i = 0; i < selectCheckboxCur.size(); i++) {
					point = mPointsCur.get((int) selectCheckboxCur.get(i));
					pointArrays.add(point);
				}
				sCheckViewIDLast = selectCheckboxCur.get(selectCheckboxCur.size() - 1);
				selectCheckboxCur.clear();

				arrayIntent = new Intent(this, GlueArrayActivity.class);
				save2Activity(arrayIntent, pointArrays);

			} else {
				ToastUtil.displayPromptInfo(TaskActivity.this, "请选择要阵列的选项");
			}

			break;
		case R.id.rl_zhantie:// 粘贴
			if (selectCheckboxCur.size() != 0 && selectCheckboxCur != null) {
				List<Point> pointArrays = new ArrayList<Point>();
				for (int i = 0; i < selectCheckboxCur.size(); i++) {
					int id = (int) selectCheckboxCur.get(i);
					// Point point = mPoints.get(id);
					point = new Point(PointType.POINT_NULL);
					point.setX(mPointsCur.get(id).getX());
					point.setY(mPointsCur.get(id).getY());
					point.setZ(mPointsCur.get(id).getZ());
					point.setU(mPointsCur.get(id).getU());
					point.setPointParam(mPointsCur.get(id).getPointParam());
					if (!point.getPointParam().getPointType().equals(PointType.POINT_WELD_BASE)) {
						pointArrays.add(point);
					}
				}
				selectCheckboxCur.clear();
				mPointsCur.addAll(pointArrays);
				mAdapter.setData(mPointsCur);
				mAdapter.notifyDataSetChanged();
			} else {
				ToastUtil.displayPromptInfo(TaskActivity.this, "请选择要粘贴的选项");
			}
			break;
		case R.id.rl_shitu:// 视图
			Log.d(TAG, "--------->视图");
			arrayIntent = new Intent(this, GlueViewActivity.class);
			Log.d(TAG, "视图：" + mPointsCur.toString());
			save2Activity(arrayIntent, mPointsCur);
			break;
		case R.id.rl_xiazai:// 下载

			// arrayIntent = new Intent(this, GlueDownloadActivity.class);
			// save2DownLoadActivity(arrayIntent, mPoints);
			if (checkForValidity(mPointsCur)) {
				CheckUpHeightAsynctask check = new CheckUpHeightAsynctask();
				check.execute(mPointsCur);
			} else {
				selectCheckboxCur.clear();
				mAdapter.setData(mPointsCur);
				singleSwitch.setChecked(false);// 有错的话设置为单选框显示
//				mList.smoothScrollToPosition(selectRadioIDCur);// Listview滚动到指定地方
//				mList.setFastScrollEnabled(true);
				mList.setSelection(selectRadioIDCur);
				mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.rl_shanchu:// 删除
			if (selectCheckboxCur.size() != 0 && selectCheckboxCur != null) {
				Log.i(TAG, "数组：" + selectCheckboxCur.toString());
				for (int i = selectCheckboxCur.size() - 1; i >= 0; i--) {
					Log.d(TAG, "Integer.parseInt(checkBox[i])" + selectCheckboxCur.get(i));
					mPointsCur.remove((int) selectCheckboxCur.get(i));
				}
				selectCheckboxCur.clear();
				if (selectRadioIDCur >= mPointsCur.size()) {
					selectRadioIDCur = mPointsCur.size() - 1;
					mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				}
				mAdapter.setData(mPointsCur);
				mAdapter.notifyDataSetChanged();
			} else {
				ToastUtil.displayPromptInfo(TaskActivity.this, "请选择要删除的选项");
			}

			break;
		case R.id.rl_xiugai:// 全选
			if(isSelectAll){
//				tv_selectAll.setText(getResources().getString(R.string.activity_glue_quanbuxuan));
				selectCheckboxCur.clear();
				isSelected = new LinkedHashMap<Integer, Boolean>();
				for (int i = 0; i < mPointsCur.size(); i++) {
					// 保存在内存中的
					selectCheckboxCur.add(i);
					// TaskMainAdapter中的选择框全设置为true
					isSelected.put(i, true);
				}
				isSelectAll = false;
			}else{
//				tv_selectAll.setText(getResources().getString(R.string.activity_glue_quanxuan));
				selectCheckboxCur.clear();
				isSelectAll = true;
				isSelected = new LinkedHashMap<Integer, Boolean>();
				for (int i = 0; i < mPointsCur.size(); i++) {
					// TaskMainAdapter中的选择框全设置为false
					isSelected.put(i, false);
				}
				isSelectAll = true;
			}
			mAdapter.setIsSelected(isSelected);
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.rl_shezhi:// 任务设置
			Log.d(TAG, "--------->设置");
			arrayIntent = new Intent(this, GlueTaskSettingActivity.class);
			startActivityForResult(arrayIntent, requestCode);
			break;
		case R.id.rl_fangan:// 方案
			Log.d(TAG, "--------->方案");
			processTaskActivity();
			// Log.d(TAG, mPoints.get(selectRadioID).toString());

			break;

		default:
			break;
		}

	}

	/**
	 * 打开进度条对话框
	 */
	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在检查抬起高度...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭进度条对话框
	 */
	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 打开没有进度条的对话框
	 */
	private void startUpLoadDialog() {
		if (uploadDialog == null) {
			uploadDialog = CustomUploadDialog.createDialog(this);
			uploadDialog.setMessage("正在上传中..");
			uploadDialog.setCanceledOnTouchOutside(false);
		}
		uploadDialog.show();
	}

	/**
	 * 关闭没有进度条的对话框
	 */
	private void stopUploadDialog() {
		if (uploadDialog != null) {
			uploadDialog.dismiss();
			uploadDialog = null;
		}
	}

	/**
	 * <p>
	 * Title: DisPlayInfoAfterGetMsg
	 * <p>
	 * Description: 显示收到的数据信息
	 * 
	 * @param revBuffer
	 */
	private void DisPlayInfoAfterGetMsg(byte[] revBuffer) {
		
		switch (MessageMgr.INSTANCE.managingMessage(revBuffer)) {
		
		case 0:
			ToastUtil.displayPromptInfo(TaskActivity.this, "校验失败");
//			sendResetCommand();
			break;
		case 1: {
			int cmdFlag = ((revBuffer[2] & 0x00ff) << 8) | (revBuffer[3] & 0x00ff);
			if (cmdFlag == 0x1a00) {// 若是获取坐标命令返回的数据,解析坐标值
				Point coordPoint = MessageMgr.INSTANCE.analyseCurCoord(revBuffer);
				 Log.d(TAG, "解析坐标值->:"+coordPoint.toString());
				StopSuccessFlag=true;//说明下位机成功返回消息
				StopRetryTimes=5;//重新设置重传次数
				mPointsCur.get(selectRadioIDCur).setX(coordPoint.getX());
				mPointsCur.get(selectRadioIDCur).setY(coordPoint.getY());
				mPointsCur.get(selectRadioIDCur).setZ(coordPoint.getZ());
				mPointsCur.get(selectRadioIDCur).setU(coordPoint.getU());
				mAdapter.setData(mPointsCur);
				mAdapter.notifyDataSetChanged();
			}
			else if (revBuffer[2] == 0x4E) {// 获取下位机参数成功
				ToastUtil.displayPromptInfo(TaskActivity.this, "获取参数成功!");
			}
//			sendResetCommand();
		}
			break;
		case 40101:
			ToastUtil.displayPromptInfo(TaskActivity.this, "非法功能");
//			sendResetCommand();
			break;
		case 40102:
			ToastUtil.displayPromptInfo(TaskActivity.this, "非法数据地址");
//			sendResetCommand();
			break;
		case 40103:
			ToastUtil.displayPromptInfo(TaskActivity.this, "非法数据");
//			sendResetCommand();

			break;
			case 40105:
				ToastUtil.displayPromptInfo(TaskActivity.this, "设备忙");
//			if(prepareReset){
//				/************************ add begin ************************/
//				buffer = new byte[ORDER_BUFFER_LENTH];
//				orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Reset);
//				MessageMgr.INSTANCE.writeData(buffer, orderLength);
//				/************************ end ******************************/
//				prepareReset=false;
//			}
				break;
			case 40109:
				ToastUtil.displayPromptInfo(TaskActivity.this, "急停中");
//			if(prepareReset){
//				/************************ add begin ************************/
//				buffer = new byte[ORDER_BUFFER_LENTH];
//				orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Reset);
//				MessageMgr.INSTANCE.writeData(buffer, orderLength);
//				/************************ end ******************************/
//				prepareReset=false;
//			}

//				sendResetCommand();
				break;
		case 40110:
			ToastUtil.displayPromptInfo(TaskActivity.this, "X轴光电报警");
//			sendResetCommand();
			break;
		case 40111:
			ToastUtil.displayPromptInfo(TaskActivity.this, "Y轴光电报警");
//			sendResetCommand();
			break;
		case 40112:
			ToastUtil.displayPromptInfo(TaskActivity.this, "Z轴光电报警");
//			sendResetCommand();
			break;
		case 40113:
			ToastUtil.displayPromptInfo(TaskActivity.this, "U轴光电报警");
//			sendResetCommand();
			break;
		case 40114:
			ToastUtil.displayPromptInfo(TaskActivity.this, "行程超限报警");
//			sendResetCommand();
			break;
		case 40115:
			ToastUtil.displayPromptInfo(TaskActivity.this, "任务下载失败");
//			sendResetCommand();
			break;
		case 40116:
			ToastUtil.displayPromptInfo(TaskActivity.this, "任务上传失败");
//			sendResetCommand();
			break;
		case 40117:
			ToastUtil.displayPromptInfo(TaskActivity.this, "任务模拟失败");
//			sendResetCommand();
			break;
		case 40118:
			ToastUtil.displayPromptInfo(TaskActivity.this, "示教指令错误");
//			sendResetCommand();
			break;
		case 40119:
			ToastUtil.displayPromptInfo(TaskActivity.this, "循迹定位失败");
//			sendResetCommand();
			break;
		case 40120:
			ToastUtil.displayPromptInfo(TaskActivity.this, "任务号不可用");
//			sendResetCommand();
			break;
		case 40121:
			ToastUtil.displayPromptInfo(TaskActivity.this, "初始化失败");
//			sendResetCommand();
			break;
		case 40122:
			ToastUtil.displayPromptInfo(TaskActivity.this, "API版本错误");
//			sendResetCommand();
			break;
		case 40123:
			ToastUtil.displayPromptInfo(TaskActivity.this, "程序升级失败");
//			sendResetCommand();
			break;
		case 40124:
			ToastUtil.displayPromptInfo(TaskActivity.this, "系统损坏");
//			sendResetCommand();
			break;
		case 40125:
			ToastUtil.displayPromptInfo(TaskActivity.this, "任务未加载");
//			sendResetCommand();
			break;
		case 40126:
			ToastUtil.displayPromptInfo(TaskActivity.this, "(Z轴)基点抬起高度过高");
//			sendResetCommand();
			break;
		case 40127:
			ToastUtil.displayPromptInfo(TaskActivity.this, "等待输入超时");
//			sendResetCommand();
			break;
		default:
			ToastUtil.displayPromptInfo(TaskActivity.this, "未知错误");
//			sendResetCommand();
			break;
		}
	}

	/**
	 * @Title  sendResetCommand
	 * @Description 发送复位命令，跟新ui
	 * @author wj
	 */
	private void sendResetCommand() {
		if(prepareReset){
			/************************ add begin ************************/
			buffer = new byte[ORDER_BUFFER_LENTH];
			orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Reset);
			MessageMgr.INSTANCE.writeData(buffer, orderLength);
			/************************ end ******************************/
			prepareReset=false;
		}
	}

	/**
	 * <p>
	 * Title: RevHandler
	 * <p>
	 * Description: 数据接收Handler
	 * <p>
	 * Company: MingSeal .Ltd
	 * 
	 * @author lyq
	 * @date 2015年11月6日
	 */
	private class RevHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// 如果消息来自子线程
			if (msg.what == SocketInputThread.SocketInputWhat) {
				// 获取下位机上传的数据
				ByteBuffer temp = (ByteBuffer) msg.obj;
				byte[] buffer;
				buffer = temp.array();
				// byte[] revBuffer = (byte[]) msg.obj;
				DisPlayInfoAfterGetMsg(buffer);
			}else if (msg.what== SocketInputThread.SocketError){
				//wifi中断
				System.out.println("wifi连接断开。。");
				SocketThreadManager.releaseInstance();
				System.out.println("单例被释放了-----------------------------");
				//设置全局变量，跟新ui
				userApplication.setWifiConnecting(false);
				WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
				ToastUtil.displayPromptInfo(TaskActivity.this,"wifi连接断开。。");
			}
		}
	}

	/**
	 * 异步线程获取Point点(因为要从数据库中获取数据,费时操作需要异步线程)
	 *
	 */
	private class GetPointsAsynctask extends AsyncTask<Object, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			mPointsCur = pointDao.findALLPointsByIdLists((List<Integer>) params[0]);

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
//				for (Point point : mPointsCur) {
//					mPointStorages.add(point);
//				}
				mPointStorages = PointCopyTools.processCopyPoints(mPointsCur);
				mAdapter.setData(mPointsCur);
				mList.setAdapter(mAdapter);
			}
		}
	}

	/**
	 * 检查抬起高度，往TaskListActivity中传因为要从数据库中读取数据，所以比较耗时，把它放到异步线程中
	 *
	 */
	private class CheckBackUpHeightAsynctask extends AsyncTask<List<Point>, Integer, Integer[]> {

		@Override
		protected void onPreExecute() {
			startProgressDialog();
		}

		@Override
		protected Integer[] doInBackground(List<Point>... params) {
			// result[0] = 1代表正常，result[1] = 0代表有错，
			// result[1]=0代表正常,result[1]=1代表抬起高度,result[1]=2代表面间距,result[1]=3代表圆角半径
			Integer[] result = new Integer[2];
			result[0] = 1;
			result[1] = 0;
			// 独立点参数改为作业点
			PointWeldWorkParam pointWeldWorkParam = null;
			// 线结束点参数
			PointWeldLineEndParam pointWeldLineEndParam = null;

			Point point;
			// 类型
			PointType pointType = PointType.POINT_NULL;
			// Point的任务参数序列
			int id = -1;
			// 保存作业点参数方案主键的List
			List<Integer> aloneIDs = new ArrayList<>();
			// 保存线结束点参数方案主键的List
			List<Integer> lineEndIDs = new ArrayList<>();
//			// 保存线中间点参数方案主键的List
//			List<Integer> lineMidIDs = new ArrayList<>();
//			// 保存面起始点参数方案主键的List
//			List<Integer> faceStartIDs = new ArrayList<>();
//			// 保存面结束点参数方案主键的List
//			List<Integer> faceEndIDs = new ArrayList<>();
			progressDialog.setMax(params[0].size());
			for (int i = 0; i < params[0].size(); i++) {
				point = params[0].get(i);
				pointType = getPointType(point);
				id = point.getPointParam().get_id();
				if (pointType.equals(PointType.POINT_WELD_WORK)) {
					// 如果等于作业点
					if (!aloneIDs.contains(id)) {
						aloneIDs.add(id);
					}
				}  else if (pointType.equals(PointType.POINT_WELD_LINE_END)) {
					// 如果等于线结束点
					if (!lineEndIDs.contains(id)) {
						lineEndIDs.add(id);
					}
				}
			}
			// 获取所有独立点的参数方案
			List<PointWeldWorkParam> aloneParams = weldWorkDao.getWeldWorkParamsByIDs(aloneIDs);
			// 获取所有线结束点的参数方案
			List<PointWeldLineEndParam> lineEndParams = weldLineEndDao.getPointWeldLineEndParamsByIDs(lineEndIDs);
//			// 获取所有线中间点的参数方案
//			List<PointGlueLineMidParam> lineMidParams = weldLineMidDao.getPointGlueLineMidParamsByIDs(lineMidIDs);
//			// 获取所有面起始点的参数方案
//			List<PointGlueFaceStartParam> faceStartParams = glueFaceStartDao.getPointFaceStartParamsByIDs(faceStartIDs);
//			// 获取所有面结束点的参数方案
//			List<PointGlueFaceEndParam> faceEndParams = glueFaceEndDao.getGlueFaceEndParamsByIDs(faceEndIDs);

			// 将方案和对应方案主键放到一个HashMap中
			HashMap<Integer, PointWeldWorkParam> aloneMaps = new HashMap<>();
			for (int i = 0; i < aloneIDs.size(); i++) {
				aloneMaps.put(aloneIDs.get(i), aloneParams.get(i));
			}
			HashMap<Integer, PointWeldLineEndParam> lineEndMaps = new HashMap<>();
			for (int i = 0; i < lineEndIDs.size(); i++) {
				lineEndMaps.put(lineEndIDs.get(i), lineEndParams.get(i));
			}
//			HashMap<Integer, PointGlueFaceEndParam> faceEndMaps = new HashMap<>();
//			for (int i = 0; i < faceEndIDs.size(); i++) {
//				faceEndMaps.put(faceEndIDs.get(i), faceEndParams.get(i));
//			}
//			HashMap<Integer, PointGlueLineMidParam> lineMidMaps = new HashMap<>();
//			for (int i = 0; i < lineMidIDs.size(); i++) {
//				lineMidMaps.put(lineMidIDs.get(i), lineMidParams.get(i));
//			}
//			HashMap<Integer, PointGlueFaceStartParam> faceStartMaps = new HashMap<>();
//			for (int i = 0; i < faceStartIDs.size(); i++) {
//				faceStartMaps.put(faceStartIDs.get(i), faceStartParams.get(i));
//			}
			// 代替之前的每个数据都从数据库里面读取
			for (int i = 0; i < params[0].size(); i++) {
				publishProgress(i);
				point = params[0].get(i);
				pointType = getPointType(point);
				id = point.getPointParam().get_id();
				System.out.println("主界面保存的数据point.getPointParam()："+point.getPointParam());
				if (pointType.equals(PointType.POINT_WELD_WORK)) {
					// 如果等于作业点
					pointWeldWorkParam = aloneMaps.get(id);
					if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < pointWeldWorkParam.getUpHeight()) {
						// Z轴行程小于抬起高度
						selectRadioIDCur = i;
						result[0] = 0;
						result[1] = 1;
						return result;
					}
				} else if (pointType.equals(PointType.POINT_WELD_LINE_END)) {
					// 如果为线结束点
					pointWeldLineEndParam = lineEndMaps.get(id);
					if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < pointWeldLineEndParam.getUpHeight()) {
						// Z轴行程小于抬起高度
						selectRadioIDCur = i;
						result[0] = 0;
						result[1] = 1;
						return result;
					}
				}
			}
			// 说明抬起高度都低于Z轴
			pointDao.deletePoints(mPointStorages);
			rowids = pointDao.insertPoints(mPointsCur);
			mPointsCur = pointDao.findALLPointsByIdLists(rowids);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Integer[] result) {
			// result[0] = 1代表正常，result[1] = 0代表有错，
			// result[1]=0代表正常,result[1]=1代表抬起高度,result[1]=2代表面间距,result[1]=3代表圆角半径
			stopProgressDialog();
			if (result[0] == 1) {

				List<Integer> ids = new ArrayList<Integer>();
				for (Point point : mPointsCur) {
					ids.add(point.getId());
				}

				Bundle extras = new Bundle();
				task.setPointids(ids);
				extras.putParcelable(TaskListActivity.TASK_KEY, task);

				intent.putExtras(extras);
				setResult(TaskListActivity.TASK_ResultCode, intent);
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
			} else {
				if (result[1] == 1) {
					ToastUtil.displayPromptInfo(TaskActivity.this, "抬起高度过高");
				}
				selectCheckboxCur.clear();
				mAdapter.setData(mPointsCur);
				singleSwitch.setChecked(false);// 有错的话设置为单选框显示
				mList.smoothScrollToPosition(selectRadioIDCur);// Listview滚动到指定地方
				mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 模拟任务因为要从数据库中读取数据，所以比较耗时，把它放到异步线程中
	 *
	 */
	private class CheckSimulationUpHeightAsynctask extends AsyncTask<List<Point>, Integer, Integer[]> {

		private ParamsSetting paramSetting;

		@Override
		protected void onPreExecute() {
			startUpLoadDialog();
			paramSetting = new ParamsSetting(TaskActivity.this);
		}

		@Override
		protected Integer[] doInBackground(List<Point>... params) {
			Integer[] result = new Integer[2];
			result[0] = 1;
			result[1] = 0;
			paramSetting.setParamsToApplication(userApplication, params[0]);
			Point point;
			// 类型
			PointType pointType = PointType.POINT_NULL;
			// Point的任务参数序列
			int id = -1;
			// 代替之前的每个数据都从数据库里面读取
			for (int i = 0; i < params[0].size(); i++) {
				publishProgress(i);
				point = params[0].get(i);
				pointType = getPointType(point);
				id = point.getPointParam().get_id();
				if (pointType.equals(PointType.POINT_WELD_WORK)) {
					// 如果等于作业点
					if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < userApplication.getAloneParamMaps().get(id)
							.getUpHeight()) {
						// Z轴行程小于抬起高度
						selectRadioIDCur = i;
						result[0] = 0;
						result[1] = 1;
						return result;
					}
				} else if (pointType.equals(PointType.POINT_WELD_LINE_END)) {
					// 如果为线结束点
					if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < userApplication.getLineEndParamMaps().get(id)
							.getUpHeight()) {
						// Z轴行程小于抬起高度
						selectRadioIDCur = i;
						result[0] = 0;
						result[1] = 1;
						return result;
					}
				}
			}

			return result;

		}

		@Override
		protected void onPostExecute(Integer[] result) {
			stopUploadDialog();
			// result[0] = 1代表正常，result[0] = 0代表有错，
			// result[1]=0代表正常,result[1]=1代表抬起高度,result[1]=2代表面间距,result[1]=3代表圆角半径
			if (result[0] == 1) {
				MessageMgr.INSTANCE.setUserApplication(userApplication);
				TaskParam.INSTANCE.setAllParamBacktoDefault();
				// TaskParam.INSTANCE.setnStartX(mPoints.get(0).getX());
				// TaskParam.INSTANCE.setnStartY(mPoints.get(0).getY());
				// TaskParam.INSTANCE.setnStartZ(mPoints.get(0).getZ());
				// TaskParam.INSTANCE.setnStartU(mPoints.get(0).getU());
				TaskParam.INSTANCE.setStrTaskName("");
				MessageMgr.INSTANCE.taskDownloadDemo(mPointsCur);
			} else {
				if (result[1] == 1) {
					ToastUtil.displayPromptInfo(TaskActivity.this, "抬起高度过高");
				}
				selectCheckboxCur.clear();
				mAdapter.setData(mPointsCur);
				singleSwitch.setChecked(false);// 有错的话设置为单选框显示
				mList.smoothScrollToPosition(selectRadioIDCur);// Listview滚动到指定地方
				mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 检查抬起高度，往GlueDownloadActivity中传因为要从数据库中读取数据，所以比较耗时，把它放到异步线程中
	 *
	 */
	private class CheckUpHeightAsynctask extends AsyncTask<List<Point>, Integer, Integer[]> {

		private ParamsSetting paramSetting;

		@Override
		protected void onPreExecute() {
			startProgressDialog();
			paramSetting = new ParamsSetting(TaskActivity.this);
		}

		@Override
		protected Integer[] doInBackground(List<Point>... params) {
			// result[0] = 1代表正常，result[0] = 0代表有错，
			// result[1]=0代表正常,result[1]=1代表抬起高度,result[1]=2代表面间距,result[1]=3代表圆角半径
			Integer[] result = new Integer[2];
			result[0] = 1;
			result[1] = 0;
			int max = params[0].size();
			progressDialog.setMax(max);
			publishProgress(0);
			paramSetting.setParamsToApplication(userApplication, params[0]);

			Point point;
			// 类型
			PointType pointType = PointType.POINT_NULL;
			// Point的任务参数序列
			int id = -1;
			// 代替之前的每个数据都从数据库里面读取
			for (int i = 0; i < params[0].size(); i++) {
				publishProgress(i);
				point = params[0].get(i);
				pointType = getPointType(point);
				id = point.getPointParam().get_id();
				if (pointType.equals(PointType.POINT_WELD_WORK)) {
					// 如果等于作业点
					if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < userApplication.getAloneParamMaps().get(id)
							.getUpHeight()) {
						// Z轴行程小于抬起高度
						selectRadioIDCur = i;
						result[0] = 0;
						result[1] = 1;
						return result;
					}
				} else if (pointType.equals(PointType.POINT_WELD_LINE_END)) {
					// 如果为线结束点
					if (RobotParam.INSTANCE.ZPulse2Journey(point.getZ()) < userApplication.getLineEndParamMaps().get(id)
							.getUpHeight()) {
						// Z轴行程小于抬起高度
						selectRadioIDCur = i;
						result[0] = 0;
						result[1] = 1;
						return result;
					}
				}
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Integer[] result) {
			// result[0] = 1代表正常，result[1] = 0代表有错，
			// result[1]=0代表正常,result[1]=1代表抬起高度,result[1]=2代表面间距,result[1]=3代表圆角半径
			stopProgressDialog();
			if (result[0] == 1) {
				Bundle extras = new Bundle();
				if (mPointsCur.size() > MAX_SIZE) {
					extras.putString(KEY_NUMBER, "0");
					userApplication.setPoints(mPointsCur);
				} else {
					extras.putString(KEY_NUMBER, "1");
					extras.putParcelableArrayList(TaskActivity.DOWNLOAD_KEY, (ArrayList<? extends Parcelable>) mPointsCur);
				}
				Intent _intent = new Intent(TaskActivity.this, GlueDownloadActivity.class);
				extras.putString(TaskActivity.DOWNLOAD_NUMBER_KEY, task.getTaskName());
				_intent.putExtras(extras);
				startActivityForResult(_intent, requestCode);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
			} else {
				if (result[1] == 1) {
					ToastUtil.displayPromptInfo(TaskActivity.this, "抬起高度过高");
				}
				selectCheckboxCur.clear();
				mAdapter.setData(mPointsCur);
				singleSwitch.setChecked(false);// 有错的话设置为单选框显示
//				mList.smoothScrollToPosition(selectRadioIDCur);// Listview滚动到指定地方
				mList.setSelection(selectRadioIDCur);//改为快速定位
				mAdapter.setSelectID(selectRadioIDCur);// 选中位置
				mAdapter.notifyDataSetInvalidated();//
			}
		}

	}

	/**
	 * @description 检查中间点的圆弧半径
	 * @param list
	 *            Point的List表
	 * @param _idx
	 * @param radius
	 * @return false-圆角半径过大,true-正常
	 */
	private boolean checkRadiusPoint(List<Point> list, int _idx, float radius) {
		SMatrix1_4[] m = new SMatrix1_4[3];
		if (getPointType(list.get(_idx - 1)) == PointType.POINT_GLUE_LINE_ARC) {
			if (getPointType(list.get(_idx + 1)) == PointType.POINT_GLUE_LINE_ARC) {
				SMatrix1_4 _m1 = new SMatrix1_4(list.get(_idx - 2).getX(), list.get(_idx - 2).getY(),
						list.get(_idx - 2).getZ());
				SMatrix1_4 _m2 = new SMatrix1_4(list.get(_idx - 1).getX(), list.get(_idx - 1).getY(),
						list.get(_idx - 1).getZ());
				SMatrix1_4 _m3 = new SMatrix1_4(list.get(_idx).getX(), list.get(_idx).getY(), list.get(_idx).getZ());
				SMatrix1_4 _m4 = new SMatrix1_4(list.get(_idx + 1).getX(), list.get(_idx + 1).getY(),
						list.get(_idx + 1).getZ());
				SMatrix1_4 _m5 = new SMatrix1_4(list.get(_idx + 2).getX(), list.get(_idx + 2).getY(),
						list.get(_idx + 2).getZ());
				float _r = RobotParam.INSTANCE.XJourney2Pulse(radius);
				if (!CommonArithmetic.getArcPtByR(_m1, _m2, _m3, _m4, _m5, _r, m)) {
					// 圆角半径过大
					selectRadioIDCur = _idx;
					return false;
				}
			} else {
				SMatrix1_4 _m1 = new SMatrix1_4(list.get(_idx + 1).getX(), list.get(_idx + 1).getY(),
						list.get(_idx + 1).getZ());
				SMatrix1_4 _m2 = new SMatrix1_4(list.get(_idx).getX(), list.get(_idx).getY(), list.get(_idx).getZ());
				SMatrix1_4 _m3 = new SMatrix1_4(list.get(_idx - 1).getX(), list.get(_idx - 1).getY(),
						list.get(_idx - 1).getZ());
				SMatrix1_4 _m4 = new SMatrix1_4(list.get(_idx - 2).getX(), list.get(_idx - 2).getY(),
						list.get(_idx - 2).getZ());
				float _r = RobotParam.INSTANCE.XJourney2Pulse(radius);
				if (!CommonArithmetic.getArcPtByR(_m1, _m2, _m3, _m4, _r, m)) {
					// 圆角半径过大
					selectRadioIDCur = _idx;
					return false;
				}
			}
		} else {
			if (getPointType(list.get(_idx + 1)) == PointType.POINT_GLUE_LINE_ARC) {
				SMatrix1_4 _m1 = new SMatrix1_4(list.get(_idx - 1).getX(), list.get(_idx - 1).getY(),
						list.get(_idx - 1).getZ());
				SMatrix1_4 _m2 = new SMatrix1_4(list.get(_idx).getX(), list.get(_idx).getY(), list.get(_idx).getZ());
				SMatrix1_4 _m3 = new SMatrix1_4(list.get(_idx + 1).getX(), list.get(_idx + 1).getY(),
						list.get(_idx + 1).getZ());
				SMatrix1_4 _m4 = new SMatrix1_4(list.get(_idx + 2).getX(), list.get(_idx + 2).getY(),
						list.get(_idx + 2).getZ());
				float _r = RobotParam.INSTANCE.XJourney2Pulse(radius);
				if (!CommonArithmetic.getArcPtByR(_m1, _m2, _m3, _m4, _r, m)) {
					// 圆角半径过大
					selectRadioIDCur = _idx;
					return false;
				}
			} else {
				SMatrix1_4 m1 = new SMatrix1_4(list.get(_idx - 1).getX(), list.get(_idx - 1).getY(),
						list.get(_idx - 1).getZ());
				SMatrix1_4 m2 = new SMatrix1_4(list.get(_idx).getX(), list.get(_idx).getY(), list.get(_idx).getZ());
				SMatrix1_4 m3 = new SMatrix1_4(list.get(_idx + 1).getX(), list.get(_idx + 1).getY(),
						list.get(_idx + 1).getZ());
				float nRadius = RobotParam.INSTANCE.XJourney2Pulse(radius);
				float angle = CommonArithmetic.getAngle(m1, m2, m3);
				SMatrix1_4 m2_1 = SMatrix1_4.operator_minus(m2, m1);
				SMatrix1_4 m3_2 = SMatrix1_4.operator_minus(m3, m2);
				// double 强转成float
				float len12 = (float) SMatrix1_4.operator_mod3(m2_1);
				float len23 = (float) SMatrix1_4.operator_mod3(m3_2);
				float lenMin = len12 < len23 ? len12 : len23;
				float rMax = (float) (lenMin * Math.tan(angle * Math.PI / 360));
				if (nRadius > rMax) {
					// 圆角半径过大
					selectRadioIDCur = _idx;
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 检查圆弧点共线问题
	 * 
	 * @param _pt1
	 *            起始点
	 * @param _pt2
	 *            圆弧点
	 * @param _pt3
	 *            结束点
	 * @return
	 */
	private boolean checkArcPoint(Point _pt1, Point _pt2, Point _pt3) {
		if (_pt1.getX() == _pt2.getX() && _pt1.getY() == _pt2.getY() && _pt1.getZ() == _pt2.getZ()
				&& _pt1.getU() == _pt2.getU()) {
			// 前一个点-圆弧点重合
			ToastUtil.displayPromptInfo(this, _pt1,"-圆弧点重合！");
			return false;
		}
		if (_pt3.getX() == _pt2.getX() && _pt3.getY() == _pt2.getY() && _pt3.getZ() == _pt2.getZ()
				&& _pt3.getU() == _pt2.getU()) {
			// 后一个点-结束点重合
			ToastUtil.displayPromptInfo(this, _pt3,"-圆弧点重合！");
			return false;
		}
		if (_pt3.getX() == _pt1.getX() && _pt3.getY() == _pt1.getY() && _pt3.getZ() == _pt1.getZ()
				&& _pt3.getU() == _pt1.getU()) {
			// 前一个点-后一个点重合
			ToastUtil.displayPromptInfo(this, _pt1,_pt3,"重合！");
			return false;
		}
		SMatrix1_4 m1 = new SMatrix1_4(_pt1.getX(), _pt1.getY(), _pt1.getZ());
		SMatrix1_4 m2 = new SMatrix1_4(_pt2.getX(), _pt2.getY(), _pt2.getZ());
		SMatrix1_4 m3 = new SMatrix1_4(_pt3.getX(), _pt3.getY(), _pt3.getZ());
		SMatrix1_4 m2_1 = SMatrix1_4.operator_minus(m2, m1);
		SMatrix1_4 m3_2 = SMatrix1_4.operator_minus(m3, m2);
		SMatrix1_4 n = SMatrix1_4.operator_cross3(m2_1, m3_2);
		if (n.getX() == 0 && n.getY() == 0 && n.getZ() == 0) {
			//
			ToastUtil.displayPromptInfo(this, _pt1,_pt3,"-圆弧点三点共线！");
			return false;
		}
		return true;
	}


}
