/**
 * 
 */
package com.mingseal.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingseal.data.db.DBHelper;
import com.mingseal.data.db.DBInfo.TableInputIO;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.utils.ArraysComprehension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangjian
 *
 */
public class WeldInputDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;

	String[] columns = { TableInputIO._ID, TableInputIO.GO_TIME_PREV, TableInputIO.GO_TIME_NEXT,
			TableInputIO.INPUT_PORT };

	public WeldInputDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	/**
	 * @Title  upDateGlueLineMid
	 * @Description 更新一条独立点数据
	 * @author wj
	 * @param
	 * @return  影响的行数，0表示错误
	 */
	public int upDateGlueInput(PointWeldInputIOParam param){
		int rowid = 0;
		try {
			db = dbHelper.getWritableDatabase();
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableInputIO.GO_TIME_PREV, param.getGoTimePrev());
			values.put(TableInputIO.GO_TIME_NEXT, param.getGoTimeNext());
			values.put(TableInputIO.INPUT_PORT, Arrays.toString(param.getInputPort()));
			rowid = db.update(TableInputIO.INPUT_IO_TABLE, values, TableInputIO._ID +"=?", new String[]{String.valueOf(param.get_id())});
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
	 * 增加一条输入IO的数据
	 *
	 * @param param
	 * @return 刚增加的这条数据的主键
	 */
	public long insertWeldInput(PointWeldInputIOParam param) {
		long rowID = 0;
		db = dbHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			values = new ContentValues();
			values.put(TableInputIO._ID, param.get_id());
			values.put(TableInputIO.GO_TIME_PREV, param.getGoTimePrev());
			values.put(TableInputIO.GO_TIME_NEXT, param.getGoTimeNext());
			values.put(TableInputIO.INPUT_PORT, Arrays.toString(param.getInputPort()));
			rowID = db.insert(TableInputIO.INPUT_IO_TABLE, TableInputIO._ID, values);
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
	 * 取得所有输入IO的数据
	 *
	 * @return List<PoinWeldBlowParam>
	 */
	public List<PointWeldInputIOParam> findAllWeldInputParams() {
		db = dbHelper.getReadableDatabase();
		List<PointWeldInputIOParam> outputIOParams = null;
		PointWeldInputIOParam output = null;

		Cursor cursor = null;
		try {
			cursor = db.query(TableInputIO.INPUT_IO_TABLE, columns, null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
                outputIOParams = new ArrayList<PointWeldInputIOParam>();
                while (cursor.moveToNext()) {
                    output = new PointWeldInputIOParam();
                    output.set_id(cursor.getInt(cursor.getColumnIndex(TableInputIO._ID)));
                    output.setGoTimePrev(cursor.getInt(cursor.getColumnIndex(TableInputIO.GO_TIME_PREV)));
                    output.setGoTimeNext(cursor.getInt(cursor.getColumnIndex(TableInputIO.GO_TIME_NEXT)));
                    output.setInputPort(ArraysComprehension
                            .boooleanParse(cursor.getString(cursor.getColumnIndex(TableInputIO.INPUT_PORT))));

                    outputIOParams.add(output);
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
		return outputIOParams;
	}

	/**
	 * 通过List<Integer> 列表来查找对应的PointGlueInputIOParam集合
	 *
	 * @param ids
	 * @return List<PoinWeldBlowParam>
	 */
	public List<PointWeldInputIOParam> getWeldInputParamsByIDs(List<Integer> ids) {
		db = dbHelper.getReadableDatabase();
		List<PointWeldInputIOParam> params = new ArrayList<>();
		PointWeldInputIOParam param = null;
		Cursor cursor = null;
		try {
			db.beginTransaction();
			for (Integer id : ids) {
				cursor = db.query(TableInputIO.INPUT_IO_TABLE, columns, TableInputIO._ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						param = new PointWeldInputIOParam();
						param.set_id(cursor.getInt(cursor.getColumnIndex(TableInputIO._ID)));
						param.setGoTimePrev(cursor.getInt(cursor.getColumnIndex(TableInputIO.GO_TIME_PREV)));
						param.setGoTimeNext(cursor.getInt(cursor.getColumnIndex(TableInputIO.GO_TIME_NEXT)));
						param.setInputPort(ArraysComprehension
								.boooleanParse(cursor.getString(cursor.getColumnIndex(TableInputIO.INPUT_PORT))));

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
	 * 通过参数方案寻找到当前方案的主键
	 *
	 * @param pointWeldInputIOParam
	 * @return 当前方案的主键
	 */
	public int getInputParamIDByParam(PointWeldInputIOParam pointWeldInputIOParam) {
		int id = -1;
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TableInputIO.INPUT_IO_TABLE, columns,
				TableInputIO.GO_TIME_PREV + "=? and " + TableInputIO.GO_TIME_NEXT + "=? and " + TableInputIO.INPUT_PORT
						+ "=?",
				new String[] { String.valueOf(pointWeldInputIOParam.getGoTimePrev()),
						String.valueOf(pointWeldInputIOParam.getGoTimeNext()),
						Arrays.toString(pointWeldInputIOParam.getInputPort()) },
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex(TableInputIO._ID));
			}
		}
		db.close();
		if (-1 == id) {
			id = (int) insertWeldInput(pointWeldInputIOParam);
		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
		}
		return id;
	}

	/**
	 * @Title getInputPointByID
	 * @Description 通过主键寻找到当前输入口的参数方案
	 * @param id
	 *            当前输入口主键
	 * @return PointGlueInputIOParam
	 */
	public PointWeldInputIOParam getInputPointByID(int id) {
		db = dbHelper.getReadableDatabase();
		PointWeldInputIOParam param = null;
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.query(TableInputIO.INPUT_IO_TABLE, columns, TableInputIO._ID + "=?",
					new String[] { String.valueOf(id) }, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					param = new PointWeldInputIOParam();
					param.set_id(cursor.getInt(cursor.getColumnIndex(TableInputIO._ID)));
					param.setGoTimePrev(cursor.getInt(cursor.getColumnIndex(TableInputIO.GO_TIME_PREV)));
					param.setGoTimeNext(cursor.getInt(cursor.getColumnIndex(TableInputIO.GO_TIME_NEXT)));
					param.setInputPort(ArraysComprehension
							.boooleanParse(cursor.getString(cursor.getColumnIndex(TableInputIO.INPUT_PORT))));

				}
			}
			if (cursor!=null){
				cursor.close();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}

		return param;
	}

	public int deleteParam(PointWeldInputIOParam pointWeldInputIOParam) {
		db = dbHelper.getWritableDatabase();
		int rowID = db.delete(TableInputIO.INPUT_IO_TABLE, TableInputIO._ID + "=?",
				new String[] { String.valueOf(pointWeldInputIOParam.get_id()) });

		db.close();
		return rowID;
	}
}
