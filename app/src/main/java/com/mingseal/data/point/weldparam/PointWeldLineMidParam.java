package com.mingseal.data.point.weldparam;

import android.os.Parcel;
import android.os.Parcelable;

import com.mingseal.data.point.PointParam;
import com.mingseal.data.point.PointType;

/**
 * 焊锡线中间点参数类
 * @author lyq-wj
 */
public class PointWeldLineMidParam extends PointParam {
	
	private int moveSpeed; //轨迹速度(单位：毫米/秒mm/s)
	private int sendSnSpeed; //送锡速度(单位：毫米/秒mm/s)
	private int stopSnTime; //停锡延时(单位：毫秒ms)
	private boolean isSn; //是否出锡
	/**
	 * @return 获取是否出锡
	 */
	public boolean isSn() {
		return isSn;
	}

	/**
	 * 设置是否出锡
	 * @param isSn 是否出锡
	 */
	public void setSn(boolean isSn) {
		this.isSn = isSn;
	}
	/**
	 * @return 获取轨迹速度(单位：毫米/秒mm/s)
	 */
	public int getMoveSpeed() {
		return moveSpeed;
	}
	
	/**
	 * 设置轨迹速度(单位：毫米/秒mm/s)
	 * @param moveSpeed 轨迹速度
	 */
	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
	
	/**
	 * @return 获取送锡速度(单位：毫米/秒mm/s)
	 */
	public int getSendSnSpeed() {
		return sendSnSpeed;
	}
	
	/**
	 * 设置送锡速度(单位：毫米/秒mm/s)
	 * @param sendSnSpeed 送锡速度
	 */
	public void setSendSnSpeed(int sendSnSpeed) {
		this.sendSnSpeed = sendSnSpeed;
	}
	
	/**
	 * @return 获取停锡延时(单位：毫秒ms)
	 */
	public int getStopSnTime() {
		return stopSnTime;
	}
	
	/**
	 * 设置停锡延时(单位：毫秒ms)
	 * @param stopSnTime 停锡延时
	 */
	public void setStopSnTime(int stopSnTime) {
		this.stopSnTime = stopSnTime;
	}
	
//	/**
//	 * @return 获取预热时间(单位：毫秒ms)
//	 */
//	public int getPreHeatTime() {
//		return preHeatTime;
//	}
//
//	/**
//	 * 设置预热时间(单位：毫秒ms)
//	 * @param preHeatTime 预热时间
//	 */
//	public void setPreHeatTime(int preHeatTime) {
//		this.preHeatTime = preHeatTime;
//	}
	
	/**
	 * 焊锡线中间点参数私有初始化方法
	 * @param moveSpeed 轨迹速度(单位：毫米/秒mm/s)
	 * @param sendSnSpeed 送锡速度(单位：毫米/秒mm/s)
	 * @param stopSnTime 停锡延时(单位：毫秒ms)
	 * @param isSn 是否出锡
	 */
	private void pointWeldLineMidParamInit(int moveSpeed, int sendSnSpeed,
			int stopSnTime, boolean isSn){
		this.moveSpeed = moveSpeed;
		this.sendSnSpeed = sendSnSpeed;
		this.stopSnTime = stopSnTime;
//		this.preHeatTime = preHeatTime;
		this.isSn=isSn;
	}
	
	/**
	 * 焊锡线中间点参数构造方法,默认值为:
	 *  @moveSpeed 轨迹速度 0 (单位：毫米/秒mm/s)
	 *  @sendSnSpeed 送锡速度 10 (单位：毫米/秒mm/s)
	 *  @stopSnTime 停锡延时 0 (单位：毫秒ms)
	 *  @preHeatTime 预热时间 0 (单位：毫秒ms)
	 */
	public PointWeldLineMidParam(){
		pointWeldLineMidParamInit(5, 0, 0, true);
		super.setPointType(PointType.POINT_WELD_LINE_MID);
	}
	
	/**
	 * 焊锡线中间点参数含参构造方法
	 * @param moveSpeed 轨迹速度(单位：毫米/秒mm/s)
	 * @param sendSnSpeed 送锡速度(单位：毫米/秒mm/s)
	 * @param stopSnTime 停锡延时(单位：毫秒ms)
	 * @param isSn 是否出锡
	 */
	public PointWeldLineMidParam(int moveSpeed, int sendSnSpeed,
			int stopSnTime, boolean isSn){
		pointWeldLineMidParamInit(moveSpeed, sendSnSpeed, stopSnTime, isSn);
		super.setPointType(PointType.POINT_WELD_LINE_MID);
	}

	@Override
	public String toString() {
		return "PointWeldLineMidParam{" +
				"moveSpeed=" + moveSpeed +
				", sendSnSpeed=" + sendSnSpeed +
				", stopSnTime=" + stopSnTime +
				", isSn=" + isSn +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PointWeldLineMidParam that = (PointWeldLineMidParam) o;

		if (moveSpeed != that.moveSpeed) return false;
		if (sendSnSpeed != that.sendSnSpeed) return false;
		if (stopSnTime != that.stopSnTime) return false;
		return isSn == that.isSn;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + moveSpeed;
		result = 31 * result + sendSnSpeed;
		result = 31 * result + stopSnTime;
		result = 31 * result + (isSn ? 1 : 0);
		return result;
	}
	public static final Parcelable.Creator<PointWeldLineMidParam> CREATOR=new Creator<PointWeldLineMidParam>() {
		@Override
		public PointWeldLineMidParam createFromParcel(Parcel source) {
			PointWeldLineMidParam point=new PointWeldLineMidParam();
			point.moveSpeed=source.readInt();
			point.sendSnSpeed=source.readInt();
			point.stopSnTime=source.readInt();
			point.isSn = source.readInt() != 0;
			point.set_id(source.readInt());
			return point;
		}

		@Override
		public PointWeldLineMidParam[] newArray(int size) {
			return new PointWeldLineMidParam[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(moveSpeed);
		dest.writeInt(sendSnSpeed);
		dest.writeInt(stopSnTime);
		dest.writeInt(isSn?1:0);
		dest.writeInt(get_id());
	}
}
