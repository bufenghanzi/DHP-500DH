/**
 *
 */
package com.mingseal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mingseal.application.UserApplication;
import com.mingseal.communicate.NetManager;
import com.mingseal.communicate.SocketInputThread;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.manager.MessageMgr;
import com.mingseal.data.param.SettingParam;
import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointType;
import com.mingseal.dhp_500dh.R;
import com.mingseal.listener.MaxMinEditFloatWatcher;
import com.mingseal.listener.MaxMinFocusChangeFloatListener;
import com.mingseal.utils.FloatUtil;
import com.mingseal.utils.MoveUtils;
import com.mingseal.utils.PointCopyTools;
import com.mingseal.utils.SharePreferenceUtils;
import com.mingseal.utils.ToastUtil;
import com.mingseal.utils.WifiConnectTools;
import com.mingseal.view.CustomView;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 商炎炳
 */
public class GlueViewActivity extends AutoLayoutActivity implements OnClickListener {

	private static final String TAG = "GlueViewActivity";

	/**
	 * 绘图区域
	 */
	private CustomView customView;
	/**
	 * x编辑框
	 */
	private EditText et_x;
	/**
	 * y编辑框
	 */
	private EditText et_y;
	/**
	 * z编辑框
	 */
	private EditText et_z;
	/**
	 * u编辑框
	 */
	private EditText et_u;
//	/**
//	 * 控制x,y变化的导航杆
//	 */
//	private MyCircleView myCircleUp;
//	/**
//	 * 移动点的导航杆
//	 */
//	private MyCircleView myCircleDown;
	/**
	 * 标题栏
	 */
	private TextView tv_title;
	/**
	 * 返回
	 */
	private RelativeLayout rl_back;
	private RelativeLayout rl_title_speed;// 标题栏的速度布局
	private RelativeLayout rl_title_moshi;// 标题栏的模式布局
	private RelativeLayout rl_title_wifi_connecting;//wifi连接情况
	private ImageView image_speed;// 标题栏的速度
	private ImageView image_moshi;// 标题栏的模式
	/**
	 * 速度
	 */
	private RelativeLayout rl_sudu;
	/**
	 * 模式
	 */
	private RelativeLayout rl_moshi;
	/**
	 * 完成
	 */
	private RelativeLayout rl_complete;
	/**
	 * @Fields tv_speed: 速度文本
	 */
	private TextView tv_speed;
	/**
	 * @Fields tv_moshi: 模式文本
	 */
	private TextView tv_moshi;

	private List<Point> pointListsCur = new ArrayList<>();// 接收从TaskActivity传递过来的Point列表
	private List<Point> pointListsFirst = new ArrayList<>();// 用于比较有没有变化
	/**
	 * 接收从TaskActivity传递过来的类型(0代表大数据,1代表小数据)
	 */
	private String numberType;// 接收从TaskActivity传递过来的类型(0代表大数据,1代表小数据)
	private Point point;

	private int speedFlag = 0;// 判断点击了几次，三次一循环，分别为高速33,中速13,低速3
	private int speed;
	private int[] speedXYZ;// 单步的话保存x,y,z的坐标
	private SettingParam settingParam;// 任务设置参数
	/**
	 * 连续0(默认),单步是1
	 */
	private int modeFlag = 0;// 连续单步判断标志
	/**
	 * 上次选中的点的position(判断是否需要定位)
	 */
	private int selectAssignPosition = -1;// 单选框上次选中的ID(判断是否需要定位)
	/**
	 * @Fields m_nAxisNum : 机器轴数
	 */
	private int m_nAxisNum;
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

	/**
	 * 从TaskActivity传过来的Intent
	 */
	private Intent intent;

	private RevHandler handler;
	private UserApplication userApplication;
	/**
	 * @Fields isChange: 判断List是否改变,true为没有变化,false表示有变化
	 */
	private boolean isChange = true;

	/**
	 * @Fields iv_wifi_connecting: wifi连接情况
	 */
	private ImageView iv_wifi_connecting;
	boolean StopFlag = false;//是否在重发状态
	boolean StopSuccessFlag = false;//停止成功标记
	private int StopRetryTimes = 5;//重传次数
	Timer mTimer;
	TimerTask mTimerTask;
	private TextView mTv_u;
	private float mTemp_x;
	private float mTemp_y;
	private float mTemp_z;
	private float mTemp_u;
	private static int KEY_X = 0;
	private static int KEY_Y = 1;
	private static int KEY_Z = 2;
	private static int KEY_U = 3;
	private TextView mTv_point_num;
	private TextView mTv_point_type;
	private TextView tv_x;
	private TextView tv_y;
	private TextView tv_z;
	private TextView tv_u;
	private int num=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view);
		userApplication = (UserApplication) getApplication();
		intent = getIntent();
		numberType = intent.getStringExtra(TaskActivity.KEY_NUMBER);
		if ("0".equals(numberType)) {
			pointListsCur = userApplication.getPoints();
		} else if ("1".equals(numberType)) {
			pointListsCur = intent.getParcelableArrayListExtra(TaskActivity.ARRAY_KEY);
		}

		//复制List
		pointListsFirst = PointCopyTools.processCopyPoints(pointListsCur);

		settingParam = SharePreferenceUtils.readFromSharedPreference(this);
		initView();
		handler = new RevHandler();
		// 线程管理单例初始化
		SocketThreadManager.sharedInstance().setInputThreadHandler(handler);
		NetManager.instance().init(this);
		// initPoint();

		customView.setPoints(pointListsCur);

		setCoords();
		m_nAxisNum = RobotParam.INSTANCE.getM_nAxisNum();
		if (m_nAxisNum == 3) {
			et_u.setEnabled(false);
			et_u.setVisibility(View.INVISIBLE);
			mTv_u.setVisibility(View.INVISIBLE);
//			myCircleDown.setRow("");
		} else {
			et_u.setEnabled(true);
			et_u.setVisibility(View.VISIBLE);
			mTv_u.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 加载组件
	 */
	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getResources().getString(R.string.activity_glue_shitu));
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		rl_back.setOnClickListener(this);
		rl_title_speed = (RelativeLayout) findViewById(R.id.rl_title_speed);
		rl_title_moshi = (RelativeLayout) findViewById(R.id.rl_title_moshi);
		iv_wifi_connecting = (ImageView) findViewById(R.id.iv_title_wifi_connecting);
		WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
		// 让RelativeLayout显示
		rl_title_speed.setVisibility(View.VISIBLE);
		rl_title_moshi.setVisibility(View.VISIBLE);
		image_speed = (ImageView) findViewById(R.id.iv_title_speed);
		image_moshi = (ImageView) findViewById(R.id.iv_title_moshi);
		// 设置初值
		mTv_point_num = (TextView) findViewById(R.id.tv_point_num);
		mTv_point_num.setText(num+":");
		mTv_point_type = (TextView) findViewById(R.id.tv_point_type);
		mTv_point_type.setText(PointType.getTypeName(pointListsCur.get(0).getPointParam().getPointType().getValue()));
		tv_x = (TextView) findViewById(R.id.tv_x);
		tv_y = (TextView) findViewById(R.id.tv_y);
		tv_z = (TextView) findViewById(R.id.tv_z);
		tv_u = (TextView) findViewById(R.id.tv_u);
		image_speed.setBackgroundResource(R.drawable.icon_speed_high);
		image_moshi.setBackgroundResource(R.drawable.icon_step_serious);
		rl_sudu = (RelativeLayout) findViewById(R.id.rl_sudu);
		tv_speed = (TextView) rl_sudu.findViewById(R.id.tv_view_speed);
		rl_sudu.setOnClickListener(this);
		rl_moshi = (RelativeLayout) findViewById(R.id.rl_moshi);
		tv_moshi = (TextView) findViewById(R.id.tv_view_moshi);
		rl_moshi.setOnClickListener(this);
		rl_complete = (RelativeLayout) findViewById(R.id.rl_complete);
		rl_complete.setOnClickListener(this);
		customView = (CustomView) findViewById(R.id.customView);
		et_x = (EditText) findViewById(R.id.et_x);
		et_y = (EditText) findViewById(R.id.et_y);
		et_z = (EditText) findViewById(R.id.et_z);
		et_u = (EditText) findViewById(R.id.et_u);
		et_x.addTextChangedListener(new MaxMinEditFloatWatcher(
				RobotParam.INSTANCE.GetXJourney(),
				0, et_x));
		et_x.setOnFocusChangeListener(new MaxMinFocusChangeFloatListener(
				RobotParam.INSTANCE.GetXJourney(),
				0, et_x));
		et_x.setOnEditorActionListener(new OnKeyEditorActionListener());
		et_x.setSelectAllOnFocus(true);
		et_y.addTextChangedListener(new MaxMinEditFloatWatcher(
				RobotParam.INSTANCE.GetYJourney(),
				0, et_y));
		et_y.setOnFocusChangeListener(new MaxMinFocusChangeFloatListener(
				RobotParam.INSTANCE.GetYJourney(),
				0, et_y));
		et_y.setOnEditorActionListener(new OnKeyEditorActionListener());
		et_y.setSelectAllOnFocus(true);
		et_z.addTextChangedListener(new MaxMinEditFloatWatcher(
				RobotParam.INSTANCE.GetZJourney(),
				0, et_z));
		et_z.setOnFocusChangeListener(new MaxMinFocusChangeFloatListener(
				RobotParam.INSTANCE.GetZJourney(),
				0, et_z));
		et_z.setOnEditorActionListener(new OnKeyEditorActionListener());

		et_z.setSelectAllOnFocus(true);

		et_u.addTextChangedListener(new MaxMinEditFloatWatcher(
				RobotParam.INSTANCE.GetUJourney(),
				0, et_u));
		et_u.setOnFocusChangeListener(new MaxMinFocusChangeFloatListener(
				RobotParam.INSTANCE.GetUJourney(),
				0, et_u));
		et_u.setOnEditorActionListener(new OnKeyEditorActionListener());

		et_u.setSelectAllOnFocus(true);
		but_x_plus = (Button) findViewById(R.id.nav_x_plus);
		but_x_minus = (Button) findViewById(R.id.nav_x_minus);
		but_y_plus = (Button) findViewById(R.id.nav_y_plus);
		but_y_minus = (Button) findViewById(R.id.nav_y_minus);
		but_z_plus = (Button) findViewById(R.id.nav_z_plus);
		but_z_minus = (Button) findViewById(R.id.nav_z_minus);
		but_u_plus = (Button) findViewById(R.id.nav_u_plus);
		but_u_minus = (Button) findViewById(R.id.nav_u_minus);

		but_u_plus.setText("↓");
		but_u_minus.setText("↑");
		mTv_u = (TextView) findViewById(R.id.tv_u);
        /*===================== begin =====================*/
		but_x_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_x_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_y_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_y_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_z_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_z_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_u_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		but_u_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(45));
		tv_x.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_y.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_z.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		tv_u.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_point_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		mTv_point_type.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(40));
		/*=====================  end =====================*/

		MoveListener moveListener = new MoveListener();
		//添加触摸事件
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

	}

	/**
	 * 自定义的OnEditorActionListener,软键盘输入回车，将数据保存到List集合中
	 */
	private class OnKeyEditorActionListener implements TextView.OnEditorActionListener {

		/**
		 * 软键盘输入回车，将数据保存到List集合中
		 */
		public OnKeyEditorActionListener() {

		}

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				SetDatasAndUpdateUI();
			}

			return false;
		}

	}

	/**
	 * 设置X,Y,Z,U的坐标值
	 */
	private void setCoords() {
		et_x.setText(FloatUtil.getFloatToString(
				customView.getPointByAssignPosition().getX()));
		et_y.setText(FloatUtil.getFloatToString(
				customView.getPointByAssignPosition().getY()));
		et_z.setText(FloatUtil.getFloatToString(
				customView.getPointByAssignPosition().getZ()));
		et_u.setText(FloatUtil.getFloatToString(
				customView.getPointByAssignPosition().getU()));
	}


	/**
	 * @author 商炎炳
	 * @ClassName MoveListener
	 * @Description x, y, z, u移动
	 * @date 2016年1月28日 下午3:29:26
	 */
	private class MoveListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.nav_u_plus) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					++num;
					if (num>=pointListsCur.size()){
						num=pointListsCur.size();
						mTv_point_num.setText(num+":");
						mTv_point_type.setText(PointType.getTypeName(pointListsCur.get(num-1).getPointParam().getPointType().getValue()));
					}else {
						mTv_point_num.setText(num+":");
						mTv_point_type.setText(PointType.getTypeName(pointListsCur.get(num-1).getPointParam().getPointType().getValue()));
					}
					SetDatasAndUpdateUI();
					customView.setAssignPosition(1);
					setCoords();

				}
			} else if (v.getId() == R.id.nav_u_minus) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					--num;
					if (num<1){
						num=1;
						mTv_point_num.setText(num+":");
						mTv_point_type.setText(PointType.getTypeName(pointListsCur.get(num-1).getPointParam().getPointType().getValue()));
					}else {
						mTv_point_num.setText(num+":");
						mTv_point_type.setText(PointType.getTypeName(pointListsCur.get(num-1).getPointParam().getPointType().getValue()));
					}
					SetDatasAndUpdateUI();
					customView.setAssignPosition(-1);
					setCoords();
				}

			} else {
				// 不为u轴控制,需要判断是否是当前点
				if (selectAssignPosition == customView.getAssignPosition()) {
					if (modeFlag == 0) {
						// 连续
						switch (v.getId()) {
							case R.id.nav_x_plus:// x+
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									MoveUtils.move(0, 0, 0, speed);
									stopTimer();
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									MoveUtils.stop(0);
									prepareStopRetry(0);
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
						}
					} else if (modeFlag == 1) {
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
						}
					}
				} else {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						MoveUtils.locationCoord(pointListsCur.get(customView.getAssignPosition()));
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						// 抬起的时候把设置上次选中的单选框
						selectAssignPosition = customView.getAssignPosition();
					}
				}
			}
			return false;
		}

	}

	/**
	 * //如果直接手动修改了坐标，暂时保存
	 */
	private void SetDatasAndUpdateUI() {
		try {
			mTemp_x = Float.parseFloat(et_x.getText().toString());
		} catch (NumberFormatException e) {
			mTemp_x = 0;
		}
		try {
			mTemp_y = Float.parseFloat(et_y.getText().toString());
		} catch (NumberFormatException e) {
			mTemp_y = 0;
		}
		try {
			mTemp_z = Float.parseFloat(et_z.getText().toString());
		} catch (NumberFormatException e) {
			mTemp_z = 0;
		}
		try {
			mTemp_u = Float.parseFloat(et_u.getText().toString());
		} catch (NumberFormatException e) {
			mTemp_u = 0;
		}
		pointListsCur.get(customView.getAssignPosition()).setX(mTemp_x);
		pointListsCur.get(customView.getAssignPosition()).setY(mTemp_y);
		pointListsCur.get(customView.getAssignPosition()).setZ(mTemp_z);
		pointListsCur.get(customView.getAssignPosition()).setU(mTemp_u);
		customView.setPoints(pointListsCur);// 重绘图
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
	 *
	 * @param i
	 */
	private void prepareStopRetry(final int i) {
		StopRetryTimes = 5;//重新设置重传次数
		StopSuccessFlag = false;//重置标记为
		StopFlag = false;//非重发停止指令状态
		if (mTimer == null) {

			mTimer = new Timer();
		}
		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					if (StopSuccessFlag == false) {
						if (StopRetryTimes > 0) {
							if (StopSuccessFlag == false) {
								StopRetryTimes--;
								MoveUtils.stop(i);
//								Log.d(TAG, "重发了停止指令");
								StopFlag = true;
							}
						} else {
							//重发失败
//							Log.d(TAG,"重发失败！");
							//关闭timer，重置参数
							StopRetryTimes = 5;//重新设置重传次数
							StopSuccessFlag = false;//重置标记为
							StopFlag = false;//非重发停止指令状态
							mTimer.cancel();
							mTimer = null;
							mTimerTask.cancel();
							mTimerTask = null;
						}
					} else {
						if (StopFlag) {//重发状态
							//成功
//								Log.d(TAG, "重发了停止指令成功！");
							StopSuccessFlag = false;
							StopRetryTimes = 5;//重新设置重传次数
							StopSuccessFlag = false;//重置标记为
							StopFlag = false;//非重发停止指令状态
							mTimer.cancel();
							mTimer = null;
							mTimerTask.cancel();
							mTimerTask = null;
						}
					}
				}
			};
		}
		if (mTimer != null && mTimerTask != null) {
			mTimer.schedule(mTimerTask, 220, 60);
		}
	}

	/**
	 * 保存页面中的信息并返回到之前的TaskActivity
	 */
	private void saveBackActivity() {
		Bundle extras = new Bundle();
		if (pointListsCur.size() > TaskActivity.MAX_SIZE) {
			extras.putString(TaskActivity.KEY_NUMBER, "0");
			userApplication.setPoints(pointListsCur);
		} else {
			extras.putString(TaskActivity.KEY_NUMBER, "1");
			extras.putParcelableArrayList(TaskActivity.VIEW_KEY, (ArrayList<? extends Parcelable>) pointListsCur);
		}

		intent.putExtras(extras);
		setResult(TaskActivity.resultViewCode, intent);
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
	}

	/**
	 * 点击返回按钮响应事件
	 */
	private void showBackDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(GlueViewActivity.this);
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
				GlueViewActivity.this.finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
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

	private void DisPlayInfoAfterGetMsg(byte[] revBuffer) {
		switch (MessageMgr.INSTANCE.managingMessage(revBuffer)) {
			case 0:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "校验失败");
				break;
			case 1: {
				int cmdFlag = ((revBuffer[2] & 0x00ff) << 8) | (revBuffer[3] & 0x00ff);
				if (cmdFlag == 0x1a00) {// 若是获取坐标命令返回的数据,解析坐标值
					Point coordPoint = MessageMgr.INSTANCE.analyseCurCoord(revBuffer);
					StopSuccessFlag = true;//说明下位机成功返回消息
					StopRetryTimes = 5;//重新设置重传次数
					pointListsCur.get(customView.getAssignPosition()).setX(coordPoint.getX());
					pointListsCur.get(customView.getAssignPosition()).setY(coordPoint.getY());
					pointListsCur.get(customView.getAssignPosition()).setZ(coordPoint.getZ());
					pointListsCur.get(customView.getAssignPosition()).setU(coordPoint.getU());
					customView.setPoints(pointListsCur);// 重绘图
					setCoords();

				} else if (revBuffer[2] == 0x4A) {// 获取下位机参数成功
					ToastUtil.displayPromptInfo(GlueViewActivity.this, "获取参数成功!");
				}
			}
			break;
			case 40101:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "非法功能");
				break;
			case 40102:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "非法数据地址");
				break;
			case 40103:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "非法数据");
				break;
			case 40105:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "设备忙");
				break;
			case 40109:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "急停中");
				break;
			case 40110:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "X轴光电报警");
				break;
			case 40111:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "Y轴光电报警");
				break;
			case 40112:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "Z轴光电报警");
				break;
			case 40113:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "U轴光电报警");
				break;
			case 40114:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "行程超限报警");
				break;
			case 40115:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "任务下载失败");
				break;
			case 40116:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "任务上传失败");
				break;
			case 40117:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "任务模拟失败");
				break;
			case 40118:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "示教指令错误");
				break;
			case 40119:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "循迹定位失败");
				break;
			case 40120:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "任务号不可用");
				break;
			case 40121:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "初始化失败");
				break;
			case 40122:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "API版本错误");
				break;
			case 40123:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "程序升级失败");
				break;
			case 40124:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "系统损坏");
				break;
			case 40125:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "任务未加载");
				break;
			case 40126:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "(Z轴)基点抬起高度过高");
				break;
			case 40127:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "等待输入超时");
				break;
			default:
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "未知错误");
				break;
		}
	}

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
			} else if (msg.what == SocketInputThread.SocketError) {
				//wifi中断
				SocketThreadManager.releaseInstance();
				//设置全局变量，跟新ui
				userApplication.setWifiConnecting(false);
				WifiConnectTools.processWifiConnect(userApplication, iv_wifi_connecting);
				ToastUtil.displayPromptInfo(GlueViewActivity.this, "wifi连接断开。。");
			}
		}
	}


	/**
	 * @param settingParam
	 * @Title saveMediumSpeed
	 * @Description 单步和连续的速度都改成中速的，且将文本提示设置成中速
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
	 * @param settingParam
	 * @Title saveLowSpeed
	 * @Description 单步和连续的速度都改成低速的，且将文本提示设置成低速
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
	 * @param settingParam
	 * @Title saveHighSpeed
	 * @Description 单步和连续的速度都改成高速的，且将文本提示设置成高速
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isChange = PointCopyTools.comparePoints(pointListsFirst, pointListsCur);
			if (isChange) {
//				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.data_not_changed));
				saveBackActivity();
//				showBackDialog();
//				GlueViewActivity.this.finish();
			} else {
				showBackDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_back:// 返回
				SetDatasAndUpdateUI();
				isChange = PointCopyTools.comparePoints(pointListsFirst, pointListsCur);
				if (isChange) {
//				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.data_not_changed));
					saveBackActivity();
//				showBackDialog();
//				GlueViewActivity.this.finish();
				} else {
					showBackDialog();
				}
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
			case R.id.rl_moshi:// 模式
				if (modeFlag == 0) {
					modeFlag = 1;// 点击一次变为单步
					ToastUtil.displayPromptInfo(this, getResources().getString(R.string.step_single));
					image_moshi.setBackgroundResource(R.drawable.icon_step_single);
					tv_moshi.setText(getResources().getString(R.string.step_single));
				} else {
					modeFlag = 0;// 默认为0
					ToastUtil.displayPromptInfo(this, getResources().getString(R.string.step_serious));
					image_moshi.setBackgroundResource(R.drawable.icon_step_serious);
					tv_moshi.setText(getResources().getString(R.string.step_serious));
				}
				break;
			case R.id.rl_complete:// 完成
				SetDatasAndUpdateUI();
				isChange = PointCopyTools.comparePoints(pointListsFirst, pointListsCur);
				if (isChange) {
//				ToastUtil.displayPromptInfo(this, getResources().getString(R.string.data_not_changed));
//				GlueViewActivity.this.finish();
//				showBackDialog();
					saveBackActivity();
				} else {
					showBackDialog();
				}
				break;

		}
	}

}
