package com.mingseal.data.point;

import android.os.Parcel;
import android.os.Parcelable;

import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.point.weldparam.PointWeldBaseParam;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldOutputIOParam;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;

import java.util.HashMap;

/**
 * 点类
 *
 * @author wj
 */
public class Point implements Parcelable {
    private int id;// 主键
    private float x;
    private float y;
    private float z;
    private float u;

    private PointParam pointParam; // 点参数

    /*=================== begin ===================*/
    static HashMap<PointType,PointParam>	cachesPointParam	= new HashMap<PointType,PointParam>();
	/*===================  add  ===================*/

    /**
     * 只用于初始化一个Point，不能用作点类型的添加
     */
	/*
	 * public Point() { super(); }
	 */
    public Point(){

    }
    /**
     * 点类构造函数,坐标默认为原点
     *
     * @param pointType
     *            点类型
     */
    public Point(PointType pointType) {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.u = 0;
        ParamFactory(pointType);
    }

    /**
     * 点类构造函数
     *
     * @param x
     * @param y
     * @param z
     * @param u
     * @param pointType
     *            点类型
     */
    public Point(float x, float y, float z, float u, PointType pointType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        ParamFactory(pointType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }

    /**
     * 获取倍数之后的X
     *
     * @param fold
     * @return 获取倍数之后的X
     */
    public int getFoldX(int fold) {
        return RobotParam.INSTANCE.XJourney2Pulse(this.x)/fold ;
    }

    /**
     * 获取倍数之后的Y
     *
     * @param fold
     * @return 获取倍数之后的Y
     */
    public int getFoldY(int fold) {
        return RobotParam.INSTANCE.YJourney2Pulse(this.y)/fold;
    }

    /**
     * 获取倍数之后的Z
     *
     * @param fold
     * @return 获取倍数之后的Z
     */
    public int getFoldZ(int fold) {
        return RobotParam.INSTANCE.ZJourney2Pulse(this.z)/fold;
    }

    /**
     * 获取倍数之后的U
     *
     * @param fold
     * @return 获取倍数之后的U
     */
    public int getFoldU(int fold) {
        return RobotParam.INSTANCE.UJourney2Pulse(this.u) / fold;
    }

    /**
     * @return 获取点参数
     */
    public PointParam getPointParam() {
        return pointParam;
    }

    /**
     * 设置点参数
     *
     * @param pointParam
     *            点参数
     */
    public void setPointParam(PointParam pointParam) {
        this.pointParam = pointParam;
    }

    @Override
    public String toString() {
        return "Point [id=" + id + ", x=" + x + ", y=" + y + ", z=" + z + ", u=" + u + ", pointParam=" + pointParam
                + "]";
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        result = 31 * result + (u != +0.0f ? Float.floatToIntBits(u) : 0);
        result = 31 * result + (pointParam != null ? pointParam.hashCode() : 0);
        return result;
    }

    //	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((pointParam == null) ? 0 : pointParam.hashCode());
//		result = prime * result + u;
//		result = prime * result + x;
//		result = prime * result + y;
//		result = prime * result + z;
//		return result;
//	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (pointParam == null) {
            if (other.pointParam != null)
                return false;
        } else if (!pointParam.equals(other.pointParam))
            return false;
        if (u != other.u)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }
    /**
     * 参数工厂
     *
     * @param pointType 点类型
     */
    private void ParamFactory(PointType pointType) {
		/*=================== begin ===================*/
        // 如果缓存里面有对应的fragment,就直接取出返回
        PointParam tempPointParam = cachesPointParam.get(pointType);
        if (tempPointParam != null) {
            this.pointParam = tempPointParam;
        }
		/*===================  add  ===================*/
        switch (pointType) {
            case POINT_WELD_BASE:
                this.pointParam = new PointWeldBaseParam();
                break;
            case POINT_WELD_WORK:
                this.pointParam = new PointWeldWorkParam();
                break;
            case POINT_WELD_LINE_START:
                this.pointParam = new PointWeldLineStartParam();
                break;
            case POINT_WELD_LINE_MID:
                this.pointParam = new PointWeldLineMidParam();
                break;
            case POINT_WELD_LINE_END:
                this.pointParam = new PointWeldLineEndParam();
                break;
            case POINT_WELD_BLOW:
                this.pointParam = new PointWeldBlowParam();
                break;
            case POINT_WELD_INPUT:
                this.pointParam=new PointWeldInputIOParam();
                break;
            case POINT_WELD_OUTPUT:
                this.pointParam=new PointWeldOutputIOParam();
                break;
            default:
                this.pointParam = new PointParam();
                break;
        }
		/*=================== begin ===================*/
        // 保存对应的fragment
        cachesPointParam.put(pointType, pointParam);
		/*===================  add  ===================*/
    }


    public static final Parcelable.Creator<Point> CREATOR = new Creator<Point>() {

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }

        @Override
        public Point createFromParcel(Parcel source) {
            Point point = new Point(PointType.POINT_GLUE_ALONE);
            point.x = source.readFloat();
            point.y = source.readFloat();
            point.z = source.readFloat();
            point.u = source.readFloat();
            point.pointParam = source.readParcelable(PointParam.class.getClassLoader());

            return point;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(z);
        dest.writeFloat(u);
        dest.writeParcelable(pointParam, flags);
    }

}
