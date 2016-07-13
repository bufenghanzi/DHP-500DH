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

        /**
         * 创建Point表语
         */
        public static final String CREATE_POINT_TABLE = "create table if not exists " + POINT_TABLE + "(" + _ID
                + " integer primary key, " + POINT_X + " integer, " + POINT_Y + " integer, " + POINT_Z + " integer, "
                + POINT_U + " integer, " + POINT_PARAM_ID + " integer, " + POINT_TYPE + " text" + ");";
        /**
         * 删除Point表语
         */
        public static final String DROP_POINT_TABLE = "drop table " + POINT_TABLE;
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
         * 焊点结束退出时是否抬起停顿标志
         */
        public static final String ISOUT = "isOut";
            /*
         * 下降焊锡前是否减速标志
         */
        public static final String ISSUS = "isSus";

        /**
         * 方案号
         */
//		public static final String PLAN="plan_id";

        /**
         * 创建作业点表语
         */
        public static final String CREATE_WORK_TABLE = "create table if not exists " + WORK_TABLE + "(" + _ID
                + " integer primary key, " + PREHEATTIME + " integer, " + SENDSNSPEEDFIR + " integer, "
                + SENDSNSUMFIR + " integer, " + SENDSNSPEEDSEC + " integer, " + SENDSNSUMSEC + " integer, " + STOPSNSTIMESEC + " BLOB, "+ SENDSNSPEEDTHIRD + " integer, "
                + SENDSNSUMTHIRD + " integer, "+ STOPSNTIMETHIRD + " integer," + SENDSNSPEEDFOURTH + " integer, " + SENDSNSUMFOURTH + " integer, " + STOPSNTIMEFOURTH + " integer, "
                + DIPDISTANCE + " integer, " + UPHEIGHT + " integer, " + ISSN + " integer, " + ISPAUSE + " integer, " + ISOUT + " integer, "+ ISSUS + " integer"
                + ");";

        /**
         * 删除作业表语
         */
        public static final String DROP_WORK_TABLE = "drop table " + WORK_TABLE;

    }

    /**
     * 面起始点
     */
    public static class TableFaceStart {
        /**
         * 面起始点表名称
         */
        public static final String FACE_START_TABLE = "face_start_table";

        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 出胶前延时
         */
        public static final String OUT_GLUE_TIME_PREV = "outGlueTimePrev";
        /**
         * 出胶(后)延时
         */
        public static final String OUT_GLUE_TIME = "outGlueTime";
        /**
         * 轨迹速度
         */
        public static final String MOVE_SPEED = "moveSpeed";
        /**
         * 是否出胶
         */
        public static final String IS_OUT_GLUE = "isOutGlue";
        /**
         * 停胶延时
         */
        public static final String STOP_GLUE_TIME = "stopGlueTime";
        /**
         * 起始方向 true:x方向 false:y方向
         */
        public static final String START_DIR = "startDir";
        /**
         * 点胶口
         */
        public static final String GLUE_PORT = "gluePort";

        /**
         * 创建面起始点的sql
         */
        public static final String CREATE_FACE_START_TABLE = "create table if not exists " + FACE_START_TABLE + "("
                + _ID + " integer primary key autoincrement, " + OUT_GLUE_TIME_PREV + " integer, " + OUT_GLUE_TIME
                + " integer, " + MOVE_SPEED + " integer, " + IS_OUT_GLUE + " integer, " + STOP_GLUE_TIME + " integer, "
                + START_DIR + " integer, " + GLUE_PORT + " BLOB" + ");";

        /**
         * 删除面起始点的sql
         */
        public static final String DROP_FACE_START_TABLE = "drop table " + FACE_START_TABLE;
    }

    /**
     * 面结束点
     */
    public static class TableFaceEnd {

        /**
         * 面结束点名称
         */
        public static final String FACE_END_TABLE = "face_end_table";
        /**
         * 主键
         */
        public static final String _ID = "_id";
        /**
         * 停胶延时
         */
        public static final String STOP_GLUE_TIME = "stopGlueTime";
        /**
         * 抬起高度
         */
        public static final String UP_HEIGHT = "upHeight";
        /**
         * 直线条数
         */
        public static final String LINE_NUM = "lineNum";
        /**
         * 是否暂停
         */
        public static final String IS_PAUSE = "isPause";

        /**
         * 创建面结束点的sql
         */
        public static final String CREATE_FACE_END_TABLE = "create table if not exists " + FACE_END_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + STOP_GLUE_TIME + " integer, " + UP_HEIGHT + " integer, "
                + LINE_NUM + " integer, " + IS_PAUSE + " integer" + ");";

        /**
         * 删除面结束点的sql
         */
        public static final String DROP_FACE_END_TABLE = "drop table " + FACE_END_TABLE;
    }

    /**
     * @author 商炎炳
     */
    public static class TableClear {

        /**
         * 清胶点数据表名
         */
        public static final String CLEAR_TABLE = "clear_table";
        /**
         * 清胶点主键
         */
        public static final String _ID = "_id";
        /**
         * 清胶延时
         */
        public static final String CLEAR_GLUE_TIME = "clearGlueTime";

        /**
         * 创建清胶点的sql
         */
        public static final String CREATE_CLEAR_TABLE = "create table if not exists " + CLEAR_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + CLEAR_GLUE_TIME + " integer" + ");";

        /**
         * 删除清胶点的sql
         */
        public static final String DROP_CLEAR_TABLE = "drop table " + CLEAR_TABLE;
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
        /**
         * 创建线起始点表语
         */
        public static final String CREATE_LINE_START_TABLE = "create table if not exists " + LINE_START_TABLE + "("
                + _ID + " integer primary key autoincrement, " + SENDSNSPEED + " integer, " + PRESENDSNSUM
                + " integer, " + PRESENDSNSPEED + " integer, " + ISSN + " integer, " + MOVESPEED + " integer, "
                + PREHEATTIME + " integer" + ");";

        /**
         * 删除线起始点表语
         */
        public static final String DROP_LINE_START_TABLE = "drop table " + LINE_START_TABLE;
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
        /**
         * 创建线中间点表语
         */
        public static final String CREATE_LINE_Mid_TABLE = "create table if not exists " + LINE_MID_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + MOVESPEED + " integer, " + SENDSNSPEED + " integer, "
                + STOPSNTIME + " integer, " + ISSN + " integer " + ");";

        /**
         * 删除线中间点表语
         */
        public static final String DROP_LINE_MID_TABLE = "drop table " + LINE_MID_TABLE;
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

        /**
         * 创建线结束点表语
         */
        public static final String CREATE_LINE_END_TABLE = "create table if not exists " + LINE_END_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + STOPSNTIME + " integer, " + UPHEIGHT
                + " integer, " + ISPAUSE + " integer"  + ");";

        /**
         * 删除线结束点表语
         */
        public static final String DROP_LINE_END_TABLE = "drop table " + LINE_END_TABLE;

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
        public static final String CREATE_WELD_BLOW_TABLE = "create table if not exists " + WELD_BLOW_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + GO_TIME_PREV + " integer, " + GO_TIME_NEXT + " integer, "
                + INPUT_PORT + " BLOB" + ");";

        /**
         * 删除输出IO表语
         */
        public static final String DROP_WELD_BLOW_TABLE = "drop table " + WELD_BLOW_TABLE;

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
