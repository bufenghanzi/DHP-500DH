package com.mingseal.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingseal.data.db.DBHelper;
import com.mingseal.data.db.DBInfo;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjian
 */
public class WeldWorkDao {
    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private ContentValues values = null;
    String[] columns = {DBInfo.TableWork._ID, DBInfo.TableWork.PREHEATTIME, DBInfo.TableWork.SENDSNSPEEDFIR, DBInfo.TableWork.SENDSNSUMFIR, DBInfo.TableWork.SENDSNSPEEDSEC, DBInfo.TableWork.SENDSNSUMSEC, DBInfo.TableWork.STOPSNSTIMESEC, DBInfo.TableWork.SENDSNSPEEDTHIRD, DBInfo.TableWork.SENDSNSUMTHIRD, DBInfo.TableWork.STOPSNTIMETHIRD, DBInfo.TableWork.SENDSNSPEEDFOURTH
            , DBInfo.TableWork.SENDSNSUMFOURTH, DBInfo.TableWork.STOPSNTIMEFOURTH, DBInfo.TableWork.DIPDISTANCE, DBInfo.TableWork.UPHEIGHT, DBInfo.TableWork.ISSN, DBInfo.TableWork.ISPAUSE
            , DBInfo.TableWork.DIPDISTANCE_ANGLE, DBInfo.TableWork.ISSUS};

    public WeldWorkDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * @param pointWeldWorkParam
     * @return 影响的行数，0表示错误
     * @Title upDateGlueAlone
     * @Description 更新一条独立点数据
     * @author wj
     */
    public int upDateGlueAlone(PointWeldWorkParam pointWeldWorkParam,String taskname) {
        int rowid = 0;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            values = new ContentValues();
            values.put(DBInfo.TableWork.PREHEATTIME, pointWeldWorkParam.getPreHeatTime());
            values.put(DBInfo.TableWork.SENDSNSPEEDFIR, pointWeldWorkParam.getSendSnSpeedFir());
            values.put(DBInfo.TableWork.SENDSNSUMFIR, pointWeldWorkParam.getSendSnSumFir());
            values.put(DBInfo.TableWork.SENDSNSPEEDSEC, pointWeldWorkParam.getSendSnSpeedSec());
            values.put(DBInfo.TableWork.SENDSNSUMSEC, pointWeldWorkParam.getSendSnSumSec());
            values.put(DBInfo.TableWork.STOPSNSTIMESEC, pointWeldWorkParam.getStopSnStimeSec());
            values.put(DBInfo.TableWork.SENDSNSPEEDTHIRD, pointWeldWorkParam.getSendSnSpeedThird());
            values.put(DBInfo.TableWork.SENDSNSUMTHIRD, pointWeldWorkParam.getSendSnSumThird());
            values.put(DBInfo.TableWork.STOPSNTIMETHIRD, pointWeldWorkParam.getStopSnTimeThird());
            values.put(DBInfo.TableWork.SENDSNSPEEDFOURTH, pointWeldWorkParam.getSendSnSpeedFourth());
            values.put(DBInfo.TableWork.SENDSNSUMFOURTH, pointWeldWorkParam.getSendSnSumFourth());
            values.put(DBInfo.TableWork.STOPSNTIMEFOURTH, pointWeldWorkParam.getStopSnTimeFourth());
            values.put(DBInfo.TableWork.DIPDISTANCE, pointWeldWorkParam.getDipDistance());
            values.put(DBInfo.TableWork.UPHEIGHT, pointWeldWorkParam.getUpHeight());
            values.put(DBInfo.TableWork.ISSN,  pointWeldWorkParam.isSn() ? 1 : 0);
            values.put(DBInfo.TableWork.ISPAUSE,  pointWeldWorkParam.isPause() ? 1 : 0);
            values.put(DBInfo.TableWork.DIPDISTANCE_ANGLE,  pointWeldWorkParam.getDipDistance_angle());
            values.put(DBInfo.TableWork.ISSUS,  pointWeldWorkParam.isSus() ? 1 : 0);
            rowid = db.update(DBInfo.TableWork.WORK_TABLE+taskname, values, DBInfo.TableWork._ID + "=?", new String[]{String.valueOf(pointWeldWorkParam.get_id())});
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
     * 增加一条独立点数据
     *
     * @param pointWeldWorkParam
     * @return
     */
    public long insertWeldWork(PointWeldWorkParam pointWeldWorkParam,String taskname) {
        long rowID = 0;
        db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            values = new ContentValues();
            values.put(DBInfo.TableWork._ID, pointWeldWorkParam.get_id());
            values.put(DBInfo.TableWork.PREHEATTIME, pointWeldWorkParam.getPreHeatTime());
            values.put(DBInfo.TableWork.SENDSNSPEEDFIR, pointWeldWorkParam.getSendSnSpeedFir());
            values.put(DBInfo.TableWork.SENDSNSUMFIR, pointWeldWorkParam.getSendSnSumFir());
            values.put(DBInfo.TableWork.SENDSNSPEEDSEC, pointWeldWorkParam.getSendSnSpeedSec());
            values.put(DBInfo.TableWork.SENDSNSUMSEC, pointWeldWorkParam.getSendSnSumSec());
            values.put(DBInfo.TableWork.STOPSNSTIMESEC, pointWeldWorkParam.getStopSnStimeSec());
            values.put(DBInfo.TableWork.SENDSNSPEEDTHIRD, pointWeldWorkParam.getSendSnSpeedThird());
            values.put(DBInfo.TableWork.SENDSNSUMTHIRD, pointWeldWorkParam.getSendSnSumThird());
            values.put(DBInfo.TableWork.STOPSNTIMETHIRD, pointWeldWorkParam.getStopSnTimeThird());
            values.put(DBInfo.TableWork.SENDSNSPEEDFOURTH, pointWeldWorkParam.getSendSnSpeedFourth());
            values.put(DBInfo.TableWork.SENDSNSUMFOURTH, pointWeldWorkParam.getSendSnSumFourth());
            values.put(DBInfo.TableWork.STOPSNTIMEFOURTH, pointWeldWorkParam.getStopSnTimeFourth());
            values.put(DBInfo.TableWork.DIPDISTANCE, pointWeldWorkParam.getDipDistance());
            values.put(DBInfo.TableWork.UPHEIGHT, pointWeldWorkParam.getUpHeight());
            values.put(DBInfo.TableWork.ISSN,  pointWeldWorkParam.isSn() ? 1 : 0);
            values.put(DBInfo.TableWork.ISPAUSE,  pointWeldWorkParam.isPause() ? 1 : 0);
            values.put(DBInfo.TableWork.DIPDISTANCE_ANGLE,  pointWeldWorkParam.getDipDistance_angle());
            values.put(DBInfo.TableWork.ISSUS,  pointWeldWorkParam.isSus() ? 1 : 0);
            rowID = db.insert(DBInfo.TableWork.WORK_TABLE+taskname, null, values);
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
     * 获得所有独立点的数据
     *
     * @return
     */
    public List<PointWeldWorkParam> findAllWeldWorkParams(String taskname) {
        db = dbHelper.getReadableDatabase();
        List<PointWeldWorkParam> aloneLists = null;
        PointWeldWorkParam alone = null;
        Cursor cursor = null;
        try {
            cursor = db.query(DBInfo.TableWork.WORK_TABLE+taskname, columns, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                aloneLists = new ArrayList<PointWeldWorkParam>();
                while (cursor.moveToNext()) {
                    alone = new PointWeldWorkParam();
                    alone.set_id(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork._ID)));
                    alone.setPreHeatTime(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.PREHEATTIME)));
                    alone.setSendSnSpeedFir(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDFIR)));
                    alone.setSendSnSumFir(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMFIR)));
                    alone.setSendSnSpeedSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDSEC)));
                    alone.setSendSnSumSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMSEC)));
                    alone.setStopSnStimeSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNSTIMESEC)));
                    alone.setSendSnSpeedThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDTHIRD)));
                    alone.setSendSnSumThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMTHIRD)));
                    alone.setStopSnTimeThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNTIMETHIRD)));
                    alone.setSendSnSpeedFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDFOURTH)));
                    alone.setSendSnSumFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMFOURTH)));
                    alone.setStopSnTimeFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNTIMEFOURTH)));
                    alone.setDipDistance(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.DIPDISTANCE)));
                    alone.setUpHeight(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.UPHEIGHT)));
                    alone.setSn(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISSN)) == 0 ? false : true);
                    alone.setPause(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISPAUSE)) == 0 ? false : true);
                    alone.setDipDistance_angle(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.DIPDISTANCE_ANGLE)));
                    alone.setSus(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISSUS)) == 0 ? false : true);
                    aloneLists.add(alone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return aloneLists;
    }

    /**
     * 删除某一行数据
     *
     * @param pointWeldWorkParam
     * @return 1为成功删除，0为未成功删除
     */
    public Integer deleteGlueAlone(PointWeldWorkParam pointWeldWorkParam,String taskname) {
        db = dbHelper.getWritableDatabase();
        int rowID = db.delete(DBInfo.TableWork.WORK_TABLE+taskname, DBInfo.TableWork._ID + "=?",
                new String[]{String.valueOf(pointWeldWorkParam.get_id())});

        db.close();
        return rowID;
    }

    /**
     * 通过id找到独立点的参数
     *
     * @param id 主键
     * @return PointGlueAloneParam
     */
    public PointWeldWorkParam getPointWeldWorkParamById(int id,String taskname) {
        PointWeldWorkParam param = new PointWeldWorkParam();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBInfo.TableWork.WORK_TABLE+taskname, columns, DBInfo.TableWork._ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            db.beginTransaction();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    param.set_id(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork._ID)));
                    param.setPreHeatTime(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.PREHEATTIME)));
                    param.setSendSnSpeedFir(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDFIR)));
                    param.setSendSnSumFir(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMFIR)));
                    param.setSendSnSpeedSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDSEC)));
                    param.setSendSnSumSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMSEC)));
                    param.setStopSnStimeSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNSTIMESEC)));
                    param.setSendSnSpeedThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDTHIRD)));
                    param.setSendSnSumThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMTHIRD)));
                    param.setStopSnTimeThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNTIMETHIRD)));
                    param.setSendSnSpeedFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDFOURTH)));
                    param.setSendSnSumFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMFOURTH)));
                    param.setStopSnTimeFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNTIMEFOURTH)));
                    param.setDipDistance(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.DIPDISTANCE)));
                    param.setUpHeight(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.UPHEIGHT)));
                    param.setSn(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISSN)) == 0 ? false : true);
                    param.setPause(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISPAUSE)) == 0 ? false : true);
                    param.setDipDistance_angle(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.DIPDISTANCE_ANGLE)));
                    param.setSus(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISSUS)) == 0 ? false : true);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
            db.close();
        }
        return param;
    }

    /**
     * 通过List<Integer> 列表来查找对应的PointGlueAloneParam集合
     *
     * @param ids
     * @return List<PointWeldWorkParam>
     */
    public List<PointWeldWorkParam> getWeldWorkParamsByIDs(List<Integer> ids,String taskname) {
        db = dbHelper.getReadableDatabase();
        List<PointWeldWorkParam> params = new ArrayList<>();
        PointWeldWorkParam param = null;
        Cursor cursor = null;
        try {
            db.beginTransaction();
            for (Integer id : ids) {
                cursor = db.query(DBInfo.TableWork.WORK_TABLE+taskname, columns, DBInfo.TableWork._ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        param = new PointWeldWorkParam();
                        param.set_id(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork._ID)));
                        param.setPreHeatTime(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.PREHEATTIME)));
                        param.setSendSnSpeedFir(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDFIR)));
                        param.setSendSnSumFir(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMFIR)));
                        param.setSendSnSpeedSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDSEC)));
                        param.setSendSnSumSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMSEC)));
                        param.setStopSnStimeSec(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNSTIMESEC)));
                        param.setSendSnSpeedThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDTHIRD)));
                        param.setSendSnSumThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMTHIRD)));
                        param.setStopSnTimeThird(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNTIMETHIRD)));
                        param.setSendSnSpeedFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSPEEDFOURTH)));
                        param.setSendSnSumFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.SENDSNSUMFOURTH)));
                        param.setStopSnTimeFourth(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.STOPSNTIMEFOURTH)));
                        param.setDipDistance(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.DIPDISTANCE)));
                        param.setUpHeight(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.UPHEIGHT)));
                        param.setSn(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISSN)) == 0 ? false : true);
                        param.setPause(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISPAUSE)) == 0 ? false : true);
                        param.setDipDistance_angle(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.DIPDISTANCE_ANGLE)));
                        param.setSus(cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork.ISSUS)) == 0 ? false : true);
                        params.add(param);
                    }
                }
                if (cursor != null) {
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
     * 通过参数找到当前参数方案的主键(是否出胶是没有数据的)
     *
     * @param pointWeldWorkParam
     * @return 当前方案的主键
     */
    public int getAloneParamIdByParam(PointWeldWorkParam pointWeldWorkParam,String taskname) {
        int id = -1;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.TableWork.WORK_TABLE+taskname, columns,
                DBInfo.TableWork.PREHEATTIME + "=? and " + DBInfo.TableWork.SENDSNSPEEDFIR + "=? and " + DBInfo.TableWork.SENDSNSUMFIR
                        + "=? and " + DBInfo.TableWork.SENDSNSPEEDSEC + "=? and " + DBInfo.TableWork.SENDSNSUMSEC + "=? and "
                        + DBInfo.TableWork.STOPSNSTIMESEC + "=? and " + DBInfo.TableWork.SENDSNSPEEDTHIRD + "=? and " + DBInfo.TableWork.SENDSNSUMTHIRD + "=? and " + DBInfo.TableWork.STOPSNTIMETHIRD + "=? and"+ DBInfo.TableWork.SENDSNSPEEDFOURTH + "=? and"
                        + DBInfo.TableWork.SENDSNSUMFOURTH + "=? and"+ DBInfo.TableWork.STOPSNTIMEFOURTH + "=? and"+ DBInfo.TableWork.DIPDISTANCE + "=? and"+ DBInfo.TableWork.UPHEIGHT + "=? and"+ DBInfo.TableWork.ISSN + "=? and"
                        + DBInfo.TableWork.ISPAUSE + "=? and"+ DBInfo.TableWork.DIPDISTANCE_ANGLE + "=? and"+ DBInfo.TableWork.ISSUS + "=?",
                new String[]{String.valueOf(pointWeldWorkParam.getPreHeatTime()),
                        String.valueOf(pointWeldWorkParam.getSendSnSpeedFir()),
                        String.valueOf(pointWeldWorkParam.getSendSnSumFir()),
                        String.valueOf(pointWeldWorkParam.getSendSnSpeedSec()),
                        String.valueOf(pointWeldWorkParam.getSendSnSumSec()),
                        String.valueOf(pointWeldWorkParam.getStopSnStimeSec()),
                        String.valueOf(pointWeldWorkParam.getSendSnSpeedThird()),
                        String.valueOf(pointWeldWorkParam.getSendSnSumThird()),
                        String.valueOf(pointWeldWorkParam.getStopSnTimeThird()),
                        String.valueOf(pointWeldWorkParam.getSendSnSpeedFourth()),
                        String.valueOf(pointWeldWorkParam.getSendSnSumFourth()),
                        String.valueOf(pointWeldWorkParam.getStopSnTimeFourth()),
                        String.valueOf(pointWeldWorkParam.getDipDistance()),
                        String.valueOf(pointWeldWorkParam.getUpHeight()),
                        String.valueOf(pointWeldWorkParam.isSn() ? 1 : 0),
                        String.valueOf(pointWeldWorkParam.isPause() ? 1 : 0),
                        String.valueOf(pointWeldWorkParam.getDipDistance_angle()),
                        String.valueOf(pointWeldWorkParam.isSus() ? 1 : 0)},
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork._ID));
            }
        }
        db.close();
        if (-1 == id) {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DBInfo.TableWork.WORK_TABLE+taskname, columns,
                    DBInfo.TableWork.PREHEATTIME + "=? and " + DBInfo.TableWork.SENDSNSPEEDFIR + "=? and " + DBInfo.TableWork.SENDSNSUMFIR
                            + "=? and " + DBInfo.TableWork.SENDSNSPEEDSEC + "=? and " + DBInfo.TableWork.SENDSNSUMSEC + "=? and "
                            + DBInfo.TableWork.STOPSNSTIMESEC + "=? and " + DBInfo.TableWork.SENDSNSPEEDTHIRD + "=? and " + DBInfo.TableWork.SENDSNSUMTHIRD + "=? and " + DBInfo.TableWork.STOPSNTIMETHIRD + "=? and"+ DBInfo.TableWork.SENDSNSPEEDFOURTH + "=? and"
                            + DBInfo.TableWork.SENDSNSUMFOURTH + "=? and"+ DBInfo.TableWork.STOPSNTIMEFOURTH + "=? and"+ DBInfo.TableWork.DIPDISTANCE + "=? and"+ DBInfo.TableWork.UPHEIGHT + "=? and"+ DBInfo.TableWork.ISSN + "=? and"
                            + DBInfo.TableWork.ISPAUSE + "=? and"+ DBInfo.TableWork.DIPDISTANCE_ANGLE + "=? and"+ DBInfo.TableWork.ISSUS + "=?",
                    new String[]{String.valueOf(pointWeldWorkParam.getPreHeatTime()),
                            String.valueOf(pointWeldWorkParam.getSendSnSpeedFir()),
                            String.valueOf(pointWeldWorkParam.getSendSnSumFir()),
                            String.valueOf(pointWeldWorkParam.getSendSnSpeedSec()),
                            String.valueOf(pointWeldWorkParam.getSendSnSumSec()),
                            String.valueOf(pointWeldWorkParam.getStopSnStimeSec()),
                            String.valueOf(pointWeldWorkParam.getSendSnSpeedThird()),
                            String.valueOf(pointWeldWorkParam.getSendSnSumThird()),
                            String.valueOf(pointWeldWorkParam.getStopSnTimeThird()),
                            String.valueOf(pointWeldWorkParam.getSendSnSpeedFourth()),
                            String.valueOf(pointWeldWorkParam.getSendSnSumFourth()),
                            String.valueOf(pointWeldWorkParam.getStopSnTimeFourth()),
                            String.valueOf(pointWeldWorkParam.getDipDistance()),
                            String.valueOf(pointWeldWorkParam.getUpHeight()),
                            String.valueOf(pointWeldWorkParam.isSn() ? 1 : 0),
                            String.valueOf(pointWeldWorkParam.isPause() ? 1 : 0),
                            String.valueOf(pointWeldWorkParam.getDipDistance_angle()),
                            String.valueOf(pointWeldWorkParam.isSus() ? 1 : 0)},
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    id = cursor.getInt(cursor.getColumnIndex(DBInfo.TableWork._ID));
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
     * @Title delsqlite_sequence
     * @Description 删除表的自增列, 都归零
     * @author wj
     */
    public void delsqlite_sequence() {
        db = dbHelper.getReadableDatabase();
        db.execSQL("DELETE FROM sqlite_sequence");
    }
}
