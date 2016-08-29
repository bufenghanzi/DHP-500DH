/**
 * 
 */
package com.mingseal.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingseal.data.db.DBHelper;
import com.mingseal.data.db.DBInfo.TableLineStart;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjian 焊锡起始点
 * 
 */
public class WeldLineStartDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;
	String[] columns = { TableLineStart._ID, TableLineStart.SENDSNSPEED,
			TableLineStart.PRESENDSNSUM, TableLineStart.PRESENDSNSPEED,
			TableLineStart.ISSN, TableLineStart.MOVESPEED,
			TableLineStart.PREHEATTIME };

	public WeldLineStartDao(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * @Title upDateGlueAlone
	 * @Description 更新一条独立点数据
	 * @author wj
	 * @param
	 * @return 影响的行数，0表示错误
	 */
	public int upDateWeldLineStart(PointWeldLineStartParam pointWeldLineStartParam,String taskname) {
		int rowid = 0;
		try {
			db = dbHelper.getWritableDatabase();
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableLineStart.SENDSNSPEED,
					pointWeldLineStartParam.getPreSendSnSpeed());
			values.put(TableLineStart.PRESENDSNSUM,
					pointWeldLineStartParam.getPreSendSnSum());
			values.put(TableLineStart.PRESENDSNSPEED,
					pointWeldLineStartParam.getPreSendSnSpeed());
			values.put(TableLineStart.ISSN,
					(boolean) pointWeldLineStartParam.isSn() ? 1 : 0);
			values.put(TableLineStart.MOVESPEED,
					pointWeldLineStartParam.getMoveSpeed());
			values.put(TableLineStart.PREHEATTIME,
					pointWeldLineStartParam.getPreHeatTime());
			rowid = db.update(TableLineStart.LINE_START_TABLE+taskname, values,
					TableLineStart._ID + "=?", new String[] { String
							.valueOf(pointWeldLineStartParam.get_id()) });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return rowid;
	}

	/**
	 * 增加一条线起始点的数据
	 *
	 * @param pointWeldLineStartParam
	 * @return 刚增加的这条数据的主键
	 */
	public long insertWeldLineStart(
			PointWeldLineStartParam pointWeldLineStartParam,String taskname) {
		long rowID = 0;
		db = dbHelper.getWritableDatabase();
		try {
			db.beginTransaction();

			values = new ContentValues();
			values.put(TableLineStart._ID, pointWeldLineStartParam.get_id());
			values.put(TableLineStart.SENDSNSPEED,
					pointWeldLineStartParam.getPreSendSnSpeed());
			values.put(TableLineStart.PRESENDSNSUM,
					pointWeldLineStartParam.getPreSendSnSum());
			values.put(TableLineStart.PRESENDSNSPEED,
					pointWeldLineStartParam.getPreSendSnSpeed());
			values.put(TableLineStart.ISSN,
					(boolean) pointWeldLineStartParam.isSn() ? 1 : 0);
			values.put(TableLineStart.MOVESPEED,
					pointWeldLineStartParam.getMoveSpeed());
			values.put(TableLineStart.PREHEATTIME,
					pointWeldLineStartParam.getPreHeatTime());

			rowID = db.insert(TableLineStart.LINE_START_TABLE+taskname,
					TableLineStart._ID, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			// 释放资源
			db.close();
		}
		return rowID;
	}

	/**
	 * 取得所有线起始点的数据
	 *
	 * @return
	 */
	public List<PointWeldLineStartParam> findAllWeldLineStartParams(String taskname) {
		db = dbHelper.getReadableDatabase();
		List<PointWeldLineStartParam> startLists = null;
		PointWeldLineStartParam start = null;

		Cursor cursor = null;
		try {
			cursor = db.query(TableLineStart.LINE_START_TABLE+taskname, columns,
                    null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
                startLists = new ArrayList<PointWeldLineStartParam>();
                while (cursor.moveToNext()) {
                    start = new PointWeldLineStartParam();
                    start.set_id(cursor.getInt(cursor
                            .getColumnIndex(TableLineStart._ID)));
                    start.setSnSpeed(cursor.getInt(cursor
                            .getColumnIndex(TableLineStart.SENDSNSPEED)));
                    start.setPreSendSnSum(cursor.getInt(cursor
                            .getColumnIndex(TableLineStart.PRESENDSNSUM)));
                    start.setPreSendSnSpeed(cursor.getInt(cursor
                            .getColumnIndex(TableLineStart.PRESENDSNSPEED)));
                    start.setSn(cursor.getInt(cursor
                            .getColumnIndex(TableLineStart.ISSN))== 0 ? false
							: true);
                    start.setMoveSpeed(cursor.getInt(cursor
                            .getColumnIndex(TableLineStart.MOVESPEED)));
                    start.setPreHeatTime(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.PREHEATTIME)));

                    startLists.add(start);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor!=null){
				cursor.close();
			}
		}
		db.close();
		return startLists;
	}

	/**
	 * 通过主键id找到PointWeldLineStartParam参数
	 *
	 * @param id
	 * @return PointWeldLineStartParam
	 */
	public PointWeldLineStartParam getPointWeldLineStartParamByID(int id,String taskname) {
		PointWeldLineStartParam param = new PointWeldLineStartParam();
		db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TableLineStart.LINE_START_TABLE+taskname, columns,
					TableLineStart._ID + "=?", new String[] { String.valueOf(id) },
					null, null, null);
			db.beginTransaction();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					param.set_id(cursor.getInt(cursor.getColumnIndex(TableLineStart._ID)));
					param.setSnSpeed(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.SENDSNSPEED)));
					param.setPreSendSnSum(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.PRESENDSNSUM)));
					param.setPreSendSnSpeed(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.PRESENDSNSPEED)));
					param.setSn(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.ISSN))== 0 ? false
							: true);
					param.setMoveSpeed(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.MOVESPEED)));
					param.setPreHeatTime(cursor.getInt(cursor
							.getColumnIndex(TableLineStart.PREHEATTIME)));

				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor!=null){
				cursor.close();
			}
			db.endTransaction();
			db.close();
		}
		return param;
	}

	/**
	 * 通过List<Integer> 列表来查找对应的 PointGlueLineStartParam集合
	 *
	 * @param ids
	 * @return List<PointWeldLineStartParam>
	 */
	public List<PointWeldLineStartParam> getPointWeldLineStartParamsByIDs(
			List<Integer> ids,String taskname) {
		db = dbHelper.getReadableDatabase();
		List<PointWeldLineStartParam> params = new ArrayList<>();
		PointWeldLineStartParam param = null;
		try {
			db.beginTransaction();
			for (Integer id : ids) {
				Cursor cursor = db.query(TableLineStart.LINE_START_TABLE+taskname,
						columns, TableLineStart._ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						param = new PointWeldLineStartParam();
						param.set_id(cursor.getInt(cursor
								.getColumnIndex(TableLineStart._ID)));
						param.setSnSpeed(cursor.getInt(cursor
								.getColumnIndex(TableLineStart.SENDSNSPEED)));
						param.setPreSendSnSum(cursor.getInt(cursor
								.getColumnIndex(TableLineStart.PRESENDSNSUM)));
						param.setPreSendSnSpeed(cursor.getInt(cursor
								.getColumnIndex(TableLineStart.PRESENDSNSPEED)));
						param.setSn(cursor.getInt(cursor
								.getColumnIndex(TableLineStart.ISSN))== 0 ? false
								: true);
						param.setMoveSpeed(cursor.getInt(cursor
								.getColumnIndex(TableLineStart.MOVESPEED)));
						param.setPreHeatTime(cursor.getInt(cursor
								.getColumnIndex(TableLineStart.PREHEATTIME)));
						params.add(param);
					}
				}
				cursor.close();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return params;
	}

	/**
	 * 通过参数方案找到当前方案的主键(是否出胶,停胶前延时,停胶后延时,抬起高度起始点是没有数据的)
	 *
	 * @param pointWeldLineStartParam
	 * @return 当前方案的主键
	 */
	public int getLineStartParamIDByParam(
			PointWeldLineStartParam pointWeldLineStartParam,String taskname) {
		int id = -1;
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db
				.query(TableLineStart.LINE_START_TABLE+taskname,
						columns,
						TableLineStart.SENDSNSPEED + "=? and "
								+ TableLineStart.PRESENDSNSUM + "=? and "
								+ TableLineStart.PRESENDSNSPEED + "=? and "
								+ TableLineStart.ISSN + "=? and "
								+ TableLineStart.MOVESPEED + "=? and "
								+ TableLineStart.PREHEATTIME
								+ "=?",
						new String[] {
								String.valueOf(pointWeldLineStartParam
										.getSnSpeed()),
								String.valueOf(pointWeldLineStartParam
										.getPreSendSnSum()),
								String.valueOf(pointWeldLineStartParam
										.getPreSendSnSpeed()),
								String.valueOf(pointWeldLineStartParam
										.isSn() ? 1 : 0),
								String.valueOf(pointWeldLineStartParam
										.getMoveSpeed()),
								String.valueOf(pointWeldLineStartParam
										.getPreHeatTime())}, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex(TableLineStart._ID));
			}
		}
		db.close();
		if (-1 == id) {
			// 先查询除了是否出胶,停胶前延时,停胶后延时,抬起高度之外有没有相对应的方案参数
			db = dbHelper.getReadableDatabase();
			cursor = db
					.query(TableLineStart.LINE_START_TABLE+taskname,
							columns,
							TableLineStart.SENDSNSPEED + "=? and "
									+ TableLineStart.PRESENDSNSUM + "=? and "
									+ TableLineStart.PRESENDSNSPEED + "=? and "
									+ TableLineStart.ISSN + "=? and "
									+ TableLineStart.MOVESPEED + "=? and "
									+ TableLineStart.PREHEATTIME
									+ "=?",
							new String[] {
									String.valueOf(pointWeldLineStartParam
											.getSnSpeed()),
									String.valueOf(pointWeldLineStartParam
											.getPreSendSnSum()),
									String.valueOf(pointWeldLineStartParam
											.getPreSendSnSpeed()),
									String.valueOf(pointWeldLineStartParam
											.isSn() ? 1 : 0),
									String.valueOf(pointWeldLineStartParam
											.getMoveSpeed()),
									String.valueOf(pointWeldLineStartParam
											.getPreHeatTime())}, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					id = cursor.getInt(cursor
							.getColumnIndex(TableLineStart._ID));
				}
			}
			db.close();

		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
		}
		return id;

	}

	/**
	 * @Title deleteParam
	 * @Description 删除某一行数据
	 * @author wj
	 * @param pointWeldLineStartParam
	 * @return
	 */
	public int deleteParam(PointWeldLineStartParam pointWeldLineStartParam,String taskname) {
		db = dbHelper.getWritableDatabase();
		int rowID = db
				.delete(TableLineStart.LINE_START_TABLE+taskname,
						TableLineStart._ID + "=?", new String[] { String
								.valueOf(pointWeldLineStartParam.get_id()) });

		db.close();
		return rowID;
	}

}
