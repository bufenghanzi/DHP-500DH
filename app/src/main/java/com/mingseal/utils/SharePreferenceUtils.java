/**
 * 
 */
package com.mingseal.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mingseal.data.param.SettingParam;

/**
 * @author 商炎炳
 *
 */
public class SharePreferenceUtils {

	/**
	 * 这个要放在第一个Activity中 ,设置SharePreference的初始数据
	 * 
	 * @param context
	 */
	public static void setSharedPreference(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Setting.SettingName, Context.MODE_PRIVATE);
		// 因为是第一次，所以需要给它设个初值并保存(如果里面有值的话，还是保存原来的值)
		int xStepDistance = sp.getInt(SettingParam.Setting.XStepDistance, 1);
		int yStepDistance = sp.getInt(SettingParam.Setting.YStepDistance, 1);
		int zStepDistance = sp.getInt(SettingParam.Setting.ZStepDistance, 1);
		int lowSpeed = sp.getInt(SettingParam.Setting.LowSpeed, 3);
		int mediumSpeed = sp.getInt(SettingParam.Setting.MediumSpeed, 13);
		int highSpeed = sp.getInt(SettingParam.Setting.HighSpeed, 33);
		int trackSpeed = sp.getInt(SettingParam.Setting.TrackSpeed, 50);
		boolean trackLocation = sp.getBoolean(SettingParam.Setting.TrackLocation, false);

		SharedPreferences.Editor editor = sp.edit();

		editor.putInt(SettingParam.Setting.XStepDistance, xStepDistance);
		editor.putInt(SettingParam.Setting.YStepDistance, yStepDistance);
		editor.putInt(SettingParam.Setting.ZStepDistance, zStepDistance);
		editor.putInt(SettingParam.Setting.LowSpeed, lowSpeed);
		editor.putInt(SettingParam.Setting.MediumSpeed, mediumSpeed);
		editor.putInt(SettingParam.Setting.HighSpeed, highSpeed);
		editor.putInt(SettingParam.Setting.TrackSpeed, trackSpeed);
		editor.putBoolean(SettingParam.Setting.TrackLocation, trackLocation);
		editor.apply();

	}
	

	/**
	 * 从SharePreference中读取数据，并保存到SettingParam中
	 * 
	 * @param context
	 * @return 任务设置参数
	 */
	public static SettingParam readFromSharedPreference(Context context) {
		SettingParam settingParam = new SettingParam();
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Setting.SettingName, Context.MODE_PRIVATE);
		int xStepDistance = sp.getInt(SettingParam.Setting.XStepDistance, 1);
		int yStepDistance = sp.getInt(SettingParam.Setting.YStepDistance, 1);
		int zStepDistance = sp.getInt(SettingParam.Setting.ZStepDistance, 1);
		int lowSpeed = sp.getInt(SettingParam.Setting.LowSpeed, 3);
		int mediumSpeed = sp.getInt(SettingParam.Setting.MediumSpeed, 13);
		int highSpeed = sp.getInt(SettingParam.Setting.HighSpeed, 33);
		int trackSpeed = sp.getInt(SettingParam.Setting.TrackSpeed, 50);
		boolean trackLocation = sp.getBoolean(SettingParam.Setting.TrackLocation, false);

		settingParam.setxStepDistance(xStepDistance);
		settingParam.setyStepDistance(yStepDistance);
		settingParam.setzStepDistance(zStepDistance);
		settingParam.setHighSpeed(highSpeed);
		settingParam.setMediumSpeed(mediumSpeed);
		settingParam.setLowSpeed(lowSpeed);
		settingParam.setTrackSpeed(trackSpeed);
		settingParam.setTrackLocation(trackLocation);

		return settingParam;

	}

	/**
	 * 将SettingParam的数据保存到SharedPreferences中
	 * 
	 * @param context
	 *            上下文
	 * @param setting
	 *            SettingParam
	 */
	public static void saveToSharedPreferences(Context context, SettingParam setting) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Setting.SettingName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		editor.putInt(SettingParam.Setting.XStepDistance, setting.getxStepDistance());
		editor.putInt(SettingParam.Setting.YStepDistance, setting.getyStepDistance());
		editor.putInt(SettingParam.Setting.ZStepDistance, setting.getzStepDistance());
		editor.putInt(SettingParam.Setting.LowSpeed, setting.getLowSpeed());
		editor.putInt(SettingParam.Setting.MediumSpeed, setting.getMediumSpeed());
		editor.putInt(SettingParam.Setting.HighSpeed, setting.getHighSpeed());
		editor.putInt(SettingParam.Setting.TrackSpeed, setting.getTrackSpeed());
		editor.putBoolean(SettingParam.Setting.TrackLocation, setting.isTrackLocation());

		editor.apply();
	}

	/**
	 * 将任务号保存到SharePreferences
	 * 
	 * @Title saveTaskNumberToPref
	 * @Description 将任务号保存到SharePreferences
	 * @param context
	 * @param number 任务号
	 * @param nSnHeight 出锡高度
	 * @param nBackSnSpeedFir 一次回锡速度
	 * @param nBackSnSpeedSec 二次回锡速度
	 * @param nBackSnSpeedThird 三次回锡速度
	 * @param nBackSnSpeedFour 四次回锡速度
	 * @param nBackSnSumFir 一次送锡量
	 * @param nBackSnSumSec 二次送锡量
	 * @param nBackSnSumThird 三次送锡量
	 * @param nBackSnSumSecFour 四次送锡量
	 * @param nXYNullSpeed
	 * @param nZNullSpeed
	 *            任务号
	 */
	public static void saveTaskNumberAndDatesToPref(Context context, int number, int nSnHeight, int nBackSnSpeedFir, int nBackSnSpeedSec, int nBackSnSpeedThird
			, int nBackSnSpeedFour, int nBackSnSumFir, int nBackSnSumSec, int nBackSnSumThird, int nBackSnSumSecFour
			, int nXYNullSpeed, int nZNullSpeed,int nAccelerate,int nDecelerate) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(SettingParam.Task.TaskNumber, number);
		editor.putInt(SettingParam.Setting.nSnHeight, nSnHeight);
		editor.putInt(SettingParam.Setting.nBackSnSpeedFir, nBackSnSpeedFir);
		editor.putInt(SettingParam.Setting.nBackSnSpeedSec, nBackSnSpeedSec);
		editor.putInt(SettingParam.Setting.nBackSnSpeedThird, nBackSnSpeedThird);
		editor.putInt(SettingParam.Setting.nBackSnSpeedFour, nBackSnSpeedFour);
		editor.putInt(SettingParam.Setting.nBackSnSumFir, nBackSnSumFir);
		editor.putInt(SettingParam.Setting.nBackSnSumSec, nBackSnSumSec);
		editor.putInt(SettingParam.Setting.nBackSnSumThird, nBackSnSumThird);
		editor.putInt(SettingParam.Setting.nBackSnSumSecFour, nBackSnSumSecFour);
		editor.putInt(SettingParam.Setting.nXYNullSpeed, nXYNullSpeed);
		editor.putInt(SettingParam.Setting.nZNullSpeed, nZNullSpeed);
		editor.putInt(SettingParam.Setting.nAccelerate, nAccelerate);
		editor.putInt(SettingParam.Setting.nDecelerate, nDecelerate);
		editor.apply();
	}

	/**
	 * 从SharePreferences中获取任务号
	 * 
	 * @Title getTaskNumberFromPref
	 * @Description 从SharePreferences中获取任务号
	 * @param context
	 * @return 任务号
	 */
	public static int getTaskNumberFromPref(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Task.TaskNumber, 1);
		return number;
	}
	public static int getnSnHeight (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nSnHeight, 8);
		return number;
	}
	public static int getnAccelerate (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nAccelerate, 3000);
		return number;
	}
	public static int getnDecelerate (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nDecelerate, 3000);
		return number;
	}
	public static int getnBackSnSpeedFir(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSpeedFir, 50);
		return number;
	}
	public static int getnBackSnSpeedSec (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSpeedSec,50);
		return number;
	}
	public static int getnBackSnSpeedThird (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSpeedThird, 50);
		return number;
	}
	public static int getnBackSnSpeedFour (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSpeedFour, 50);
		return number;
	}
	public static int getnBackSnSumFir(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSumFir, 0);
		return number;
	}
	public static int getnBackSnSumSec (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSumSec, 0);
		return number;
	}
	public static int getnBackSnSumThird (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSumThird, 0);
		return number;
	}
	public static int getnBackSnSumSecFour (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nBackSnSumSecFour, 0);
		return number;
	}
	public static int getnXYNullSpeed(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nXYNullSpeed, 200);
		return number;
	}
	public static int getnZNullSpeed (Context context) {
		SharedPreferences sp = context.getSharedPreferences(SettingParam.Task.TaskName, Context.MODE_PRIVATE);
		int number = sp.getInt(SettingParam.Setting.nZNullSpeed, 200);
		return number;
	}
	/**
	 * 保存相对应的点参数的默认号
	 * 
	 * @Title saveParamNumberToPref
	 * @Description 保存某个方案的参数序列号
	 * @param context
	 * @param key
	 *            方案名
	 * @param number
	 *            默认方案号
	 */
	public static void saveParamNumberToPref(Context context, String key, int number) {

		SharedPreferences sp = context.getSharedPreferences(SettingParam.DefaultNum.PointDefaultNum, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, number);
		editor.apply();

	}

	/**
	 * 从SharePreferences中读取传入的方案的默认方案号
	 * 
	 * @Title getParamNumberFromPref
	 * @Description 从SharePreferences中读取上次保存的默认方案号
	 * @param context
	 * @param key
	 *            方案名
	 * @return 默认方案号
	 */
	public static int getParamNumberFromPref(Context context, String key) {

		SharedPreferences sp = context.getSharedPreferences(SettingParam.DefaultNum.PointDefaultNum, Context.MODE_PRIVATE);
		int number = sp.getInt(key, 1);
		return number;

	}

}
