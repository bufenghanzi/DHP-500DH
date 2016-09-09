package com.mingseal.data.point.weldparam;

import android.os.Parcel;
import android.os.Parcelable;

import com.mingseal.data.point.PointParam;
import com.mingseal.data.point.PointType;

/**
 * 焊锡吹锡点参数类
 * @author wj
 */
public class PointWeldBlowParam extends PointParam {
	
	private int goTimePrev; // 动作前延时(单位:毫秒ms)
	private boolean isSn;//是否出锡

	/**
	 * @return 获取动作前延时
	 */
	public int getGoTimePrev() {
		return goTimePrev;
	}

	/**
	 * 设置动作前延时
	 *
	 * @param goTimePrev
	 *            动作前延时
	 */
	public void setGoTimePrev(int goTimePrev) {
		this.goTimePrev = goTimePrev;
	}

//	/**
//	 * @return 获取动作后延时
//	 */
//	public int getGoTimeNext() {
//		return goTimeNext;
//	}
//
//	/**
//	 * 设置动作后延时
//	 *
//	 * @param goTimeNext
//	 *            动作后延时
//	 */
//	public void setGoTimeNext(int goTimeNext) {
//		this.goTimeNext = goTimeNext;
//	}


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

	 * 焊锡吹锡点参数构造方法,默认值:
	 * @blowSnTime 吹锡时间 0
	 */
	public PointWeldBlowParam(){
		PointWeldBlowParamInit(true,0);
		super.setPointType(PointType.POINT_WELD_BLOW);
	}

	private void PointWeldBlowParamInit(boolean isSn,int goTimePrev) {
		this.isSn = isSn;
		this.goTimePrev=goTimePrev;
	}

	@Override
	public String toString() {
		return "PointWeldBlowParam{" +
				"goTimePrev=" + goTimePrev +
				", isSn=" + isSn +
				'}';
	}

	public String getString(){
		return  "PointWeldBlowParam{" +
				"goTimePrev=" + goTimePrev +
				", isSn=" + isSn +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PointWeldBlowParam blowParam = (PointWeldBlowParam) o;

		if (goTimePrev != blowParam.goTimePrev) return false;
		return isSn == blowParam.isSn;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + goTimePrev;
		result = 31 * result + (isSn ? 1 : 0);
		return result;
	}

	public static final Parcelable.Creator<PointWeldBlowParam> CREATOR=new Creator<PointWeldBlowParam>() {
		@Override
		public PointWeldBlowParam createFromParcel(Parcel source) {
			PointWeldBlowParam point=new PointWeldBlowParam();
			point.isSn = source.readInt() != 0;
			point.goTimePrev = source.readInt();
			point.set_id(source.readInt());
			return point;
		}

		@Override
		public PointWeldBlowParam[] newArray(int size) {
			return new PointWeldBlowParam[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(isSn?1:0);
		dest.writeInt(goTimePrev);
		dest.writeInt(get_id());
	}
}
