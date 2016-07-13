/**
 * 
 */
package com.mingseal.utils;

import android.content.Context;

import com.mingseal.data.dao.WeldBlowDao;
import com.mingseal.data.dao.WeldInputDao;
import com.mingseal.data.dao.WeldLineEndDao;
import com.mingseal.data.dao.WeldLineMidDao;
import com.mingseal.data.dao.WeldLineStartDao;
import com.mingseal.data.dao.WeldOutputDao;
import com.mingseal.data.dao.WeldWorkDao;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointParam;
import com.mingseal.data.point.PointType;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldOutputIOParam;
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
	/**
	 * 输入IO的数据库操作
	 */
	private WeldInputDao weldInputDao;// 输入IO的数据库操作
	/**
	 * 输出IO的数据库操作
	 */
	private WeldOutputDao weldOutputDao;// 输出IO的数据库操作

	/**
	 * 初始化各个和数据库操作的Dao
	 * 
	 * @param context
	 */
	public UploadTaskAnalyse(Context context) {
		weldWorkDao = new WeldWorkDao(context);
		weldLineStartDao = new WeldLineStartDao(context);
		weldLineMidDao = new WeldLineMidDao(context);
		weldLineEndDao = new WeldLineEndDao(context);
		weldBlowDao = new WeldBlowDao(context);
		weldInputDao = new WeldInputDao(context);
		weldOutputDao = new WeldOutputDao(context);
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
		// 输入IO参数
		PointWeldInputIOParam inputParam = null;
		// 输出IO参数
		PointWeldOutputIOParam outputParam = null;
		// 各个点胶口的初始化，因为下载是有24个的，实际上保存的时候是不需要这么多的
		boolean[] ports = null;
		// 作业点HashMap集合
		HashMap<PointWeldWorkParam, Integer> aloneParamMaps = new HashMap<>();
		// 起始点HashMap集合
		HashMap<PointWeldLineStartParam, Integer> lineStartParamMaps = new HashMap<>();
		// 中间点HashMap集合
		HashMap<PointWeldLineMidParam, Integer> lineMidParamMaps = new HashMap<>();
		// 结束点HashMap集合
		HashMap<PointWeldLineEndParam, Integer> lineEndParamMaps = new HashMap<>();
		//吹锡点集合
		HashMap<PointWeldBlowParam, Integer> blowParamMaps = new HashMap<>();
		// 输入IO点HashMap集合
		HashMap<PointWeldInputIOParam, Integer> inputParamMaps = new HashMap<>();
		// 输出IO点HashMap集合
		HashMap<PointWeldOutputIOParam, Integer> outputParamMaps = new HashMap<>();
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
				if (aloneParamMaps.containsKey(aloneParam)) {
					pointParam.set_id(aloneParamMaps.get(aloneParam));
				} else {
					int _id = weldWorkDao.getAloneParamIdByParam(aloneParam);
					pointParam.set_id(_id);
					aloneParamMaps.put(aloneParam, _id);
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
				if (lineStartParamMaps.containsKey(lineStartParam)) {
					pointParam.set_id(lineStartParamMaps.get(lineStartParam));
				} else {
					int _id = weldLineStartDao.getLineStartParamIDByParam(lineStartParam);
					pointParam.set_id(_id);
					lineStartParamMaps.put(lineStartParam, _id);
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
				if (lineMidParamMaps.containsKey(lineMidParam)) {
					pointParam.set_id(lineMidParamMaps.get(lineMidParam));
				} else {
					int _id = weldLineMidDao.getLineMidParamIDByParam(lineMidParam);
					pointParam.set_id(_id);
					lineMidParamMaps.put(lineMidParam, _id);
				}

				pointParam.setPointType(PointType.POINT_WELD_LINE_MID);
				point.setPointParam(pointParam);
				points.add(point);

			}  else if (point.getPointParam().getPointType() == PointType.POINT_WELD_LINE_END) {
				// 结束点解析
				lineEndParam = (PointWeldLineEndParam) point.getPointParam();
				pointParam = new PointParam();
				if (lineEndParamMaps.containsKey(lineEndParam)) {
					pointParam.set_id(lineEndParamMaps.get(lineEndParam));
				} else {
					int _id = weldLineEndDao.getLineEndParamIDByParam(lineEndParam);
					pointParam.set_id(_id);
					lineEndParamMaps.put(lineEndParam, _id);
				}

				pointParam.setPointType(PointType.POINT_WELD_LINE_END);
				point.setPointParam(pointParam);
				points.add(point);

			} else if (point.getPointParam().getPointType() == PointType.POINT_WELD_INPUT) {
				// 输入IO
				inputParam = (PointWeldInputIOParam) point.getPointParam();
				pointParam = new PointParam();

				if (inputParamMaps.containsKey(inputParam)) {
					pointParam.set_id(inputParamMaps.get(inputParam));
				} else {
					int _id = weldInputDao.getInputParamIDByParam(inputParam);
					pointParam.set_id(_id);
					inputParamMaps.put(inputParam, _id);
				}

				pointParam.setPointType(PointType.POINT_WELD_INPUT);
				point.setPointParam(pointParam);
				points.add(point);

			} else if (point.getPointParam().getPointType() == PointType.POINT_WELD_OUTPUT) {
				// 输出IO
				outputParam = (PointWeldOutputIOParam) point.getPointParam();
				pointParam = new PointParam();

				if (outputParamMaps.containsKey(outputParam)) {
					pointParam.set_id(outputParamMaps.get(outputParam));
				} else {
					int _id = weldOutputDao.getOutputParamIDByParam(outputParam);
					pointParam.set_id(_id);
					outputParamMaps.put(outputParam, _id);
				}

				pointParam.setPointType(PointType.POINT_WELD_OUTPUT);
				point.setPointParam(pointParam);
				points.add(point);

			}else if (point.getPointParam().getPointType() == PointType.POINT_WELD_BLOW) {
				// 输出IO
				blowParam = (PointWeldBlowParam) point.getPointParam();
				pointParam = new PointParam();

				if (blowParamMaps.containsKey(blowParam)) {
					pointParam.set_id(blowParamMaps.get(blowParam));
				} else {
					int _id = weldBlowDao.getOutputParamIDByParam(blowParam);
					pointParam.set_id(_id);
					blowParamMaps.put(blowParam, _id);
				}

				pointParam.setPointType(PointType.POINT_WELD_BLOW);
				point.setPointParam(pointParam);
				points.add(point);

			}
		}
		return points;
	}
}
