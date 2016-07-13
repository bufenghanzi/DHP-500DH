package com.mingseal.data.point.weldparam;

import android.os.Parcel;
import android.os.Parcelable;

import com.mingseal.data.point.IOPort;
import com.mingseal.data.point.PointParam;
import com.mingseal.data.point.PointType;

import java.util.Arrays;

/**
 * 焊锡吹锡点参数类
 * @author lyq
 */
public class PointWeldBlowParam extends PointParam {
	
	private int goTimePrev; // 动作前延时(单位:毫秒ms)
	private int goTimeNext; // 动作后延时(单位:毫秒ms)
	private boolean[] outputPort; // 5号输出口

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

	/**
	 * @return 获取动作后延时
	 */
	public int getGoTimeNext() {
		return goTimeNext;
	}

	/**
	 * 设置动作后延时
	 *
	 * @param goTimeNext
	 *            动作后延时
	 */
	public void setGoTimeNext(int goTimeNext) {
		this.goTimeNext = goTimeNext;
	}


	/**
	 * @return 获取输出口数据
	 */
	public boolean[] getOutputPort() {
		return outputPort;
	}

	/**
	 * 5号输出口
	 * @param outputPort
	 */
	public void setOutputPort(boolean[] outputPort) {
		this.outputPort = outputPort;
	}

	/**

	 * 焊锡吹锡点参数构造方法,默认值:
	 * @blowSnTime 吹锡时间 0
	 */
	public PointWeldBlowParam(){
		PointWeldBlowParamInit(0,0);
		super.setPointType(PointType.POINT_WELD_BLOW);
		this.outputPort=new boolean[IOPort.IO_NO_ALL.ordinal()];
	}

	private void PointWeldBlowParamInit(int goTimePrev, int goTimeNext) {
		this.goTimeNext=goTimeNext;
		this.goTimePrev=goTimePrev;
	}

	public PointWeldBlowParam(int goTimePrev, int goTimeNext, boolean[] outputPort) {
		PointWeldBlowParamInit(goTimePrev,goTimeNext);
		super.setPointType(PointType.POINT_WELD_BLOW);
		this.outputPort = outputPort;
	}

	@Override
	public String toString() {
		return "PoinWeldBlowParam{" +
				"goTimePrev=" + goTimePrev +
				", goTimeNext=" + goTimeNext +
				", outputPort=" + Arrays.toString(outputPort) +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PointWeldBlowParam that = (PointWeldBlowParam) o;

		if (goTimePrev != that.goTimePrev) return false;
		if (goTimeNext != that.goTimeNext) return false;
		return Arrays.equals(outputPort, that.outputPort);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + goTimePrev;
		result = 31 * result + goTimeNext;
		result = 31 * result + Arrays.hashCode(outputPort);
		return result;
	}

	public static final Parcelable.Creator<PointWeldBlowParam> CREATOR=new Creator<PointWeldBlowParam>() {
		@Override
		public PointWeldBlowParam createFromParcel(Parcel source) {
			PointWeldBlowParam point=new PointWeldBlowParam();
			point.goTimePrev = source.readInt();
			point.goTimeNext = source.readInt();
			boolean[] val = null;
			val = new boolean[IOPort.IO_NO_ALL.ordinal()];
			source.readBooleanArray(val);
			point.outputPort = val;
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
		dest.writeInt(goTimePrev);
		dest.writeInt(goTimeNext);
		dest.writeBooleanArray(outputPort);
		dest.writeInt(get_id());
	}
}
