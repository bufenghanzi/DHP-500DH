/**
 * 
 */
package com.mingseal.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingseal.data.db.DBHelper;
import com.mingseal.data.db.DBInfo.TableLineMid;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjian
 *
 */
public class WeldLineMidDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;
	String[] columns = { TableLineMid._ID, TableLineMid.MOVESPEED, TableLineMid.SENDSNSPEED,
			TableLineMid.STOPSNTIME, TableLineMid.ISSN };

	public WeldLineMidDao(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * @Title  upDateGlueAlone
	 * @Description 更新一条独立点数据
	 * @author wj
	 * @param
	 * @return  影响的行数，0表示错误
	 */
	public int upDateWeldLineMid(PointWeldLineMidParam pointWeldLineMidParam){
		int rowid = 0;
		try {
			db = dbHelper.getWritableDatabase();
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableLineMid.MOVESPEED, pointWeldLineMidParam.getMoveSpeed());
			values.put(TableLineMid.SENDSNSPEED, pointWeldLineMidParam.getSendSnSpeed());
			values.put(TableLineMid.STOPSNTIME, pointWeldLineMidParam.getStopSnTime());
			values.put(TableLineMid.ISSN, (boolean) pointWeldLineMidParam.isSn() ? 1 : 0);
			rowid = db.update(TableLineMid.LINE_MID_TABLE, values, TableLineMid._ID +"=?", new String[]{String.valueOf(pointWeldLineMidParam.get_id())});
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
	 * 增加一条线中间点的数据
	 *
	 * @param pointWeldLineMidParam
	 * @return 刚增加的这条数据的主键
	 */
	public long insertWeldLineMid(PointWeldLineMidParam pointWeldLineMidParam) {
		long rowID = 0;
		db = dbHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableLineMid._ID, pointWeldLineMidParam.get_id());
			values.put(TableLineMid.MOVESPEED, pointWeldLineMidParam.getMoveSpeed());
			values.put(TableLineMid.SENDSNSPEED, pointWeldLineMidParam.getSendSnSpeed());
			values.put(TableLineMid.STOPSNTIME, pointWeldLineMidParam.getStopSnTime());
			values.put(TableLineMid.ISSN, (boolean) pointWeldLineMidParam.isSn() ? 1 : 0);
			rowID = db.insert(TableLineMid.LINE_MID_TABLE, TableLineMid._ID, values);

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
	 * 取得所有线中间点的数据
	 *
	 * @return
	 */
	public List<PointWeldLineMidParam> findAllWeldLineMidParams() {
		db = dbHelper.getReadableDatabase();
		List<PointWeldLineMidParam> midLists = null;
		PointWeldLineMidParam mid = null;

		Cursor cursor = null;
		try {
			cursor = db.query(TableLineMid.LINE_MID_TABLE, columns, null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
                midLists = new ArrayList<PointWeldLineMidParam>();
                while (cursor.moveToNext()) {
                    mid = new PointWeldLineMidParam();

                    mid.set_id(cursor.getInt(cursor.getColumnIndex(TableLineMid._ID)));
                    mid.setMoveSpeed(cursor.getInt(cursor.getColumnIndex(TableLineMid.MOVESPEED)));
                    mid.setSendSnSpeed(cursor.getInt(cursor.getColumnIndex(TableLineMid.SENDSNSPEED)));
                    mid.setStopSnTime(cursor.getInt(cursor.getColumnIndex(TableLineMid.STOPSNTIME)));
                    mid.setSn(cursor.getInt(cursor.getColumnIndex(TableLineMid.ISSN)) == 0 ? false : true);
                    midLists.add(mid);
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

		return midLists;
	}

	/**
	 * 通过主键找到PointWeldLineMidParam参数
	 *
	 * @param id
	 * @return PointWeldLineMidParam
	 */
	public PointWeldLineMidParam getPointWeldLineMidParam(int id) {
		PointWeldLineMidParam param = new PointWeldLineMidParam();
		db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TableLineMid.LINE_MID_TABLE, columns, TableLineMid._ID + "=?",
					new String[] { String.valueOf(id) }, null, null, null);
			db.beginTransaction();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					param.set_id(cursor.getInt(cursor.getColumnIndex(TableLineMid._ID)));
					param.setMoveSpeed(cursor.getInt(cursor.getColumnIndex(TableLineMid.MOVESPEED)));
					param.setSendSnSpeed(cursor.getInt(cursor.getColumnIndex(TableLineMid.SENDSNSPEED)));
					param.setStopSnTime(cursor.getInt(cursor.getColumnIndex(TableLineMid.STOPSNTIME)));
					param.setSn(cursor.getInt(cursor.getColumnIndex(TableLineMid.ISSN)) == 0 ? false : true);
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
	 * 通过List<Integer> 列表来查找对应的 PointWeldLineMidParam集合
	 *
	 * @param ids
	 * @return List<PointGlueLineMidParam>
	 */
	public List<PointWeldLineMidParam> getPointWeldLineMidParamsByIDs(List<Integer> ids) {
		db = dbHelper.getReadableDatabase();
		List<PointWeldLineMidParam> params = new ArrayList<>();
		PointWeldLineMidParam param = null;
		try {
			db.beginTransaction();
			for (Integer id : ids) {
				Cursor cursor = db.query(TableLineMid.LINE_MID_TABLE, columns, TableLineMid._ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						param = new PointWeldLineMidParam();
						param.set_id(cursor.getInt(cursor.getColumnIndex(TableLineMid._ID)));
						param.setMoveSpeed(cursor.getInt(cursor.getColumnIndex(TableLineMid.MOVESPEED)));
						param.setSendSnSpeed(cursor.getInt(cursor.getColumnIndex(TableLineMid.SENDSNSPEED)));
						param.setStopSnTime(cursor.getInt(cursor.getColumnIndex(TableLineMid.STOPSNTIME)));
						param.setSn(cursor.getInt(cursor.getColumnIndex(TableLineMid.ISSN)) == 0 ? false : true);
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
	 * 通过参数方案找到当前方案的主键(是否出胶是没有数据的)
	 *
	 * @param pointWeldLineMidParam
	 * @return 当前方案的主键
	 */
	public int getLineMidParamIDByParam(PointWeldLineMidParam pointWeldLineMidParam) {
		int id = -1;
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TableLineMid.LINE_MID_TABLE, columns,
				TableLineMid.MOVESPEED + "=? and " + TableLineMid.SENDSNSPEED + "=? and " + TableLineMid.STOPSNTIME
						+ "=? and " + TableLineMid.ISSN + "=?",
				new String[] { String.valueOf(pointWeldLineMidParam.getMoveSpeed()),
						String.valueOf(pointWeldLineMidParam.getSendSnSpeed()),
						String.valueOf(pointWeldLineMidParam.getStopSnTime()),
						String.valueOf(pointWeldLineMidParam.isSn() ? 1 : 0) },
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex(TableLineMid._ID));
			}
		}
		db.close();
		if (-1 == id) {
			db = dbHelper.getReadableDatabase();
			cursor = db.query(TableLineMid.LINE_MID_TABLE, columns,
					TableLineMid.MOVESPEED + "=? and " + TableLineMid.SENDSNSPEED + "=? and " + TableLineMid.STOPSNTIME
							+ "=? and " + TableLineMid.ISSN + "=?",
					new String[] { String.valueOf(pointWeldLineMidParam.getMoveSpeed()),
							String.valueOf(pointWeldLineMidParam.getSendSnSpeed()),
							String.valueOf(pointWeldLineMidParam.getStopSnTime()),
							String.valueOf(pointWeldLineMidParam.isSn() ? 1 : 0) },
					null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					id = cursor.getInt(cursor.getColumnIndex(TableLineMid._ID));
				}
			}
			db.close();
			if (-1 == id) {
				id = (int) insertWeldLineMid(pointWeldLineMidParam);
			}
		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
		}
		return id;

	}

	/**
	 * @Title  deleteParam
	 * @Description
	 * @author wj
	 * @param pointWeldLineMidParam
	 * @return
	 */
	public int deleteParam(PointWeldLineMidParam pointWeldLineMidParam) {
		db = dbHelper.getWritableDatabase();
		int rowID = db.delete(TableLineMid.LINE_MID_TABLE, TableLineMid._ID + "=?",
				new String[] { String.valueOf(pointWeldLineMidParam.get_id()) });
		db.close();
		return rowID;
	}

}
