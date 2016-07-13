/**
 * 
 */
package com.mingseal.application;

import android.app.Application;
import android.os.Handler;

import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointTask;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldOutputIOParam;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;
import com.mingseal.data.user.User;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.HashMap;
import java.util.List;

/**
 * @author wangjian
 * @description 保存全局变量
 */
public class UserApplication extends Application {
	private User user;
	private List<Point> points;// Point的List集合
	// private List<Integer> pointIDs;// 任务中任务点的主键集合
	private PointTask pointTask;// 任务
	private HashMap<Integer, PointWeldWorkParam> aloneParamMaps;// 独立点Map集合改为作业点
	private HashMap<Integer, PointWeldLineStartParam> lineStartParamMaps;// 线起始点Map集合改为焊锡起始点
	private HashMap<Integer, PointWeldLineMidParam> lineMidParamMaps;// 线中间点Map集合改为焊锡中间点
	private HashMap<Integer, PointWeldLineEndParam> lineEndParamMaps;// 线结束点Map集合改为焊锡结束点
	private HashMap<Integer, PointWeldBlowParam> blowParamMaps;// 清胶点Map集合改为吹锡点
	private HashMap<Integer, PointWeldInputIOParam> inputParamMaps;// 输入IO点Map集合
	private HashMap<Integer, PointWeldOutputIOParam> outputParamMaps;// 输出IO点Map集合
	private boolean isWifiConnecting = false;// wifi连接情况
	private static Handler mHandler;

	public static Handler getHandler() {
		return mHandler;
	}

	/**
	 * @return 全局User对象
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 设置全局User对象
	 * 
	 * @param user
	 *            用户
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return 全局Point的List集合
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * 设置全局的Point的List集合
	 * 
	 * @param points
	 *            Point的List集合
	 */
	public void setPoints(List<Point> points) {
		this.points = points;
	}

	/**
	 * @return 任务
	 */
	public PointTask getPointTask() {
		return pointTask;
	}

	/**
	 * 设置任务
	 * 
	 * @param pointTask
	 */
	public void setPointTask(PointTask pointTask) {
		this.pointTask = pointTask;
	}

	/**
	 * @return 作业点参数的Map集合
	 */
	public HashMap<Integer, PointWeldWorkParam> getAloneParamMaps() {
		return aloneParamMaps;
	}

	/**
	 * 设置作业点参数的Map集合
	 * 
	 * @param aloneParamMaps
	 *            作业点参数Map
	 */
	public void setAloneParamMaps(
			HashMap<Integer, PointWeldWorkParam> aloneParamMaps) {
		this.aloneParamMaps = aloneParamMaps;
	}

	/**
	 * @return 线结束点参数的Map集合
	 */
	public HashMap<Integer, PointWeldLineEndParam> getLineEndParamMaps() {
		return lineEndParamMaps;
	}

	/**
	 * 设置线结束点参数的Map集合
	 * 
	 * @param lineEndParamMaps
	 *            线结束点参数的Map
	 */
	public void setLineEndParamMaps(
			HashMap<Integer, PointWeldLineEndParam> lineEndParamMaps) {
		this.lineEndParamMaps = lineEndParamMaps;
	}



	/**
	 * @return 线起始点参数的Map集合
	 */
	public HashMap<Integer, PointWeldLineStartParam> getLineStartParamMaps() {
		return lineStartParamMaps;
	}

	/**
	 * 
	 * 设置线起始点参数的Map集合
	 * 
	 * @param lineStartParamMaps
	 *            线起始点参数的Map集合
	 */
	public void setLineStartParamMaps(
			HashMap<Integer, PointWeldLineStartParam> lineStartParamMaps) {
		this.lineStartParamMaps = lineStartParamMaps;
	}

	/**
	 * @return 线中间点参数的Map集合
	 */
	public HashMap<Integer, PointWeldLineMidParam> getLineMidParamMaps() {
		return lineMidParamMaps;
	}

	/**
	 * 设置线中间点参数的Map集合
	 * 
	 * @param lineMidParamMaps
	 *            线中间点参数的Map集合
	 */
	public void setLineMidParamMaps(
			HashMap<Integer, PointWeldLineMidParam> lineMidParamMaps) {
		this.lineMidParamMaps = lineMidParamMaps;
	}



	/**
	 * @return 吹锡点参数的Map集合
	 */
	public HashMap<Integer, PointWeldBlowParam> getBlowParamMaps() {
		return blowParamMaps;
	}

	/**
	 * 设置吹锡点参数的Map集合
	 * 
	 * @param blowParamMaps
	 *            清胶点参数的Map集合
	 */
	public void setClearParamMaps(
			HashMap<Integer, PointWeldBlowParam> blowParamMaps) {
		this.blowParamMaps = blowParamMaps;
	}

	/**
	 * @return 输入IO点参数的Map集合
	 */
	public HashMap<Integer, PointWeldInputIOParam> getInputParamMaps() {
		return inputParamMaps;
	}

	/**
	 * 设置输入IO点参数的Map集合
	 * 
	 * @param inputParamMaps
	 *            输入IO点参数的Map集合
	 */
	public void setInputParamMaps(
			HashMap<Integer, PointWeldInputIOParam> inputParamMaps) {
		this.inputParamMaps = inputParamMaps;
	}

	/**
	 * @return 输出IO点参数的Map集合
	 */
	public HashMap<Integer, PointWeldOutputIOParam> getOutputParamMaps() {
		return outputParamMaps;
	}

	/**
	 * 设置输出IO点参数的Map集合
	 * 
	 * @param outputParamMaps
	 *            输出IO点参数的Map集合
	 */
	public void setOutputParamMaps(
			HashMap<Integer, PointWeldOutputIOParam> outputParamMaps) {
		this.outputParamMaps = outputParamMaps;
	}

	/**
	 * ParamsSetting中设置独立点，起始点，中间点，结束点，面起点，面终点，清胶点，输入IO，输出IO的参数方案
	 * 
	 * @param aloneParamMaps
	 *            独立点参数方案
	 * @param lineStartParamMaps
	 *            线起始点参数方案
	 * @param lineMidParamMaps
	 *            线中间点参数方案
	 * @param lineEndParamMaps
	 *            线结束点参数方案
	 * @param blowParamMaps
	 *            清胶点参数方案
	 * @param inputParamMaps
	 *            输入IO点参数方案
	 * @param outputParamMaps
	 *            输出IO点参数方案
	 */
	public void setParamMaps(
			HashMap<Integer, PointWeldWorkParam> aloneParamMaps,
			HashMap<Integer, PointWeldLineStartParam> lineStartParamMaps,
			HashMap<Integer, PointWeldLineMidParam> lineMidParamMaps,
			HashMap<Integer, PointWeldLineEndParam> lineEndParamMaps,
			HashMap<Integer, PointWeldBlowParam> blowParamMaps,
			HashMap<Integer, PointWeldInputIOParam> inputParamMaps,
			HashMap<Integer, PointWeldOutputIOParam> outputParamMaps) {
		this.aloneParamMaps = aloneParamMaps;
		this.lineStartParamMaps = lineStartParamMaps;
		this.lineMidParamMaps = lineMidParamMaps;
		this.lineEndParamMaps = lineEndParamMaps;
		this.blowParamMaps = blowParamMaps;
		this.inputParamMaps = inputParamMaps;
		this.outputParamMaps = outputParamMaps;
	}

	/**
	 * @Title isWifiConnecting
	 * @Description 获取wifi连接情况
	 * @return true为连接成功(获取参数成功),false为连接失败(没有获取到机器参数)
	 */
	public boolean isWifiConnecting() {
		return isWifiConnecting;
	}

	/**
	 * @Title setWifiConnecting
	 * @Description 设置wifi连接情况
	 * @param isWifiConnecting
	 *            true为连接成功(获取参数成功),false为连接失败(没有获取到机器参数)
	 */
	public void setWifiConnecting(boolean isWifiConnecting) {
		this.isWifiConnecting = isWifiConnecting;
	}

	@Override
	public void onCreate() {
		// 定义一个handler
		mHandler = new Handler();
		super.onCreate();
//		LeakCanary.install(this);
		AutoLayoutConifg.getInstance().useDeviceSize().init(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
