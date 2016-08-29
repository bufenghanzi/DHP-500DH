package com.mingseal.data.point.weldparam;

import android.os.Parcel;

import com.mingseal.data.point.PointParam;
import com.mingseal.data.point.PointType;

/**
 * 焊锡作业点
 *
 * @author lyq
 */
public class PointWeldWorkParam extends PointParam {

    private int preHeatTime;                    //预热时间(单位:毫秒ms)
    private int sendSnSpeedFir;            //一次送锡速度(单位:毫米/秒 mm/s)
    private int sendSnSumFir;                //一次送锡量(单位:丝米dmm)
    private int sendSnSpeedSec;            //二次送锡速度(单位:毫米/秒 mm/s)
    private int sendSnSumSec;            //二次送锡量(单位:丝米dmm)
    private int stopSnStimeSec;            //二次停锡时间
    private int sendSnSpeedThird;        //三次送锡速度(单位:毫米/秒 mm/s)
    private int sendSnSumThird;            //三次送锡量(单位:丝米dmm)
    private int stopSnTimeThird;            //三次停锡时间
    private int sendSnSpeedFourth;    //四次送锡速度(单位:毫米/秒 mm/s)
    private int sendSnSumFourth;        //四次送锡量(单位:丝米dmm)
    private int stopSnTimeFourth;        //四次停锡时间
    private int dipDistance;                    //倾斜距离(单位:mm)
    private int dipDistance_angle;                    //倾斜角度(单位:度)
    private int upHeight;                        //抬起高度(单位:mm)
    private boolean isSn;                        //是否出锡
//    private boolean isOut;                    //焊点结束退出时是否抬起停顿标志	0: 不抬起停顿	1:抬起停顿
    private boolean isSus;                    //下降焊锡前是否减速标志			0: 不减速		1:减速
	private boolean isPause;				//是否暂停

    @Override
    public int get_id() {
        return super.get_id();
    }

    @Override
    public void set_id(int _id) {
        super.set_id(_id);
    }

    /**
     * @return 获取预热时间(单位:毫秒ms)
     */
    public int getPreHeatTime() {
        return preHeatTime;
    }

    /**
     * 设置预热时间(单位:毫秒ms)
     *
     * @param preHeatTime 预热时间
     */
    public void setPreHeatTime(int preHeatTime) {
        this.preHeatTime = preHeatTime;
    }

    /**
     * @return 获取一次送锡速度(单位:毫米/秒 mm/s)
     */
    public int getSendSnSpeedFir() {
        return sendSnSpeedFir;
    }

    /**
     * 设置一次送锡速度
     *
     * @param sendSnSpeedFir 一次送锡速度
     */
    public void setSendSnSpeedFir(int sendSnSpeedFir) {
        this.sendSnSpeedFir = sendSnSpeedFir;
    }

    /**
     * @return 获取一次送锡量(单位:丝米dmm)
     */
    public int getSendSnSumFir() {
        return sendSnSumFir;
    }

    /**
     * 设置一次送锡量(单位:丝米dmm)
     *
     * @param sendSnSumFir 一次送锡量(单位:丝米dmm)
     */
    public void setSendSnSumFir(int sendSnSumFir) {
        this.sendSnSumFir = sendSnSumFir;
    }

    /**
     * @return 获取二次送锡速度(单位:毫米/秒 mm/s)
     */
    public int getSendSnSpeedSec() {
        return sendSnSpeedSec;
    }

    /**
     * 设置二次送锡速度(单位:毫米/秒 mm/s)
     *
     * @param sendSnSpeedSec 二次送锡速度
     */
    public void setSendSnSpeedSec(int sendSnSpeedSec) {
        this.sendSnSpeedSec = sendSnSpeedSec;
    }

    /**
     * @return 获取二次送锡量(单位:丝米dmm)
     */
    public int getSendSnSumSec() {
        return sendSnSumSec;
    }

    /**
     * 设置二次送锡量(单位:丝米dmm)
     *
     * @param sendSnSumSec 二次送锡量
     */
    public void setSendSnSumSec(int sendSnSumSec) {
        this.sendSnSumSec = sendSnSumSec;
    }

    /**
     * @return 获取二次停锡时间
     */
    public int getStopSnStimeSec() {
        return stopSnStimeSec;
    }

    /**
     * 设置二次停锡时间
     *
     * @param stopSnStimeSec 二次停锡时间
     */
    public void setStopSnStimeSec(int stopSnStimeSec) {
        this.stopSnStimeSec = stopSnStimeSec;
    }

    /**
     * @return 获取三次送锡速度(单位:毫米/秒 mm/s)
     */
    public int getSendSnSpeedThird() {
        return sendSnSpeedThird;
    }

    /**
     * 设置三次送锡速度(单位:毫米/秒 mm/s)
     *
     * @param sendSnSpeedThird 三次送锡速度
     */
    public void setSendSnSpeedThird(int sendSnSpeedThird) {
        this.sendSnSpeedThird = sendSnSpeedThird;
    }

    /**
     * @return 获取三次送锡量(单位:丝米dmm)
     */
    public int getSendSnSumThird() {
        return sendSnSumThird;
    }

    /**
     * 设置三次送锡量(单位:丝米dmm)
     *
     * @param sendSnSumThird 三次送锡量
     */
    public void setSendSnSumThird(int sendSnSumThird) {
        this.sendSnSumThird = sendSnSumThird;
    }

    /**
     * @return 获取三次停锡时间
     */
    public int getStopSnTimeThird() {
        return stopSnTimeThird;
    }

    /**
     * 设置三次停锡时间
     *
     * @param stopSnTimeThird 三次停锡时间
     */
    public void setStopSnTimeThird(int stopSnTimeThird) {
        this.stopSnTimeThird = stopSnTimeThird;
    }

    /**
     * @return 获取四次送锡速度(单位:毫米/秒 mm/s)
     */
    public int getSendSnSpeedFourth() {
        return sendSnSpeedFourth;
    }

    /**
     * 设置四次送锡速度(单位:毫米/秒 mm/s)
     *
     * @param sendSnSpeedFourth 四次送锡速度
     */
    public void setSendSnSpeedFourth(int sendSnSpeedFourth) {
        this.sendSnSpeedFourth = sendSnSpeedFourth;
    }

    /**
     * @return 获取四次送锡量(单位:丝米dmm)
     */
    public int getSendSnSumFourth() {
        return sendSnSumFourth;
    }

    /**
     * 设置四次送锡量(单位:丝米dmm)
     *
     * @param sendSnSumFourth 四次送锡量
     */
    public void setSendSnSumFourth(int sendSnSumFourth) {
        this.sendSnSumFourth = sendSnSumFourth;
    }

    /**
     * @return 获取四次停锡时间
     */
    public int getStopSnTimeFourth() {
        return stopSnTimeFourth;
    }

    /**
     * 设置四次停锡时间
     *
     * @param stopSnTimeFourth 四次停锡时间
     */
    public void setStopSnTimeFourth(int stopSnTimeFourth) {
        this.stopSnTimeFourth = stopSnTimeFourth;
    }

    /**
     * @return 获取倾斜距离(单位:mm)
     */
    public int getDipDistance() {
        return dipDistance;
    }

    /**
     * 设置倾斜距离(单位:mm)
     *
     * @param dipDistance 倾斜距离
     */
    public void setDipDistance(int dipDistance) {
        this.dipDistance = dipDistance;
    }


    /**
     * @return 获取抬起高度(单位:mm)
     */
    public int getUpHeight() {
        return upHeight;
    }

    /**
     * 设置抬起高度(单位:mm)
     *
     * @param upHeight 抬起高度
     */
    public void setUpHeight(int upHeight) {
        this.upHeight = upHeight;
    }

    /**
     * @return 获取是否出锡
     */
    public boolean isSn() {
        return isSn;
    }

    /**
     * 设置是否出锡
     *
     * @param isSn 是否出锡
     */
    public void setSn(boolean isSn) {
        this.isSn = isSn;
    }



    /**
     * @return 倾斜角度
     */
    public int getDipDistance_angle() {
        return dipDistance_angle;
    }

    /**
     * 倾斜角度
     * @param dipDistance_angle
     */
    public void setDipDistance_angle(int dipDistance_angle) {
        this.dipDistance_angle = dipDistance_angle;
    }


    /**
     * @return 下降焊锡前是否减速标志            0: 不减速		1:减速
     * @deprecat
     */
    public boolean isSus() {
        return isSus;
    }

    /**
     * 下降焊锡前是否减速标志			0: 不减速		1:减速
     *
     * @param isSus
     */
    public void setSus(boolean isSus) {
        this.isSus = isSus;
    }


	/**
	 * @return 获取是否暂停
	 */
	public boolean isPause() {
		return isPause;
	}

	/**
	 * 设置是否暂停
	 * @param isPause 是否暂停
	 */
	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

    /**
     * 焊锡作业点默认构造函数,默认值为:
     *
     * @preHeatTime 预热时间 0
     * @sendSnSpeedFir 一次送锡速度20
     * @sendSnSumFir 一次送锡量0
     * @sendSnSpeedSec 二次送锡速度 10
     * @sendSnSumSec 二次送锡量 0
     * @stopSnStimeSec 二次停锡时间 0
     * @sendSnSpeedThird 三次送锡速度 10
     * @sendSnSumThird 三次送锡量 0
     * @stopSnTimeThird 三次停锡时间 0
     * @sendSnSpeedFourth 四次送锡速度 10
     * @sendSnSumFourth 四次送锡量 0
     * @stopSnTimeFourth 四次停锡时间 0
     * @dipDistance 倾斜距离 0
     * @stopSnTime 停锡延时 0
     * @upHeight 抬起高度 10
     * @isSn 是否出锡 是
     * @isPause 是否暂停 否
     * @snPort 10000000000000000000
     */
    public PointWeldWorkParam() {
        this.preHeatTime = 0;
        this.sendSnSpeedFir = 20;
        this.sendSnSumFir = 0;
        this.sendSnSpeedSec = 10;
        this.sendSnSumSec = 0;
        this.stopSnStimeSec = 0;
        this.sendSnSpeedThird = 10;
        this.sendSnSumThird = 0;
        this.stopSnTimeThird = 0;
        this.sendSnSpeedFourth = 10;
        this.sendSnSumFourth = 0;
        this.stopSnTimeFourth = 0;
        this.dipDistance = 0;
        this.upHeight = 10;
        this.isSn = true;
        this.isPause = false;
        this.dipDistance_angle=0;
        this.isSus=false;
        super.setPointType(PointType.POINT_WELD_WORK);
    }

    public PointWeldWorkParam(int preHeatTime, int sendSnSpeedFir, int sendSnSumFir, int sendSnSpeedSec, int sendSnSumSec, int stopSnStimeSec, int sendSnSpeedThird, int sendSnSumThird, int stopSnTimeThird, int sendSnSpeedFourth, int sendSnSumFourth, int stopSnTimeFourth,
                              int dipDistance, int upHeight, boolean isSn, boolean isPause, int dipDistance_angle, boolean isSus) {
        this.preHeatTime = preHeatTime;
        this.sendSnSpeedFir = sendSnSpeedFir;
        this.sendSnSumFir = sendSnSumFir;
        this.sendSnSpeedSec = sendSnSpeedSec;
        this.sendSnSumSec = sendSnSumSec;
        this.stopSnStimeSec = stopSnStimeSec;
        this.sendSnSpeedThird = sendSnSpeedThird;
        this.sendSnSumThird = sendSnSumThird;
        this.stopSnTimeThird = stopSnTimeThird;
        this.sendSnSpeedFourth = sendSnSpeedFourth;
        this.sendSnSumFourth = sendSnSumFourth;
        this.stopSnTimeFourth = stopSnTimeFourth;
        this.dipDistance = dipDistance;
        this.upHeight = upHeight;
        this.isSn = isSn;
        this.isPause = isPause;
        this.dipDistance_angle = dipDistance_angle;
        this.isSus = isSus;
        super.setPointType(PointType.POINT_WELD_WORK);
    }

    @Override
    public String toString() {
        return "PointWeldAloneParam{" +
                "preHeatTime=" + preHeatTime +
                ", sendSnSpeedFir=" + sendSnSpeedFir +
                ", sendSnSumFir=" + sendSnSumFir +
                ", sendSnSpeedSec=" + sendSnSpeedSec +
                ", sendSnSumSec=" + sendSnSumSec +
                ", stopSnStimeSec=" + stopSnStimeSec +
                ", sendSnSpeedThird=" + sendSnSpeedThird +
                ", sendSnSumThird=" + sendSnSumThird +
                ", stopSnTimeThird=" + stopSnTimeThird +
                ", sendSnSpeedFourth=" + sendSnSpeedFourth +
                ", sendSnSumFourth=" + sendSnSumFourth +
                ", stopSnTimeFourth=" + stopSnTimeFourth +
                ", dipDistance=" + dipDistance +
                ", upHeight=" + upHeight +
                ", isSn=" + isSn +
                ", isPause=" + isPause +
                ", dipDistance_angle=" + dipDistance_angle +
                ", isSus=" + isSus +
                '}';
    }
    public String getString(){
        return "PointWeldAloneParam{" +
                "preHeatTime=" + preHeatTime +
                ", sendSnSpeedFir=" + sendSnSpeedFir +
                ", sendSnSumFir=" + sendSnSumFir +
                ", sendSnSpeedSec=" + sendSnSpeedSec +
                ", sendSnSumSec=" + sendSnSumSec +
                ", stopSnStimeSec=" + stopSnStimeSec +
                ", sendSnSpeedThird=" + sendSnSpeedThird +
                ", sendSnSumThird=" + sendSnSumThird +
                ", stopSnTimeThird=" + stopSnTimeThird +
                ", sendSnSpeedFourth=" + sendSnSpeedFourth +
                ", sendSnSumFourth=" + sendSnSumFourth +
                ", stopSnTimeFourth=" + stopSnTimeFourth +
                ", dipDistance=" + dipDistance +
                ", upHeight=" + upHeight +
                ", isSn=" + isSn +
                ", isPause=" + isPause +
                ", dipDistance_angle=" + dipDistance_angle +
                ", isSus=" + isSus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PointWeldWorkParam that = (PointWeldWorkParam) o;

        if (preHeatTime != that.preHeatTime) return false;
        if (sendSnSpeedFir != that.sendSnSpeedFir) return false;
        if (sendSnSumFir != that.sendSnSumFir) return false;
        if (sendSnSpeedSec != that.sendSnSpeedSec) return false;
        if (sendSnSumSec != that.sendSnSumSec) return false;
        if (stopSnStimeSec != that.stopSnStimeSec) return false;
        if (sendSnSpeedThird != that.sendSnSpeedThird) return false;
        if (sendSnSumThird != that.sendSnSumThird) return false;
        if (stopSnTimeThird != that.stopSnTimeThird) return false;
        if (sendSnSpeedFourth != that.sendSnSpeedFourth) return false;
        if (sendSnSumFourth != that.sendSnSumFourth) return false;
        if (stopSnTimeFourth != that.stopSnTimeFourth) return false;
        if (dipDistance != that.dipDistance) return false;
        if (dipDistance_angle != that.dipDistance_angle) return false;
        if (upHeight != that.upHeight) return false;
        if (isSn != that.isSn) return false;
        if (isSus != that.isSus) return false;
        return isPause == that.isPause;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + preHeatTime;
        result = 31 * result + sendSnSpeedFir;
        result = 31 * result + sendSnSumFir;
        result = 31 * result + sendSnSpeedSec;
        result = 31 * result + sendSnSumSec;
        result = 31 * result + stopSnStimeSec;
        result = 31 * result + sendSnSpeedThird;
        result = 31 * result + sendSnSumThird;
        result = 31 * result + stopSnTimeThird;
        result = 31 * result + sendSnSpeedFourth;
        result = 31 * result + sendSnSumFourth;
        result = 31 * result + stopSnTimeFourth;
        result = 31 * result + dipDistance;
        result = 31 * result + dipDistance_angle;
        result = 31 * result + upHeight;
        result = 31 * result + (isSn ? 1 : 0);
        result = 31 * result + (isSus ? 1 : 0);
        result = 31 * result + (isPause ? 1 : 0);
        return result;
    }

    public static final Creator<PointWeldWorkParam> CREATOR = new Creator<PointWeldWorkParam>() {
        @Override
        public PointWeldWorkParam createFromParcel(Parcel source) {
            PointWeldWorkParam point = new PointWeldWorkParam();
            point.preHeatTime = source.readInt();
            point.sendSnSpeedFir = source.readInt();
            point.sendSnSumFir = source.readInt();
            point.sendSnSpeedSec = source.readInt();
            point.sendSnSumSec = source.readInt();
            point.stopSnStimeSec = source.readInt();
            point.sendSnSpeedThird = source.readInt();
            point.sendSnSumThird = source.readInt();
            point.stopSnTimeThird = source.readInt();
            point.sendSnSpeedFourth = source.readInt();
            point.sendSnSumFourth = source.readInt();
            point.stopSnTimeFourth = source.readInt();
            point.dipDistance = source.readInt();
            point.upHeight = source.readInt();
            point.isSn = source.readInt() != 0;
            point.isPause = source.readInt() != 0;
            point.dipDistance_angle = source.readInt();
            point.isSus = source.readInt() != 0;
            point.set_id(source.readInt());
            return point;
        }

        @Override
        public PointWeldWorkParam[] newArray(int size) {
            return new PointWeldWorkParam[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(preHeatTime);
        dest.writeInt(sendSnSpeedFir);
        dest.writeInt(sendSnSumFir);
        dest.writeInt(sendSnSpeedSec);
        dest.writeInt(sendSnSumSec);
        dest.writeInt(stopSnStimeSec);
        dest.writeInt(sendSnSpeedThird);
        dest.writeInt(sendSnSumThird);
        dest.writeInt(stopSnTimeThird);
        dest.writeInt(sendSnSpeedFourth);
        dest.writeInt(sendSnSumFourth);
        dest.writeInt(stopSnTimeFourth);
        dest.writeInt(dipDistance);
        dest.writeInt(upHeight);
        dest.writeInt(isSn?1:0);
        dest.writeInt(isPause?1:0);
        dest.writeInt(dipDistance_angle);
        dest.writeInt(isSus?1:0);
        dest.writeInt(get_id());
    }
}
