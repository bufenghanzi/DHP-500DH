/**
 * 
 */
package com.mingseal.utils;

import android.content.Context;

import com.mingseal.application.UserApplication;
import com.mingseal.data.dao.WeldBlowDao;
import com.mingseal.data.dao.WeldInputDao;
import com.mingseal.data.dao.WeldLineEndDao;
import com.mingseal.data.dao.WeldLineMidDao;
import com.mingseal.data.dao.WeldLineStartDao;
import com.mingseal.data.dao.WeldOutputDao;
import com.mingseal.data.dao.WeldWorkDao;
import com.mingseal.data.point.Point;
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
 * @author wj
 * @description 各参数方案的设置
 */
public class ParamsSetting {

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
	private WeldBlowDao weldBlowDao;// 清胶点的数据库操作
	/**
	 * 输入IO的数据库操作
	 */
	private WeldInputDao weldInputDao;// 输入IO的数据库操作
	/**
	 * 输出IO的数据库操作
	 */
	private WeldOutputDao weldOutputDao;// 输出IO的数据库操作

	private Context context;

	/**
	 * 初始化各个和数据库操作的Dao
	 * 
	 * @param context
	 */
	public ParamsSetting(Context context) {
		weldWorkDao = new WeldWorkDao(context);
		weldLineStartDao = new WeldLineStartDao(context);
		weldLineMidDao = new WeldLineMidDao(context);
		weldLineEndDao = new WeldLineEndDao(context);
		weldBlowDao = new WeldBlowDao(context);
		weldInputDao = new WeldInputDao(context);
		weldOutputDao = new WeldOutputDao(context);
	}

	/**
	 * 将Point的List集合中每个点对应的参数类型方案设置到全局变量中
	 * <p>
	 * 最好是放到异步线程里面,因为要从数据库读取数据
	 * 
	 * @param userApplication
	 *            全局变量
	 * @param points
	 *            point的List集合
	 */
	public void setParamsToApplication(UserApplication userApplication, List<Point> points) {
		Point point;
		// 类型
		PointType pointType = PointType.POINT_NULL;
		// Point的任务参数序列
		int id = -1;
		// 保存独立点参数方案主键的List
		List<Integer> aloneIDs = new ArrayList<>();
		// 保存线起始点参数方案主键的List
		List<Integer> lineStartIDs = new ArrayList<>();
		// 保存线中间点参数方案主键的List
		List<Integer> lineMidIDs = new ArrayList<>();
		// 保存线结束点参数方案主键的List
		List<Integer> lineEndIDs = new ArrayList<>();

		// 保存吹锡点参数方案主键的List
		List<Integer> blowIDs = new ArrayList<>();
		// 保存输入IO参数方案主键的List
		List<Integer> inputIDs = new ArrayList<>();
		// 保存输出IO参数方案主键的List
		List<Integer> outputIDs = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			pointType = point.getPointParam().getPointType();
			id = point.getPointParam().get_id();
			if (pointType.equals(PointType.POINT_WELD_WORK)) {
				// 如果等于独立点
				if (!aloneIDs.contains(id)) {
					aloneIDs.add(id);
				}
			} else if (pointType.equals(PointType.POINT_WELD_LINE_START)) {
				// 线起始点
				if (!lineStartIDs.contains(id)) {
					lineStartIDs.add(id);
				}
			} else if (pointType.equals(PointType.POINT_WELD_LINE_MID)) {
				// 线中间点
				if (!lineMidIDs.contains(id)) {
					lineMidIDs.add(id);
				}
			} else if (pointType.equals(PointType.POINT_WELD_LINE_END)) {
				// 如果等于线结束点
				if (!lineEndIDs.contains(id)) {
					lineEndIDs.add(id);
				}
			} else if (pointType.equals(PointType.POINT_WELD_BLOW)) {
				// 如果等于吹锡点
				if (!blowIDs.contains(id)) {
					blowIDs.add(id);
				}
			} else if (pointType.equals(PointType.POINT_WELD_INPUT)) {
				// 如果等于输入IO
				if (!inputIDs.contains(id)) {
					inputIDs.add(id);
				}
			} else if (pointType.equals(PointType.POINT_WELD_OUTPUT)) {
				// 如果等于输出IO
				if (!outputIDs.contains(id)) {
					outputIDs.add(id);
				}
			}
		}
		// 存放独立点的参数方案
		HashMap<Integer, PointWeldWorkParam> aloneMaps = new HashMap<>();
		// 存放线起始点的参数方案
		HashMap<Integer, PointWeldLineStartParam> lineStartMaps = new HashMap<>();
		// 存放线中间点的参数方案
		HashMap<Integer, PointWeldLineMidParam> lineMidMaps = new HashMap<>();
		// 存放线结束点的参数方案
		HashMap<Integer, PointWeldLineEndParam> lineEndMaps = new HashMap<>();

		// 存放吹锡点的参数方案
		HashMap<Integer, PointWeldBlowParam> blowMaps = new HashMap<>();
		// 存放输入IO的参数方案
		HashMap<Integer, PointWeldInputIOParam> inputMaps = new HashMap<>();
		// 存放输出IO的参数方案
		HashMap<Integer, PointWeldOutputIOParam> outputMaps = new HashMap<>();

		// 获取所有独立点的参数方案
		if (!aloneIDs.isEmpty()) {
			List<PointWeldWorkParam> aloneParams = weldWorkDao.getWeldWorkParamsByIDs(aloneIDs);
			// 将方案和对应方案主键放到一个HashMap中
			for (int i = 0; i < aloneIDs.size(); i++) {
				aloneMaps.put(aloneIDs.get(i), aloneParams.get(i));
			}
		}

		// 获取所有线起始点的参数方案
		if (!lineStartIDs.isEmpty()) {
			List<PointWeldLineStartParam> lineStartParams = weldLineStartDao.getPointWeldLineStartParamsByIDs(lineStartIDs);
			for (int i = 0; i < lineStartIDs.size(); i++) {
				lineStartMaps.put(lineStartIDs.get(i), lineStartParams.get(i));
			}
		}

		// 获取所有线中间点的参数方案
		if (!lineMidIDs.isEmpty()) {
			List<PointWeldLineMidParam> lineMidParams = weldLineMidDao.getPointWeldLineMidParamsByIDs(lineMidIDs);

			for (int i = 0; i < lineMidIDs.size(); i++) {
				lineMidMaps.put(lineMidIDs.get(i), lineMidParams.get(i));
			}
		}

		// 获取所有线结束点的参数方案
		if (!lineEndIDs.isEmpty()) {
			List<PointWeldLineEndParam> lineEndParams = weldLineEndDao.getPointWeldLineEndParamsByIDs(lineEndIDs);

			for (int i = 0; i < lineEndIDs.size(); i++) {
				lineEndMaps.put(lineEndIDs.get(i), lineEndParams.get(i));
			}
		}
		// 获取所有吹锡点的参数方案
		if (!blowIDs.isEmpty()) {
			List<PointWeldBlowParam> clearParams = weldBlowDao.getWeldOutputIOParamsByIDs(blowIDs);
			for (int i = 0; i < blowIDs.size(); i++) {
				blowMaps.put(blowIDs.get(i), clearParams.get(i));
			}
		}

		// 获取所有输入IO点的参数方案
		if (!inputIDs.isEmpty()) {
			List<PointWeldInputIOParam> inputParams = weldInputDao.getWeldInputParamsByIDs(inputIDs);
			for (int i = 0; i < inputIDs.size(); i++) {
				inputMaps.put(inputIDs.get(i), inputParams.get(i));
			}
		}

		// 获取所有输出IO点的参数方案
		if (!outputIDs.isEmpty()) {
			List<PointWeldOutputIOParam> outputParams = weldOutputDao.getWeldOutputIOParamsByIDs(outputIDs);
			for (int i = 0; i < outputIDs.size(); i++) {
				outputMaps.put(outputIDs.get(i), outputParams.get(i));
			}
		}

		// 设置到全局变量中
		userApplication.setParamMaps(aloneMaps, lineStartMaps, lineMidMaps, lineEndMaps,blowMaps, inputMaps, outputMaps);
	}
}
