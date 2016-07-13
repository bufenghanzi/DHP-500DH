/**
 * 
 */
package com.mingseal.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingseal.data.db.DBHelper;
import com.mingseal.data.db.DBInfo.TableLineEnd;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjian
 *
 */
public class WeldLineEndDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;
	String[] columns = { TableLineEnd._ID, TableLineEnd.STOPSNTIME, TableLineEnd.UPHEIGHT,
			TableLineEnd.ISPAUSE };

	public WeldLineEndDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	/**
	 * @Title  upDateGlueAlone
	 * @Description 更新一条独立点数据
	 * @author wj
	 * @param
	 * @return  影响的行数，0表示错误
	 */
	public int upDateWeldLineEnd(PointWeldLineEndParam pointWeldLineEndParam){
		int rowid = 0;
		try {
			db = dbHelper.getWritableDatabase();
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableLineEnd.STOPSNTIME, pointWeldLineEndParam.getStopSnTime());
			values.put(TableLineEnd.UPHEIGHT, pointWeldLineEndParam.getUpHeight());
			values.put(TableLineEnd.ISPAUSE, (boolean) pointWeldLineEndParam.isPause() ? 1 : 0);
			rowid = db.update(TableLineEnd.LINE_END_TABLE, values, TableLineEnd._ID +"=?", new String[]{String.valueOf(pointWeldLineEndParam.get_id())});
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
	 * 增加一条线结束点的数据
	 *
	 * @param pointWeldLineEndParam
	 * @return pointWeldLineEndParam
	 */
	public long insertWeldLineEnd(PointWeldLineEndParam pointWeldLineEndParam) {
		long rowID = 0;
		db = dbHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableLineEnd._ID, pointWeldLineEndParam.get_id());
			values.put(TableLineEnd.STOPSNTIME, pointWeldLineEndParam.getStopSnTime());
			values.put(TableLineEnd.UPHEIGHT, pointWeldLineEndParam.getUpHeight());
			values.put(TableLineEnd.ISPAUSE, (boolean) pointWeldLineEndParam.isPause() ? 1 : 0);
			rowID = db.insert(TableLineEnd.LINE_END_TABLE, TableLineEnd._ID, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			// 释放资源
			db.close();
		}
		return rowID;

	}

	/**
	 * 取得所有线结束点的数据
	 *
	 * @return List<PointWeldLineEndParam>
	 */
	public List<PointWeldLineEndParam> findAllWeldLineEndParams() {
		db = dbHelper.getReadableDatabase();
		List<PointWeldLineEndParam> endLists = null;
		PointWeldLineEndParam end = null;

		Cursor cursor = null;
		try {
			cursor = db.query(TableLineEnd.LINE_END_TABLE, columns, null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
                endLists = new ArrayList<PointWeldLineEndParam>();
                while (cursor.moveToNext()) {
                    end = new PointWeldLineEndParam();
                    end.set_id(cursor.getInt(cursor.getColumnIndex(TableLineEnd._ID)));
                    end.setStopSnTime(cursor.getInt(cursor.getColumnIndex(TableLineEnd.STOPSNTIME)));
                    end.setUpHeight(cursor.getInt(cursor.getColumnIndex(TableLineEnd.UPHEIGHT)));
					end.setPause(cursor.getInt(cursor.getColumnIndex(TableLineEnd.ISPAUSE)) == 0 ? false : true);
                    endLists.add(end);

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

		return endLists;
	}

	/**
	 * 通过id得到PointWeldLineEndParam参数
	 *
	 * @param id
	 * @return PointWeldLineEndParam
	 */
	public PointWeldLineEndParam getPointWeldLineEndParamByID(int id) {
		PointWeldLineEndParam param = new PointWeldLineEndParam();
		db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TableLineEnd.LINE_END_TABLE, columns, TableLineEnd._ID + "=?",
					new String[] { String.valueOf(id) }, null, null, null);
			db.beginTransaction();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					param.set_id(cursor.getInt(cursor.getColumnIndex(TableLineEnd._ID)));
					param.setStopSnTime(cursor.getInt(cursor.getColumnIndex(TableLineEnd.STOPSNTIME)));
					param.setUpHeight(cursor.getInt(cursor.getColumnIndex(TableLineEnd.UPHEIGHT)));
					param.setPause(cursor.getInt(cursor.getColumnIndex(TableLineEnd.ISPAUSE)) == 0 ? false : true);

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
	 * 通过List<Integer> 列表来查找对应的 PointWeldLineEndParam集合
	 *
	 * @param ids
	 * @return List<PointWeldLineEndParam>
	 */
	public List<PointWeldLineEndParam> getPointWeldLineEndParamsByIDs(List<Integer> ids) {
		db = dbHelper.getReadableDatabase();
		List<PointWeldLineEndParam> params = new ArrayList<>();
		PointWeldLineEndParam param = null;
		Cursor cursor = null;
		try {
			db.beginTransaction();
			for (Integer id : ids) {
				cursor = db.query(TableLineEnd.LINE_END_TABLE, columns, TableLineEnd._ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						param = new PointWeldLineEndParam();
						param.set_id(cursor.getInt(cursor.getColumnIndex(TableLineEnd._ID)));
						param.setStopSnTime(cursor.getInt(cursor.getColumnIndex(TableLineEnd.STOPSNTIME)));
						param.setUpHeight(cursor.getInt(cursor.getColumnIndex(TableLineEnd.UPHEIGHT)));
						param.setPause(cursor.getInt(cursor.getColumnIndex(TableLineEnd.ISPAUSE)) == 0 ? false : true);
						params.add(param);
					}
				}
				if (cursor!=null){
					cursor.close();
				}
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
	 * 通过参数方案寻找到当前方案的主键(Messagemgr放进去的停前延时为‘停胶后延时’,所有'停胶前延时'是没有数据的)
	 *
	 * @param pointWeldLineEndParam
	 * @return 当前方案的主键
	 */
	public int getLineEndParamIDByParam(PointWeldLineEndParam pointWeldLineEndParam) {
		int id = -1;
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TableLineEnd.LINE_END_TABLE, columns,
				TableLineEnd.STOPSNTIME + "=? and " + TableLineEnd.UPHEIGHT + "=? and "
						+ TableLineEnd.ISPAUSE + "=? ",
				new String[] { String.valueOf(pointWeldLineEndParam.getStopSnTime()),
						String.valueOf(pointWeldLineEndParam.getUpHeight()),
						String.valueOf(pointWeldLineEndParam.isPause() ? 1 : 0), },
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex(TableLineEnd._ID));
			}
		}
		db.close();
		if (-1 == id) {
			db = dbHelper.getReadableDatabase();
			cursor = db.query(TableLineEnd.LINE_END_TABLE, columns,
					TableLineEnd.STOPSNTIME + "=? and " + TableLineEnd.UPHEIGHT + "=? and "
							+ TableLineEnd.ISPAUSE + "=? ",
					new String[] { String.valueOf(pointWeldLineEndParam.getStopSnTime()),
							String.valueOf(pointWeldLineEndParam.getUpHeight()),
							String.valueOf(pointWeldLineEndParam.isPause() ? 1 : 0), },
					null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					id = cursor.getInt(cursor.getColumnIndex(TableLineEnd._ID));
				}
			}
			db.close();
			if(-1 == id){
				id = (int) insertWeldLineEnd(pointWeldLineEndParam);
			}
		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
		}
		return id;
	}

	public int deleteParam(PointWeldLineEndParam param) {
		db = dbHelper.getWritableDatabase();
		int rowID = db.delete(TableLineEnd.LINE_END_TABLE, TableLineEnd._ID + "=?",
				new String[] { String.valueOf(param.get_id()) });

		db.close();
		return rowID;
	}
}
