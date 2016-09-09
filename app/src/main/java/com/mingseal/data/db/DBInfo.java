/**
 *
 */
package com.mingseal.data.db;

import android.os.Environment;

/**
 * @author wj
 */
public class DBInfo {
    /**
     * 数据库
     */
    public static class DB {

        /**
         * 数据库名称
         */
        public static final String DB_NAME = "mingseal_dhp_dh";

        /**
         * 数据库版本
         */
        public static final int VERSION = 1;

        /**
         * 数据库保存的路径
         */
        public static final String DB_LOCATION = "/data/data/com.mingseal.dhp/databases/" + DB_NAME;

        /**
         * 保存之后的数据库的目录
         */
        public static final String DB_SAVED_LOCATION_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
        /**
         * 保存之后的数据库的文件
         */
        public static final String DB_SAVED_LOCATION = DB_SAVED_LOCATION_DIRECTORY + "/" + DB_NAME;
    }

    /**
     * PointTask表,任务列表
     */
    public static class TablePointTask {
        /**
         * PointTask表名称
         */
        public static final String TASK_TABLE = "task_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 任务名称
         */
        public static final String TASK_NAME = "taskName";

        /**
         * Point表的主键 List集合
         */
        public static final String POINT_IDS = "pointIDs";

        /**
         * @Fields CREATE_TIME: 创建日期
         */
        public static final String CREATE_TIME = "createTime";
        /**
         * @Fields MODIFY_TIME: 修改日期
         */
        public static final String MODIFY_TIME = "modifyTime";

        /**
         * 创建任务列表
         */
        public static final String CREATE_TASK_TABLE = "create table if not exists " + TASK_TABLE + "(" + _ID
                + " integer primary key, " + TASK_NAME + " text, " + POINT_IDS + " blob, " + CREATE_TIME + " text, "
                + MODIFY_TIME + " text" + ");";

        /**
         * 删除任务列表
         */
        public static final String DROP_POINT_TABLE = "drop table " + TASK_TABLE;
    }

    /**
     * Point表
     */
    public static class TablePoint {
        /**
         * Point表名称
         */
        public static final String POINT_TABLE = "point_table";

        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * x点坐标
         */
        public static final String POINT_X = "x";
        /**
         * y点坐标
         */
        public static final String POINT_Y = "y";
        /**
         * z点坐标
         */
        public static final String POINT_Z = "z";
        /**
         * u点坐标
         */
        public static final String POINT_U = "u";

        /**
         * 要找任务方案的主键，和POINT_TYPE一起找
         */
        public static final String POINT_PARAM_ID = "paramid";

        /**
         * 点类型
         */
        public static final String POINT_TYPE = "pointtype";

        public static String create_point_table(String taskname) {
            return "create table if not exists " + POINT_TABLE + taskname + "(" + _ID
                    + " integer primary key, " + POINT_X + " integer, " + POINT_Y + " integer, " + POINT_Z + " integer, "
                    + POINT_U + " integer, " + POINT_PARAM_ID + " integer, " + POINT_TYPE + " text" + ");";
        }

        public static String drop_point_table(String taskname) {
            return "drop table " + POINT_TABLE + taskname;
        }

        public static String alter_point_table(String taskname, String newTaskname) {
            return "ALTER TABLE " + POINT_TABLE + taskname + " RENAME TO " + POINT_TABLE + newTaskname;
        }
    }

    /**
     * 焊锡作业点
     */
    public static class TableWork {

        /**
         * 独立点表名称
         */
        public static final String WORK_TABLE = "weld_work_table";

        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 预热时间
         */
        public static final String PREHEATTIME = "preHeatTime";
        /**
         * 一次送锡速度
         */
        public static final String SENDSNSPEEDFIR = "sendSnSpeedFir";
        /**
         * 一次送锡量
         */
        public static final String SENDSNSUMFIR = "sendSnSumFir";
        /**
         * 二次送锡速度
         */
        public static final String SENDSNSPEEDSEC = "sendSnSpeedSec";
        /**
         * 二次送锡量
         */
        public static final String SENDSNSUMSEC = "sendSnSumSec";
        /**
         * 二次停锡时间
         */
        public static final String STOPSNSTIMESEC = "stopSnStimeSec";
        /*
         * 三次送锡速度
         */
        public static final String SENDSNSPEEDTHIRD = "sendSnSpeedThird";
        /*
         * 三次送锡量
         */
        public static final String SENDSNSUMTHIRD = "sendSnSumThird";
        /*
         * 三次停锡时间
         */
        public static final String STOPSNTIMETHIRD = "stopSnTimeThird";
        /*
         * 四次送锡速度
         */
        public static final String SENDSNSPEEDFOURTH = "sendSnSpeedFourth";
        /*
         * 四次送锡量
         */
        public static final String SENDSNSUMFOURTH = "sendSnSumFourth";
        /*
         * 四次停锡时间
         */
        public static final String STOPSNTIMEFOURTH = "stopSnTimeFourth";
        /*
       * 倾斜距离
       */
        public static final String DIPDISTANCE = "dipDistance";
        /*
       * 抬起高度
       */
        public static final String UPHEIGHT = "upHeight";
        /*
     * 是否出锡
     */
        public static final String ISSN = "isSn";
        /*
     * 焊点是否暂停
     */
        public static final String ISPAUSE = "isPause";
        /*
     * 倾斜角度
     */
        public static final String DIPDISTANCE_ANGLE = "dipDistance_angle";
        /*
     * 下降焊锡前是否减速标志
     */
        public static final String ISSUS = "isSus";

        /**
         * 调用以生成作业表
         *
         * @param taskname 任务名
         * @return 表名：独立点+任务名
         */
        public static String create_work_table(String taskname) {
            return "create table if not exists " + WORK_TABLE + taskname + "(" + _ID
                    + " integer primary key, " + PREHEATTIME + " integer, " + SENDSNSPEEDFIR + " integer, "
                    + SENDSNSUMFIR + " integer, " + SENDSNSPEEDSEC + " integer, " + SENDSNSUMSEC + " integer, " + STOPSNSTIMESEC + " BLOB, " + SENDSNSPEEDTHIRD + " integer, "
                    + SENDSNSUMTHIRD + " integer, " + STOPSNTIMETHIRD + " integer," + SENDSNSPEEDFOURTH + " integer, " + SENDSNSUMFOURTH + " integer, " + STOPSNTIMEFOURTH + " integer, "
                    + DIPDISTANCE + " integer, " + UPHEIGHT + " integer, " + ISSN + " integer, " + ISPAUSE + " integer, " + DIPDISTANCE_ANGLE + " integer, " + ISSUS + " integer"
                    + ");";
        }

        /**
         * 删除表
         *
         * @param taskname
         * @return
         */
        public static String drop_work_table(String taskname) {
            return "drop table " + WORK_TABLE + taskname;
        }

        /**
         * 更改表名
         *
         * @param taskname
         * @param newTaskname
         * @return
         */
        public static String alter_work_table(String taskname, String newTaskname) {
            return "ALTER TABLE " + WORK_TABLE + taskname + " RENAME TO " + WORK_TABLE + newTaskname;
        }

    }

    /**
     * 线起始点
     */
    public static class TableLineStart {

        /**
         * 起始点表名称
         */
        public static final String LINE_START_TABLE = "line_start_table";

        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 送锡速度
         */
        public static final String SENDSNSPEED = "sendSnSpeed";
        /**
         * 预送锡量
         */
        public static final String PRESENDSNSUM = "preSendSnSum";
        /**
         * 预送锡速度
         */
        public static final String PRESENDSNSPEED = "preSendSnSpeed";
        /**
         * 是否出锡
         */
        public static final String ISSN = "isSn";
        /**
         * 轨迹速度
         */
        public static final String MOVESPEED = "moveSpeed";
        /**
         * 预热时间
         */
        public static final String PREHEATTIME = "preHeatTime";

        public static String create_line_start_table(String taskname) {
            return "create table if not exists " + LINE_START_TABLE + taskname + "("
                    + _ID + " integer primary key autoincrement, " + SENDSNSPEED + " integer, " + PRESENDSNSUM
                    + " integer, " + PRESENDSNSPEED + " integer, " + ISSN + " integer, " + MOVESPEED + " integer, "
                    + PREHEATTIME + " integer" + ");";
        }

        public static String drop_line_start_table(String taskname) {
            return "drop table " + LINE_START_TABLE + taskname;
        }

        public static String alter_line_start_table(String taskname, String newTaskname) {
            return "ALTER TABLE " + LINE_START_TABLE + taskname + " RENAME TO " + LINE_START_TABLE + newTaskname;
        }
    }

    /**
     * 线中间点
     */
    public static class TableLineMid {
        /**
         * 线中间点表名称
         */
        public static final String LINE_MID_TABLE = "line_mid_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 轨迹速度
         */
        public static final String MOVESPEED = "moveSpeed";
        /**
         * 送锡速度
         */
        public static final String SENDSNSPEED = "sendSnSpeed";
        /**
         * 停锡延时
         */
        public static final String STOPSNTIME = "stopSnTime";
        /**
         * 是否出锡
         */
        public static final String ISSN = "isSn";


        public static String create_line_mid_table(String taskname) {
            return "create table if not exists " + LINE_MID_TABLE + taskname + "(" + _ID
                    + " integer primary key autoincrement, " + MOVESPEED + " integer, " + SENDSNSPEED + " integer, "
                    + STOPSNTIME + " integer, " + ISSN + " integer " + ");";
        }

        public static String drop_line_mid_table(String taskname) {
            return "drop table " + LINE_MID_TABLE + taskname;
        }

        public static String alter_line_mid_table(String taskname, String newTaskname) {
            return "ALTER TABLE " + LINE_MID_TABLE + taskname + " RENAME TO " + LINE_MID_TABLE + newTaskname;
        }
    }

    /**
     * 线结束点
     */
    public static class TableLineEnd {
        /**
         * 线结束点表名称
         */
        public static final String LINE_END_TABLE = "line_end_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 停锡延时
         */
        public static final String STOPSNTIME = "stopSnTime";
        /**
         * 抬起高度
         */
        public static final String UPHEIGHT = "upHeight";
        /**
         * 是否暂停
         */
        public static final String ISPAUSE = "isPause";

        public static String create_line_end_table(String taskname) {
            return "create table if not exists " + LINE_END_TABLE + taskname + "(" + _ID
                    + " integer primary key autoincrement, " + STOPSNTIME + " integer, " + UPHEIGHT
                    + " integer, " + ISPAUSE + " integer" + ");";
        }

        public static String drop_line_end_table(String taskname) {
            return "drop table " + LINE_END_TABLE + taskname;
        }

        public static String alter_line_end_table(String taskname, String newTaskname) {
            return "ALTER TABLE " + LINE_END_TABLE + taskname + " RENAME TO " + LINE_END_TABLE + newTaskname;
        }

    }

    /**
     * 点胶输出IO
     */
    public static class TableOutputIO {

        /**
         * 输出IO表名称
         */
        public static final String OUTPUT_IO_TABLE = "output_io_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 动作前延时
         */
        public static final String GO_TIME_PREV = "goTimePrev";
        /**
         * 动作后延时
         */
        public static final String GO_TIME_NEXT = "goTimeNext";
        /**
         * 输入口
         */
        public static final String INPUT_PORT = "inputPort";

        /**
         * 创建输出IO表语
         */
        public static final String CREATE_OUTPUT_IO_TABLE = "create table if not exists " + OUTPUT_IO_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + GO_TIME_PREV + " integer, " + GO_TIME_NEXT + " integer, "
                + INPUT_PORT + " BLOB" + ");";

        /**
         * 删除输出IO表语
         */
        public static final String DROP_OUTPUT_IO_TABLE = "drop table " + OUTPUT_IO_TABLE;

    }

    /**
     * 吹锡
     */
    public static class TableWeldBlow {

        /**
         * 输出IO表名称
         */
        public static final String WELD_BLOW_TABLE = "weld_blow_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 动作前延时
         */
        public static final String GO_TIME_PREV = "goTimePrev";

        /**
         * 是否出锡
         */
        public static final String ISSN = "isSn";

        public static String create_WELD_BLOW_table(String taskname) {
            return "create table if not exists " + WELD_BLOW_TABLE + taskname + "(" + _ID
                    + " integer primary key autoincrement, " + GO_TIME_PREV + " integer, "
                    + ISSN + " integer" + ");";
        }

        public static String drop_WELD_BLOW_table(String taskname) {
            return "drop table " + WELD_BLOW_TABLE + taskname;
        }

        public static String alter_WELD_BLOW_table(String taskname, String newTaskname) {
            return "ALTER TABLE " + WELD_BLOW_TABLE + taskname + " RENAME TO " + WELD_BLOW_TABLE + newTaskname;
        }

    }

    /**
     * 点胶输入IO
     */
    public static class TableInputIO {

        /**
         * 输入IO表名称
         */
        public static final String INPUT_IO_TABLE = "input_io_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 动作前延时
         */
        public static final String GO_TIME_PREV = "goTimePrev";
        /**
         * 动作后延时
         */
        public static final String GO_TIME_NEXT = "goTimeNext";
        /**
         * 输入口
         */
        public static final String INPUT_PORT = "inputPort";

        /**
         * 创建输入IO表语
         */
        public static final String CREATE_INPUT_IO_TABLE = "create table if not exists " + INPUT_IO_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + GO_TIME_PREV + " integer, " + GO_TIME_NEXT + " integer, "
                + INPUT_PORT + " BLOB" + ");";

        /**
         * 删除输入IO表语
         */
        public static final String DROP_INPUT_IO_TABLE = "drop table " + INPUT_IO_TABLE;

    }

    public static class TableUser {
        /**
         * 用户表名称
         */
        public static final String USER_TABLE = "user_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 用户名
         */
        public static final String USERNAME = "username";
        /**
         * 密码
         */
        public static final String PASSWORD = "password";
        /**
         * 类型(管理员/技术支持/操作员)
         */
        public static final String TYPE = "type";
        /**
         * 创建用户表语句
         */
        public static final String CREATE_USER_TABLE = "create table if not exists " + USER_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + USERNAME + " text, " + PASSWORD + " text, " + TYPE + " text"
                + ");";
        /**
         * 删除用户表语
         */
        public static final String DROP_USER_TABLE = "drop table " + USER_TABLE;

    }

    public static class WifiSSID {
        /**
         * 用户表名称
         */
        public static final String WIFI_TABLE = "wifi_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * wifi-ssid
         */
        public static final String SSID = "ssid";
        /**
         * 创建用户表语句
         */
        public static final String CREATE_WIFI_TABLE = "create table if not exists " + WIFI_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + SSID + " text " + ");";
        /**
         * 删除用户表语
         */
        public static final String DROP_WIFI_TABLE = "drop table " + WIFI_TABLE;
    }
}
