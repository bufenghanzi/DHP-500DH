/**
 * 
 */
package com.mingseal.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingseal.data.db.DBHelper;
import com.mingseal.data.db.DBInfo;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjian
 *
 */
public class WeldBlowDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;

	String[] columns = { DBInfo.TableWeldBlow._ID, DBInfo.TableWeldBlow.GO_TIME_PREV, DBInfo.TableWeldBlow.ISSN};

	public WeldBlowDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	/**
	 * @Title  upDateGlueLineMid
	 * @Description 更新一条独立点数据
	 * @author wj
	 * @param
	 * @return  影响的行数，0表示错误
	 */
	public int upDateWeldOutput(PointWeldBlowParam param,String taskname){
		int rowid = 0;
		try {
			db = dbHelper.getWritableDatabase();
			db.beginTransaction();
			values = new ContentValues();
			values.put(DBInfo.TableWeldBlow.GO_TIME_PREV, param.getGoTimePrev());
			values.put(DBInfo.TableWeldBlow.ISSN, (boolean) param.isSn() ? 1 : 0);
			rowid = db.update(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, values, DBInfo.TableWeldBlow._ID +"=?", new String[]{String.valueOf(param.get_id())});
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
	 * 增加一条输出IO的数据
	 *
	 * @param param
	 * @return 刚增加的这条数据的主键
	 */
	public long insertWeldOutput(PointWeldBlowParam param,String taskname) {
		long rowID = 0;
		db = dbHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			values = new ContentValues();
			values.put(DBInfo.TableWeldBlow._ID, param.get_id());
			values.put(DBInfo.TableWeldBlow.GO_TIME_PREV, param.getGoTimePrev());
			values.put(DBInfo.TableWeldBlow.ISSN, (boolean) param.isSn() ? 1 : 0);
			rowID = db.insert(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, DBInfo.TableWeldBlow._ID, values);
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
	 * 取得所有输出IO的数据
	 *
	 * @return List<PointWeldOutputIOParam>
	 */
	public List<PointWeldBlowParam> findAllWeldOutputParams(String taskname) {
		db = dbHelper.getReadableDatabase();

		List<PointWeldBlowParam> outputIOParams = null;
		PointWeldBlowParam output = null;

		Cursor cursor = null;
		try {
			cursor = db.query(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, columns, null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
                outputIOParams = new ArrayList<PointWeldBlowParam>();
                while (cursor.moveToNext()) {
                    output = new PointWeldBlowParam();
                    output.set_id(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow._ID)));
                    output.setGoTimePrev(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow.GO_TIME_PREV)));
                    output.setSn(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow.ISSN)) != 0);

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
	 * 通过List<Integer> 列表来查找对应的PointGlueOutputIOParam集合
	 *
	 * @param ids
	 * @return List<PointWeldOutputIOParam>
	 */
	public List<PointWeldBlowParam> getWeldOutputIOParamsByIDs(List<Integer> ids,String taskname) {
		db = dbHelper.getReadableDatabase();
		List<PointWeldBlowParam> params = new ArrayList<>();
		PointWeldBlowParam param = null;
		Cursor cursor = null;
		try {
			db.beginTransaction();
			for (Integer id : ids) {
				cursor = db.query(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, columns, DBInfo.TableWeldBlow._ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						param = new PointWeldBlowParam();
						param.set_id(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow._ID)));
						param.setGoTimePrev(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow.GO_TIME_PREV)));
						param.setSn(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow.ISSN)) != 0);

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
	 * 通过参数寻找到当前方案的主键
	 *
	 * @param pointWeldOutputIOParam
	 * @return 当前方案的主键
	 */
	public int getOutputParamIDByParam(PointWeldBlowParam pointWeldOutputIOParam,String taskname) {
		int id = -1;
		db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, columns,
					DBInfo.TableWeldBlow.GO_TIME_PREV + "=? and " + DBInfo.TableWeldBlow.ISSN + "=?",
                    new String[] { String.valueOf(pointWeldOutputIOParam.getGoTimePrev()),
							String.valueOf(pointWeldOutputIOParam.isSn() ? 1 : 0) },
                    null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    id = cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow._ID));
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
		return id;
	}

	/**
	 * @Title getOutPutPointByID
	 * @Description 通过主键寻找到当前输出口的参数方案
	 * @param id
	 *            主键
	 * @return PointWeldOutputIOParam
	 */
	public PointWeldBlowParam getOutPutPointByID(int id,String taskname) {
		db = dbHelper.getReadableDatabase();
		PointWeldBlowParam param = null;
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.query(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, columns, DBInfo.TableWeldBlow._ID + "=?",
					new String[] { String.valueOf(id) }, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					param = new PointWeldBlowParam();
					param.set_id(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow._ID)));
					param.setGoTimePrev(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow.GO_TIME_PREV)));
					param.setSn(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWeldBlow.ISSN)) != 0);

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

	public int deleteParam(PointWeldBlowParam pointWeldOutputIOParam,String taskname) {
		db = dbHelper.getWritableDatabase();
		int rowID = db.delete(DBInfo.TableWeldBlow.WELD_BLOW_TABLE+taskname, DBInfo.TableWeldBlow._ID + "=?",
				new String[] { String.valueOf(pointWeldOutputIOParam.get_id()) });

		db.close();
		return rowID;
	}

}
