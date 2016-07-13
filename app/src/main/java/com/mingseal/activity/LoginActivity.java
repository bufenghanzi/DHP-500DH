/**
 * 
 */
package com.mingseal.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.mingseal.adapter.LoginSpinnerAdapter;
import com.mingseal.application.UserApplication;
import com.mingseal.communicate.NetManager;
import com.mingseal.communicate.SocketInputThread;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.dao.UserDao;
import com.mingseal.data.dao.WiFiDao;
import com.mingseal.data.manager.MessageMgr;
import com.mingseal.data.param.CmdParam;
import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.protocol.Protocol_400_1;
import com.mingseal.data.user.User;
import com.mingseal.dhp_500dh.R;
import com.mingseal.utils.ToastUtil;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author wangjian
 *
 */
public class LoginActivity extends AutoLayoutActivity implements OnClickListener {
	private final static String TAG = "LoginActivity";

	/**
	 * 管理员/游客
	 */
	private Spinner sp_admin;
	/**
	 * 用户名
	 */
	private EditText et_username;
	/**
	 * 密码框
	 */
	private EditText et_password;
	/**
	 * 隐藏/显示密码框
	 */
	private ImageView iv_showPassword;

	/**
	 * 登录
	 */
	private RelativeLayout rl_login;
	/**
	 * 数组适配器
	 */
	private LoginSpinnerAdapter spinnerAdapter;
	/**
	 * 是否隐藏
	 */
	private boolean isHidden = true;
	/**
	 * 判断登录的是管理员还是用户
	 */
	private String loginAdmin="";// 判断登录的是管理员还是用户
	private UserDao userDao;// 用户登录的Dao
	private WiFiDao wifiDao;// wifi的ssid Dao
	private List<User> users;
	private User user;
	private int user_id;//判断用户是否存在的一个id
	private UserApplication userApplication;//保存用户的全局变量
	private RevHandler handler;

//	private ImageView iv_connect_tip;
	private byte[] buffer;
	private final int ORDER_BUFFER_LENTH = 100;
	private Protocol_400_1 protocol = null;
	private int orderLength = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setImmersionStatus();
		setContentView(R.layout.activity_login);
		protocol = new Protocol_400_1();
		Log.d(TAG, "loginActivity--->onCreate()");
		initView();
		sp_admin.setAdapter(spinnerAdapter);
		sp_admin.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
		initUserData();
		userApplication = (UserApplication) getApplication();
	}

	private void setImmersionStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	/**
	 * 加载组件
	 */
	private void initView() {
		sp_admin = (Spinner) findViewById(R.id.sp_admin);
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		iv_showPassword = (ImageView) findViewById(R.id.iv_showPassword);
		rl_login = (RelativeLayout) findViewById(R.id.rl_login);

		spinnerAdapter = new LoginSpinnerAdapter(this);
		String[] admins = getResources().getStringArray(R.array.adminMethods);
		spinnerAdapter.setAdmins(admins);
		iv_showPassword.setOnClickListener(this);
		rl_login.setOnClickListener(this);
		et_username.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(55));
		et_password.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(55));
		et_username.setText("admin");
		et_password.setText("admin");
		
//		View view = View.inflate(this, R.layout.activity_task_list, null);
//		iv_connect_tip = (ImageView) view.findViewById(R.id.iv_connect_tip);
	}

	/**
	 * 初始化UserDao,并往数据库中添加一些数据
	 */
	private void initUserData() {
		// 初始化
		userDao = new UserDao(this);
		wifiDao=new WiFiDao(this);
		users = userDao.findAllUserLists();
		if (users.isEmpty() || users.size() == 0) {
			// 数据为空,需要插入初始数据
			initUser();
		}
	}

	/**
	 * 插入初始数据,并保存到数据库中
	 */
	private void initUser() {
		// 插入一条默认管理员的信息
		user = new User("admin", "admin", getResources().getString(R.string.login_admin));
		users.add(user);
		// 插入一条默认的技术支持的信息
		user = new User("mingseal", "mingseal", getResources().getString(R.string.login_technician));
		users.add(user);
		// 插入到数据库
		userDao.insertUsers(users);

	}

	/**
	 * 自定义的OnItemSelectedListener,实现选中的是管理员还是游客的框框
	 *
	 */
	private class SpinnerXMLSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// 将选中的管理员还是游客传给一个字段loginAdmin
			loginAdmin = spinnerAdapter.getItem(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	/**
	 * 判断输入框用户名和密码是否为空,true表示都不为空,false表示有空数据
	 */
	private boolean checkUserNameValidity() {
		if("".equals(et_username.getText().toString())){
			ToastUtil.displayPromptInfo(this, "用户名不能为空");
			et_username.requestFocus();
			return false;
		}else if("".equals(et_password.getText().toString())){
			ToastUtil.displayPromptInfo(this, "密码不能为空");
			et_password.requestFocus();
			return false;
		}else{
			user = new User();
			user.setUsername(et_username.getText().toString());
			user.setPassword(et_password.getText().toString());
			user.setType(loginAdmin);
			return true;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_showPassword:
			// 点击隐藏显示密码框
			if (isHidden) {
				// 隐藏
				et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				iv_showPassword.setImageResource(R.drawable.show);
			} else {
				// 显示
				et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
				iv_showPassword.setImageResource(R.drawable.hide);
			}
			isHidden = !isHidden;
			et_password.postInvalidate();
			// 切换后将EditText光标置于末尾
			CharSequence charSequence = et_password.getText();
			if (charSequence instanceof Spannable) {
				Spannable spanText = (Spannable) charSequence;
				Selection.setSelection(spanText, charSequence.length());
			}
			break;
		case R.id.rl_login:
			//获取机器参数
			
			if(checkUserNameValidity()){
				user_id = userDao.checkUserExist(user);
				if(user_id!=-1){
					//将User保存到全局变量里去
					user.setId(user_id);
					userApplication.setUser(user);
					//登录成功
					et_username.setText("");
					et_password.setText("");
					Intent intent= null ;
					if(loginAdmin.equals(getResources().getString(R.string.login_admin))){
						//跳转到管理员界面
						intent = new Intent(this, TaskListActivity.class);
					}else if(loginAdmin.equals(getResources().getString(R.string.login_user))){
						//跳转到操作员界面
						intent = new Intent(this, TaskListUserActivity.class);
					}
					String ssid_info=getSSIDInfo();
					String ssid=null;
					int _id=-1;
					//判断是否有连入网络，进行相应的字符串截取
					if (ssid_info.contains("\"")){
						//联入了网络
						ssid=ssid_info.substring(1,ssid_info.lastIndexOf("\""));
						System.out.println("ssid:======"+ssid);
						//查询数据库
						_id=wifiDao.findNumbySSID(ssid);
					}
					String unknownSSID="<"+"unknown ssid"+">";
					//如果连接的是指定的wifi或没有连接也就是离线模式编辑wifi则直接进入
					//ssid低版本(4.0)上读取的信息是没有引号的，高版本(4.4)上读取到SSID是包含有引号的
					if (_id>0||ssid_info.equals(unknownSSID)){
						handler = new RevHandler();
			            // 线程管理单例初始化
			            SocketThreadManager.sharedInstance().setInputThreadHandler(handler);
			            NetManager.instance().init(this);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
						//登录成功要获取机器参数
						MessageMgr.INSTANCE.getMachineParam();
						finish();
					}else {
						//不进入，提示用户连接指定wifi
						ToastUtil.displayPromptInfo(LoginActivity.this,"请连接wifi模块！");
					}
				}else{
					ToastUtil.displayPromptInfo(this, "用户名密码错误");
				}
			}
			break;
		}
	}
	private String getSSIDInfo()
	{
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String maxText = info.getMacAddress();
		String ipText = intToIp(info.getIpAddress());
		String ssid = info.getSSID();
		return  ssid;
	}
	private String intToIp(int ip)
	{
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
				+ ((ip >> 24) & 0xFF);
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
			ToastUtil.displayPromptInfo(LoginActivity.this, "校验失败");
			sendResetCommand();
			break;
		case 1: {
			int cmdFlag = ((revBuffer[2] & 0x00ff) << 8) | (revBuffer[3] & 0x00ff);
			if (revBuffer[2] == 0x4A) {// 获取下位机参数成功
				Log.d(TAG, RobotParam.INSTANCE.GetXJourney() + ",分辨率：x" + RobotParam.INSTANCE.GetXDifferentiate()+",y:"+ RobotParam.INSTANCE.GetYDifferentiate()+",z:"+ RobotParam.INSTANCE.GetZDifferentiate());
				ToastUtil.displayPromptInfo(LoginActivity.this, "获取参数成功!");
			}
		}
			break;
		case 40101:
			ToastUtil.displayPromptInfo(LoginActivity.this, "非法功能");
			sendResetCommand();
			break;
		case 40102:
			ToastUtil.displayPromptInfo(LoginActivity.this, "非法数据地址");
			sendResetCommand();
			break;
		case 40103:
			ToastUtil.displayPromptInfo(LoginActivity.this, "非法数据");
			sendResetCommand();
			break;
		case 40105:
			ToastUtil.displayPromptInfo(LoginActivity.this, "设备忙");
			sendResetCommand();
			break;
		case 40109:
			ToastUtil.displayPromptInfo(LoginActivity.this, "急停中");
			break;
		case 40110:
			ToastUtil.displayPromptInfo(LoginActivity.this, "X轴光电报警");
			sendResetCommand();
			break;
		case 40111:
			ToastUtil.displayPromptInfo(LoginActivity.this, "Y轴光电报警");
			sendResetCommand();
			break;
		case 40112:
			ToastUtil.displayPromptInfo(LoginActivity.this, "Z轴光电报警");
			sendResetCommand();
			break;
		case 40113:
			ToastUtil.displayPromptInfo(LoginActivity.this, "U轴光电报警");
			sendResetCommand();
			break;
		case 40114:
			ToastUtil.displayPromptInfo(LoginActivity.this, "行程超限报警");
			sendResetCommand();
			break;
		case 40115:
			ToastUtil.displayPromptInfo(LoginActivity.this, "任务下载失败");
			sendResetCommand();
			break;
		case 40116:
			ToastUtil.displayPromptInfo(LoginActivity.this, "任务上传失败");
			sendResetCommand();
			break;
		case 40117:
			ToastUtil.displayPromptInfo(LoginActivity.this, "任务模拟失败");
			sendResetCommand();
			break;
		case 40118:
			ToastUtil.displayPromptInfo(LoginActivity.this, "示教指令错误");
			sendResetCommand();
			break;
		case 40119:
			ToastUtil.displayPromptInfo(LoginActivity.this, "循迹定位失败");
			sendResetCommand();
			break;
		case 40120:
			ToastUtil.displayPromptInfo(LoginActivity.this, "任务号不可用");
			sendResetCommand();
			break;
		case 40121:
			ToastUtil.displayPromptInfo(LoginActivity.this, "初始化失败");
			sendResetCommand();
			break;
		case 40122:
			ToastUtil.displayPromptInfo(LoginActivity.this, "API版本错误");
			sendResetCommand();
			break;
		case 40123:
			ToastUtil.displayPromptInfo(LoginActivity.this, "程序升级失败");
			sendResetCommand();
			break;
		case 40124:
			ToastUtil.displayPromptInfo(LoginActivity.this, "系统损坏");
			sendResetCommand();
			break;
		case 40125:
			ToastUtil.displayPromptInfo(LoginActivity.this, "任务未加载");
			sendResetCommand();
			break;
		case 40126:
			ToastUtil.displayPromptInfo(LoginActivity.this, "(Z轴)基点抬起高度过高");
			sendResetCommand();
			break;
		case 40127:
			ToastUtil.displayPromptInfo(LoginActivity.this, "等待输入超时");
			sendResetCommand();
			break;
		default:
			ToastUtil.displayPromptInfo(LoginActivity.this, "未知错误");
			sendResetCommand();
			break;
		}
	}

	private void sendResetCommand() {
			/************************ add begin ************************/
			buffer = new byte[ORDER_BUFFER_LENTH];
			orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Reset);
			MessageMgr.INSTANCE.writeData(buffer, orderLength);
			/************************ end ******************************/
//			mPointsCur.get(selectRadioIDCur).setX(0);
//			mPointsCur.get(selectRadioIDCur).setY(0);
//			mPointsCur.get(selectRadioIDCur).setZ(0);
//			mPointsCur.get(selectRadioIDCur).setU(0);
//			mAdapter.setData(mPointsCur);
//			mAdapter.notifyDataSetChanged();
	}

	private class RevHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// 如果消息来自子线程
			if (msg.what == SocketInputThread.SocketInputWhat) {
				userApplication.setWifiConnecting(true);
//				WifiConnectTools.processWifiConnect(userApplication, iv_connect_tip);
				// 获取下位机上传的数据
				ByteBuffer temp = (ByteBuffer) msg.obj;
				byte[] buffer;
				buffer = temp.array();
				// byte[] revBuffer = (byte[]) msg.obj;
				DisPlayInfoAfterGetMsg(buffer);
			}
		}
	}

	

}
