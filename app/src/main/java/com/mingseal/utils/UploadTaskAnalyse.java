/**
 * 
 */
package com.mingseal.utils;

import android.content.Context;

import com.mingseal.data.dao.WeldBlowDao;
import com.mingseal.data.dao.WeldLineEndDao;
import com.mingseal.data.dao.WeldLineMidDao;
import com.mingseal.data.dao.WeldLineStartDao;
import com.mingseal.data.dao.WeldWorkDao;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointParam;
import com.mingseal.data.point.PointType;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 商炎炳
 * @description 上传解析数据成功之后需要把数据变成自己想要的List格式
 */
public class UploadTaskAnalyse {

	private static final String TAG = "UploadTaskAnalyse";
	private  String taskname;
	/**
	 * 独立点的数据库操作改为作业点
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
	/**
	 * 清胶点的数据库操作改为吹锡点
	 */
	private WeldBlowDao weldBlowDao;// 吹锡点的数据库操作

	private int weldWork_key=0;
	private int weldLineStart_key=0;
	private int weldLineMid_key=0;
	private int weldLineEnd_key=0;
	private int weldBlow_key=0;
	/**
	 * 初始化各个和数据库操作的Dao
	 *
	 * @param context
	 * @param taskname
	 */
	public UploadTaskAnalyse(Context context, String taskname) {
		weldWorkDao = new WeldWorkDao(context);
		weldLineStartDao = new WeldLineStartDao(context);
		weldLineMidDao = new WeldLineMidDao(context);
		weldLineEndDao = new WeldLineEndDao(context);
		weldBlowDao = new WeldBlowDao(context);
		this.taskname = taskname;
	}

	/**
	 * 解析下载成功的任务（*重写HashCode方法*）
	 * 
	 * @param pointUploads
	 * @return 解析之后的List数据
	 */
	public List<Point> analyseTaskSuccess(List<Point> pointUploads) {
		// 上传成功里面的Point的List数组
		List<Point> points = new ArrayList<>();
		// 用于上传成功之后数据的PointParam的解析
		PointParam pointParam = null;
		// 作业点参数
		PointWeldWorkParam aloneParam = null;
		// 起始点参数
		PointWeldLineStartParam lineStartParam = null;
		// 中间点参数
		PointWeldLineMidParam lineMidParam = null;
		// 结束点参数
		PointWeldLineEndParam lineEndParam = null;
		//吹锡点
		PointWeldBlowParam    blowParam=  null;
		// 各个点胶口的初始化，因为下载是有24个的，实际上保存的时候是不需要这么多的
		boolean[] ports = null;
		// 作业点HashMap集合
		HashMap<String, Integer> aloneParamMaps = new HashMap<>();
		// 起始点HashMap集合
		HashMap<String, Integer> lineStartParamMaps = new HashMap<>();
		// 中间点HashMap集合
		HashMap<String, Integer> lineMidParamMaps = new HashMap<>();
		// 结束点HashMap集合
		HashMap<String, Integer> lineEndParamMaps = new HashMap<>();
		//吹锡点集合
		HashMap<String, Integer> blowParamMaps = new HashMap<>();

		for (Point point : pointUploads) {
			if (point.getPointParam().getPointType() == PointType.POINT_WELD_BASE) {
				// 基准点解析
				points.add(point);
			} else if (point.getPointParam().getPointType() == PointType.POINT_WELD_WORK) {
				// 作业点解析
				aloneParam = (PointWeldWorkParam) point.getPointParam();
//				ports = new boolean[20];
//				for (int i = 0; i < 20; i++) {
//					ports[i] = aloneParam.getGluePort()[i];
//				}
//				aloneParam.setGluePort(ports);

				// 先判断Map里面有没有，有的话，直接添加，无需查询数据库
				pointParam = new PointParam();
				if (aloneParamMaps.containsKey(aloneParam.getString())) {
					pointParam.set_id(aloneParamMaps.get(aloneParam.getString()));
				} else {
					//自增主键从1开始,如果大于10，则使用第一种方案
					weldWork_key=weldWork_key+1;
					if (weldWork_key>10){
						pointParam.set_id(1);
					}else {

						aloneParamMaps.put(aloneParam.getString(),weldWork_key);
						aloneParam.set_id(weldWork_key);
						int rowid=(int) weldWorkDao.insertWeldWork(aloneParam, taskname);
						L.d("插入数据库数据："+weldWorkDao.getPointWeldWorkParamById(weldWork_key,taskname).toString());

						L.d("aloneParamMaps.contains(aloneParam.hashCode())::"+aloneParamMaps.containsKey(aloneParam.toString()));
						pointParam.set_id(weldWork_key);
					}
				}

				pointParam.setPointType(PointType.POINT_WELD_WORK);
				point.setPointParam(pointParam);
				points.add(point);

			} else if (point.getPointParam().getPointType() == PointType.POINT_WELD_LINE_START) {
				// 起始点解析
				lineStartParam = (PointWeldLineStartParam) point.getPointParam();
//				ports = new boolean[20];
//				for (int i = 0; i < 20; i++) {
//					ports[i] = lineStartParam.getGluePort()[i];
//				}
//				lineStartParam.setGluePort(ports);
				// 先判断Map里面有没有，有的话，直接添加，无需查询数据库
				pointParam = new PointParam();
				if (lineStartParamMaps.containsKey(lineStartParam.getString())) {
					pointParam.set_id(lineStartParamMaps.get(lineStartParam.getString()));
				} else {
					//自增主键从1开始
					weldLineStart_key=weldLineStart_key+1;
					if (weldLineStart_key>10){
						pointParam.set_id(1);

					}else {

						lineStartParamMaps.put(lineStartParam.getString(), weldLineStart_key);
						lineStartParam.set_id(weldLineStart_key);
						int rowid=(int) weldLineStartDao.insertWeldLineStart(lineStartParam, taskname);
						pointParam.set_id(weldLineStart_key);
					}
				}

				pointParam.setPointType(PointType.POINT_WELD_LINE_START);
				point.setPointParam(pointParam);
				points.add(point);

			} else if (point.getPointParam().getPointType() == PointType.POINT_WELD_LINE_MID) {
				// 中间点解析
				lineMidParam = (PointWeldLineMidParam) point.getPointParam();
//				ports = new boolean[20];
//				for (int i = 0; i < 20; i++) {
//					ports[i] = lineMidParam.getGluePort()[i];
//				}
//				lineMidParam.setGluePort(ports);
				pointParam = new PointParam();
				if (lineMidParamMaps.containsKey(lineMidParam.getString())) {
					pointParam.set_id(lineMidParamMaps.get(lineMidParam.getString()));
				} else {
					//自增主键从1开始
					weldLineMid_key=weldLineMid_key+1;
					if (weldLineMid_key>10){
						pointParam.set_id(1);

					}else {

						lineMidParamMaps.put(lineMidParam.getString(), weldLineMid_key);
						lineMidParam.set_id(weldLineMid_key);
						int rowid=(int) weldLineMidDao.insertWeldLineMid(lineMidParam, taskname);
						pointParam.set_id(weldLineMid_key);
					}
				}

				pointParam.setPointType(PointType.POINT_WELD_LINE_MID);
				point.setPointParam(pointParam);
				points.add(point);

			}  else if (point.getPointParam().getPointType() == PointType.POINT_WELD_LINE_END) {
				// 结束点解析
				lineEndParam = (PointWeldLineEndParam) point.getPointParam();
				pointParam = new PointParam();
				if (lineEndParamMaps.containsKey(lineEndParam.getString())) {
					pointParam.set_id(lineEndParamMaps.get(lineEndParam.getString()));
				} else {
					//自增主键从1开始
					weldLineEnd_key=weldLineEnd_key+1;
					if (weldLineEnd_key>10){
						pointParam.set_id(1);

					}else {

						lineEndParamMaps.put(lineEndParam.getString(), weldLineEnd_key);
						lineEndParam.set_id(weldLineEnd_key);
						int rowid=(int) weldLineEndDao.insertWeldLineEnd(lineEndParam, taskname);
						pointParam.set_id(weldLineEnd_key);
					}
				}

				pointParam.setPointType(PointType.POINT_WELD_LINE_END);
				point.setPointParam(pointParam);
				points.add(point);

			} else if (point.getPointParam().getPointType() == PointType.POINT_WELD_BLOW) {
				// 输出IO
				blowParam = (PointWeldBlowParam) point.getPointParam();
				pointParam = new PointParam();

				if (blowParamMaps.containsKey(blowParam.getString())) {
					pointParam.set_id(blowParamMaps.get(blowParam.getString()));
				} else {
					//自增主键从1开始
					weldBlow_key=weldBlow_key+1;
					if (weldBlow_key>10){
						pointParam.set_id(1);

					}else {

						blowParamMaps.put(blowParam.getString(), weldBlow_key);
						blowParam.set_id(weldBlow_key);
						int rowid=(int) weldBlowDao.insertWeldOutput(blowParam, taskname);
						pointParam.set_id(weldBlow_key);
					}
				}

				pointParam.setPointType(PointType.POINT_WELD_BLOW);
				point.setPointParam(pointParam);
				points.add(point);

			}
		}
		return points;
	}
}
