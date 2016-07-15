package com.mingseal.data.manager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.mingseal.application.UserApplication;
import com.mingseal.communicate.SocketThreadManager;
import com.mingseal.data.param.CmdParam;
import com.mingseal.data.param.OrderParam;
import com.mingseal.data.param.TaskParam;
import com.mingseal.data.param.robot.RobotParam;
import com.mingseal.data.point.Point;
import com.mingseal.data.point.PointType;
import com.mingseal.data.point.weldparam.PointWeldBlowParam;
import com.mingseal.data.point.weldparam.PointWeldInputIOParam;
import com.mingseal.data.point.weldparam.PointWeldLineEndParam;
import com.mingseal.data.point.weldparam.PointWeldLineMidParam;
import com.mingseal.data.point.weldparam.PointWeldLineStartParam;
import com.mingseal.data.point.weldparam.PointWeldOutputIOParam;
import com.mingseal.data.point.weldparam.PointWeldWorkParam;
import com.mingseal.data.protocol.Protocol_400_1;
import com.mingseal.utils.DataCheckout;
import com.mingseal.utils.DateUtil;
import com.mingseal.utils.SocketThread;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author 李英骑
 * @ClassName: MessageMgr
 * @Description: 消息管理类
 * @date 2015年11月2日 下午2:26:05
 * @Companly Mingseal.Ltd
 */
public enum MessageMgr {

    INSTANCE;
    private final static String TAG = "MessageMgr";
    private final int ORDER_BUFFER_LENTH = 100;
    private final int SIZEOF_SHORT = 2;
    private final int ARC_FLAG = 65533;
    private final int CHANGE_FLAG = 65534;
    private final int INSERT_FLAG = 65535;
    private final int OFFSET_VAL = 32768;
    private final int USER_O_NO_NUM = 5;
    private final int MAXDISTANCE = 200;
    private final int MINDISTANCE = 1;
    private final int UPLOAD_SUCCESS = 1248;
    private final int UPLOAD_FAILURE = 1249;
    private final int UPLOADING = 8421;
    private byte[] buffer;
    private Protocol_400_1 protocol = null;
    private Point point = null;
    private CmdParam cmd;
    private CmdParam[] stepCmd;
    private int step = 0;
    private SocketThread mThread;
    private Handler handler;
    public CmdParam cmdDelayFlag = CmdParam.Cmd_Null;
    private int orderLength = 0;
    private int m_nRobotSeries;
    private List<Point> mPointList;
    private boolean isDH = false;

    private byte[] data = null;
    private int size = 0;
    private UserApplication userApplication;
    private Context context;

    //-----------------------任务上传相关变量-----------------------
    private List<Byte> revTaskBuffer;
    private int revTaskSize = 0;//PreUpload命令返回数据中的任务长度
    public int upLoadLen = 600;//上传的任务数据包长度
    private int upLoadRetryTimes = 0;//上传重发尝试次数
    private boolean isUploading = false;//正在处理任务上传标志
    private boolean upLoadFlag = false;//数据包上传标志
    private boolean upLoadRetryFlag = false;//数据包重新上传标志
    private boolean upLoadSuccess = false;//数据上传完成标志
    private int nNum = 0;//任务点个数


    private MessageMgr() {
        stepCmd = new CmdParam[2];
        revTaskBuffer = new ArrayList<Byte>();
        // buffer = new byte[ORDER_BUFFER_LENTH];
        this.protocol = new Protocol_400_1();
        this.point = new Point(PointType.POINT_NULL);
    }

    /**
     * @Title: init
     * @Description: MessageMgr的初始化函数
     */
    public void init() {
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void startThread() {
        mThread = new SocketThread(handler);
        new Thread(mThread).start();
    }

    public UserApplication getUserApplication() {
        return userApplication;
    }

    /**
     * 设置全局变量的UserApplication
     *
     * @param userApplication
     */
    public void setUserApplication(UserApplication userApplication) {
        this.userApplication = userApplication;
    }

    /**
     * @param buffer
     * @param orderLength
     * @Title: writeData
     * @Description: 内部写数据方法
     */
    public void writeData(byte[] buffer, int orderLength) {
        SocketThreadManager.sharedInstance().sendMsg(buffer);
    }

//    /**
//     * @param _pt    点集信息
//     * @param _ptEnd 结束点信息
//     * @param task   任务数据流
//     * @return
//     * @Title: insertPoint400
//     * @Description: 结束点断胶插点
//     */
//    private int insertPoint400(Point[] _pt, Point _ptEnd, TaskDataStream task) {
//        PointInfo400 info = new PointInfo400();
//        int nNum = 0;
//        PointWeldLineEndParam paramEnd = userApplication.getLineEndParamMaps().get(_ptEnd.getPointParam().get_id());
//
//        int nMoveSpeed = 0;
//        if (_pt[1].getPointParam().getPointType() == PointType.POINT_WELD_LINE_START) {
//            nMoveSpeed = userApplication.getLineStartParamMaps().get(_pt[1].getPointParam().get_id()).getMoveSpeed();
//        } else if (_pt[1].getPointParam().getPointType() == PointType.POINT_WELD_LINE_MID) {
//            nMoveSpeed = userApplication.getLineMidParamMaps().get(_pt[1].getPointParam().get_id()).getMoveSpeed();
//        } else if (_pt[0].getPointParam().getPointType() == PointType.POINT_WELD_LINE_START) {
//            nMoveSpeed = userApplication.getLineStartParamMaps().get(_pt[0].getPointParam().get_id()).getMoveSpeed();
//        } else if (_pt[0].getPointParam().getPointType() == PointType.POINT_WELD_LINE_MID) {
//            nMoveSpeed = userApplication.getLineMidParamMaps().get(_pt[0].getPointParam().get_id()).getMoveSpeed();
//        }
//
//        SMatrix1_4 m1 = new SMatrix1_4(_pt[2].getX(), _pt[2].getY(), _pt[2].getZ());
//        SMatrix1_4 m2 = new SMatrix1_4(_pt[3].getX(), _pt[3].getY(), _pt[3].getZ());
//        double dis = SMatrix1_4.operator_mod3(SMatrix1_4.operator_minus(m1, m2));
//        double len = RobotParam.INSTANCE.XJourney2Pulse(paramEnd.getStopSnTime());
//        if (len > dis) {
//            len = dis / 2;
//        }
//        SMatrix1_4 m = CommonArithmetic.insertLinePoint(m1, m2, (float) len);
//        // 将结束点变成中间点
//        info.setAllValueDefault();
//        info.setPointType((byte) 2);
//        info.setLen((byte) 48);
//        info.setFlag((byte) 2);
//        info.setIfPause((byte) (paramEnd.isPause() ? 1 : 0));
//        // pInfo = ByteArray2ShortArray(info.getPointInfo());
//        // task.PushBack(pInfo[0]);
//        // task.PushBack(pInfo[1]);
//        // task.PushBack(pInfo[2]);
//        byte[] temp = info.getPointInfo();
//        task.pushBackByByte(temp[0]);
//        task.pushBackByByte(temp[1]);
//        task.pushBackByByte(temp[2]);
//        task.pushBackByByte(temp[3]);
//        task.pushBackByByte(temp[4]);
//        task.pushBackByByte(temp[5]);
//        task.pushBack(_ptEnd.getX());
//        task.pushBack(_ptEnd.getX() >>> 16);
//        task.pushBack(_ptEnd.getY());
//        task.pushBack(_ptEnd.getY() >>> 16);
//        task.pushBack(_ptEnd.getZ());
//        task.pushBack(_ptEnd.getZ() >>> 16);
//        task.pushBack(_ptEnd.getU());
//        task.pushBack(_ptEnd.getU() >>> 16);
//        task.pushBack(0);// 组号
//        task.pushBack(0);// 组号
//        // 无断胶时直接拉丝
//        if (paramEnd.getBreakGlueLen() == 0) {
//            nMoveSpeed = paramEnd.getDrawSpeed();
//        }
//        task.pushBack(nMoveSpeed);
//        task.pushBack(nMoveSpeed >>> 16);
//        int nTurnAngle = CommonArithmetic.getTurnAngle(_pt, 2);
//        // 无断胶时直接拉丝
//        if (paramEnd.getBreakGlueLen() == 0) {
//            nTurnAngle = 90;
//        }
//        task.pushBack(nTurnAngle);
//        int nTurnV = (int) (CommonArithmetic.getTurnV(_pt, 2) * 1000000);
//        task.pushBack(nTurnV);
//        task.pushBack(nTurnV >>> 16);
//        task.pushBack(paramEnd.getStopGlueTime());
//        int nUpHeight = RobotParam.INSTANCE.ZJourney2Pulse(paramEnd.getUpHeight());
//        task.pushBack(nUpHeight);
//        task.pushBack(nUpHeight >>> 16);
//        task.pushBack(paramEnd.getBreakGlueLen());
//        task.pushBack(paramEnd.getDrawDistance());
//        task.pushBack(paramEnd.getDrawSpeed());
//        nNum++;
//        if (paramEnd.getBreakGlueLen() > 0) {
//            // 插入一拉丝中间点
//            info.setAllValueDefault();
//            info.setPointType((byte) 2);
//            info.setLen((byte) 36);
//            info.setFlag((byte) 1);// 插入点标记
//            // pInfo = ByteArray2ShortArray(info.getPointInfo());
//            // task.PushBack(pInfo[0]);
//            // task.PushBack(pInfo[1]);
//            // task.PushBack(pInfo[2]);
//            temp = info.getPointInfo();
//            task.pushBackByByte(temp[0]);
//            task.pushBackByByte(temp[1]);
//            task.pushBackByByte(temp[2]);
//            task.pushBackByByte(temp[3]);
//            task.pushBackByByte(temp[4]);
//            task.pushBackByByte(temp[5]);
//            task.pushBack((int) Math.floor(m.getX()));
//            task.pushBack(((int) Math.floor(m.getX())) >>> 16);
//            task.pushBack((int) Math.floor(m.getY()));
//            task.pushBack(((int) Math.floor(m.getY())) >>> 16);
//            task.pushBack((int) Math.floor(m.getZ()));
//            task.pushBack(((int) Math.floor(m.getZ())) >>> 16);
//            task.pushBack(_ptEnd.getU());
//            task.pushBack(_ptEnd.getU() >>> 16);
//            task.pushBack(0);// 组号
//            task.pushBack(0);// 组号
//            task.pushBack(paramEnd.getDrawSpeed());
//            task.pushBack(paramEnd.getDrawSpeed() >>> 16);
//            nTurnAngle = 90;
//            task.pushBack(nTurnAngle);
//            nTurnV = 2 * 1000000;
//            task.pushBack(nTurnV);
//            task.pushBack(nTurnV >>> 16);
//            nNum++;
//        }
//        // 插入一结束点
//        info.setAllValueDefault();
//        info.setPointType((byte) 4);
//        info.setLen((byte) 32);
//        info.setFlag((byte) 1);// 插入点标记
//        info.setIfPause((byte) (paramEnd.isPause() ? 1 : 0));
//        int z = (int) m.getZ() - RobotParam.INSTANCE.ZJourney2Pulse(paramEnd.getDrawDistance());
//        // 防止拉丝抬起过高
//        if (z < RobotParam.INSTANCE.ZJourney2Pulse(paramEnd.getUpHeight())) {
//            z = RobotParam.INSTANCE.ZJourney2Pulse(paramEnd.getUpHeight());
//        }
//        // pInfo = ByteArray2ShortArray(info.getPointInfo());
//        // task.PushBack(pInfo[0]);
//        // task.PushBack(pInfo[1]);
//        // task.PushBack(pInfo[2]);
//        temp = info.getPointInfo();
//        task.pushBackByByte(temp[0]);
//        task.pushBackByByte(temp[1]);
//        task.pushBackByByte(temp[2]);
//        task.pushBackByByte(temp[3]);
//        task.pushBackByByte(temp[4]);
//        task.pushBackByByte(temp[5]);
//        task.pushBack((int) Math.floor(m.getX()));
//        task.pushBack(((int) Math.floor(m.getX())) >>> 16);
//        task.pushBack((int) Math.floor(m.getY()));
//        task.pushBack(((int) Math.floor(m.getY())) >>> 16);
//        task.pushBack(z);
//        task.pushBack(z >>> 16);
//        task.pushBack(_ptEnd.getU());
//        task.pushBack(_ptEnd.getU() >>> 16);
//        task.pushBack(0);// 组号
//        task.pushBack(0);// 组号
//        // 拉丝后无需停胶延时
//        task.pushBack(0);
//        // ---------------------------
//        task.pushBack(nUpHeight);
//        task.pushBack(nUpHeight >>> 16);
//        nNum++;
//        return nNum;
//    }

    /**
     * @param _ptemp
     * @return 点数组
     * @Title: object2Point
     * @Description: Object数组转byte数组
     */
    private Point[] object2Point(Object[] _ptemp) {
        Point[] result = new Point[_ptemp.length];
        for (int i = 0; i < _ptemp.length; i++) {
            result[i] = (Point) _ptemp[i];
        }
        return result;
    }

    /**
     * 组织任务数据流添加输入IO点
     *
     * @param p    当前任务点
     * @param task 任务数据流
     */
    private void createTask400WeldInput(Point p, TaskDataStream task) {
        PointInfo400 info = new PointInfo400();
        info.setPointType((byte) 7);
        info.setIoFlag((byte) 1);// 输入标记 0:输出 1:输入
        info.setLen((byte) 10);
        PointWeldInputIOParam pParam = userApplication.getInputParamMaps().get(p.getPointParam().get_id());
        boolean[] ioStatus = pParam.getInputPort();
        for (int i = 0, j = ioStatus.length - 1; i < ioStatus.length; i++, j--) {
            info.setInputIOPort(j, ioStatus[i]);
        }
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);
        task.pushBack(pParam.getGoTimePrev());
        task.pushBack(pParam.getGoTimeNext());
        nNum++;
    }

    /**
     * 组织任务数据流添加输出IO点
     *
     * @param p    当前任务点
     * @param task 任务数据流
     */
    private void createTask400WeldOutput(Point p, TaskDataStream task) {
        PointInfo400 info = new PointInfo400();
        info.setPointType((byte) 7);
        info.setIoFlag((byte) 0);// 输入标记 0:输出 1:输入
        info.setLen((byte) 10);
        PointWeldOutputIOParam pParam = userApplication.getOutputParamMaps().get(p.getPointParam().get_id());
        boolean[] ioStatus = pParam.getInputPort();
        for (int i = 0, j = ioStatus.length - 1; i < ioStatus.length; i++, j--) {
            info.setOutputIOPort(j, ioStatus[i]);
        }
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);
        task.pushBack(pParam.getGoTimePrev());
        task.pushBack(pParam.getGoTimeNext());
        nNum++;
    }

    /**
     * 组织任务数据流添加吹锡IO点
     *
     * @param p    当前任务点
     * @param task 任务数据流
     */
    private void createTask400WeldBlow(Point p, TaskDataStream task) {
        PointInfo400 info = new PointInfo400();
        info.setPointType((byte) 7);
        info.setIoFlag((byte) 0);// 输入标记 0:输出 1:输入
        info.setLen((byte) 10);
        PointWeldBlowParam pParam = userApplication.getBlowParamMaps().get(p.getPointParam().get_id());
        boolean[] ioStatus = pParam.getOutputPort();
        for (int i = 0, j = ioStatus.length - 1; i < ioStatus.length; i++, j--) {
            info.setOutputIOPort(j, ioStatus[i]);
        }
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);
        task.pushBack(pParam.getGoTimePrev());
        task.pushBack(pParam.getGoTimeNext());
        nNum++;
    }

    /**
     * 组织任务数据流添加独立点改为作业点
     *
     * @param p    当前任务点
     * @param task 任务数据流
     */
    private void createTask400GlueAlone(Point p, TaskDataStream task) {
        PointWeldWorkParam pParam = userApplication.getAloneParamMaps().get(p.getPointParam().get_id());
        PointInfo400 info = new PointInfo400();

        info.setAllValueDefault();
        info.setPointType((byte) 0);//作业点
        info.setIfSn((byte) (pParam.isSn() ? 1 : 0));//是否出锡
        info.setIfOut((byte) (pParam.isOut() ? 1 : 0));//是否抬停
        info.setIfSus((byte) (pParam.isSus() ? 1 : 0));//是否减速
        info.setLen((byte) 54);//记录总字节数
        info.setIfPause((byte) (pParam.isPause() ? 1 : 0));//是否暂停
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);

        task.pushBack(p.getX());
        task.pushBack(p.getX() >>> 16);
        task.pushBack(p.getY());
        task.pushBack(p.getY() >>> 16);
        task.pushBack(p.getZ());
        task.pushBack(p.getZ() >>> 16);
        task.pushBack(p.getU());
        task.pushBack(p.getU() >>> 16);

        task.pushBack(0);// 组号
        task.pushBack(0);// 组号
        task.pushBack(pParam.getSendSnSpeedFir());//一次送锡速度
        task.pushBack(pParam.getSendSnSumFir());//一次送锡量
        task.pushBack(pParam.getPreHeatTime());//预热时间
        task.pushBack(pParam.getSendSnSpeedSec());//二次送锡速度
        task.pushBack(pParam.getSendSnSumSec());//二次送锡量
        task.pushBack(pParam.getStopSnStimeSec());//二次停锡时间
        task.pushBack(pParam.getSendSnSpeedThird());//三次送锡速度
        task.pushBack(pParam.getSendSnSumThird());//三次送锡量
        task.pushBack(pParam.getStopSnTimeThird());//三次停锡时间
        task.pushBack(pParam.getSendSnSpeedFourth());//四次送锡速度
        task.pushBack(pParam.getSendSnSumFourth());//四次送锡量
        task.pushBack(pParam.getStopSnTimeFourth());//四次停锡时间
        task.pushBack(pParam.getDipDistance());//倾斜距离
        task.pushBack(pParam.getUpHeight());//抬起高度
        nNum++;

    }

    /**
     * 组织任务数据流添加起始点
     *
     * @param p    当前任务点
     * @param task 任务数据流
     */
    private void createTask400GlueLineStart(Point p, TaskDataStream task) {
        PointWeldLineStartParam pParam = userApplication.getLineStartParamMaps().get(p.getPointParam().get_id());

        PointInfo400 info = new PointInfo400();
        info.setAllValueDefault();
        info.setPointType((byte) 1);
        info.setIfSn((byte) (pParam.isSn() ? 1 : 0));//是否出锡
        info.setLen((byte) 36);
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);
        task.pushBack(p.getX());
        task.pushBack(p.getX() >>> 16);
        task.pushBack(p.getY());
        task.pushBack(p.getY() >>> 16);
        task.pushBack(p.getZ());
        task.pushBack(p.getZ() >>> 16);
        task.pushBack(p.getU());
        task.pushBack(p.getU() >>> 16);
        task.pushBack(0);// 组号
        task.pushBack(0);// 组号
        task.pushBack(pParam.getMoveSpeed());//运行速度
        task.pushBack(pParam.getSnSpeed());//送锡速度
        task.pushBack(pParam.getPreSendSnSpeed());//预送锡速度
        task.pushBack(pParam.getPreSendSnSum());//预送锡量
        task.pushBack(pParam.getPreHeatTime());//预送时间
        nNum++;
    }

    /**
     * 组织任务数据流添加中间点
     *
     * @param pointList     Point集合
     * @param pointCountNum 当前num
     * @param p             当前任务点
     * @param task          任务数据流
     * @param _pt           point数组
     */
    private void createTask400GlueLineMid(List<Point> pointList, int pointCountNum, Point p, TaskDataStream task, Point[] _pt) {

        PointWeldLineMidParam pParam = userApplication.getLineMidParamMaps().get(p.getPointParam().get_id());

        //插入一中间点
        PointInfo400 info = new PointInfo400();
        info.setAllValueDefault();
        info.setPointType((byte) 2);
        info.setIfSn((byte) (pParam.isSn() ? 1 : 0));//是否出锡
        info.setLen((byte) 32);
        // GetGWOutput
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);
        task.pushBack(p.getX());
        task.pushBack(p.getX() >>> 16);
        task.pushBack(p.getY());
        task.pushBack(p.getY() >>> 16);
        task.pushBack(p.getZ());
        task.pushBack(p.getZ() >>> 16);
        task.pushBack(p.getU());
        task.pushBack(p.getU() >>> 16);
        task.pushBack(0);
        task.pushBack(0);
        task.pushBack(pParam.getMoveSpeed());
        task.pushBack(pParam.getSendSnSpeed());
        task.pushBack(pParam.getStopSnTime());
        nNum++;
    }

    /**
     * 组织任务数据流添加结束点
     *
     * @param pointList
     * @param pointCountNum
     * @param p             当前任务点
     * @param task          任务数据流
     */
    private void createTask400GlueLineEnd(List<Point> pointList, int pointCountNum, Point p, TaskDataStream task) {
        PointWeldLineEndParam pParam = userApplication.getLineEndParamMaps().get(p.getPointParam().get_id());

        PointInfo400 info = new PointInfo400();
        info.setAllValueDefault();
        info.setPointType((byte) 4);
        info.setLen((byte) 30);
        info.setIfPause((byte) (pParam.isPause() ? 1 : 0));
        byte[] temp = info.getPointInfo();
        task.pushBackByByte(temp[0]);
        task.pushBackByByte(temp[1]);
        task.pushBackByByte(temp[2]);
        task.pushBackByByte(temp[3]);
        task.pushBackByByte(temp[4]);
        task.pushBackByByte(temp[5]);
        task.pushBack(p.getX());
        task.pushBack(p.getX() >>> 16);
        task.pushBack(p.getY());
        task.pushBack(p.getY() >>> 16);
        task.pushBack(p.getZ());
        task.pushBack(p.getZ() >>> 16);
        task.pushBack(p.getU());
        task.pushBack(p.getU() >>> 16);
        task.pushBack(0);// 组号
        task.pushBack(0);// 组号
        task.pushBack(pParam.getStopSnTime());
        task.pushBack(pParam.getUpHeight());
        nNum++;
    }

    /**
     * @param pointList 点集信息
     * @param num       点集个数
     * @param task      任务数据流
     * @return
     * @Title: createTask400
     * @Description: 组织任务数据流
     */
    private int createTask400(List<Point> pointList, int num, TaskDataStream task) {
        Object[] _ptemp = pointList.toArray();
        Point[] _pt = object2Point(_ptemp);
        if (!task.byteTask.isEmpty()) {
            task.clear();
        }
        int pointCountNum = 0;
        //数据头110个字节，添加55次
        for (int i = 0; i < 55; i++) {
            task.pushBack(0);
        }
        //设置任务号
        task.setValue(0, TaskParam.INSTANCE.getnTaskNum());
        //任务速度曲线(0:梯形曲线，1:S形曲线)
        if (TaskParam.INSTANCE.getnSpeedCurve() > 0) {
            short temp = task.getByte2ShortValue(0);
            temp &= 0x00ff;
            temp |= (1 << 15);
            task.setValue(0, temp);
        }
        {
            // 将任务名存入vector中
            byte[] buffer19 = new byte[19];
            byte[] buffer20 = new byte[20];
            byte[] buffer = null;
            try {
                //底层都使用单字节的ascii码字符,双字节的string转换为单字节的字节数组
                buffer = TaskParam.INSTANCE.getStrTaskName().getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (buffer.length > 19) {
                for (int j = 0; j < 19; j++) {
                    buffer19[j] = buffer[j];
                }
            } else {
                for (int j = 0; j < buffer.length; j++) {
                    buffer19[j] = buffer[j];
                }
            }
            // memcpy//暂行性方法,效率低,内存占用大
            short[] temp = byteArray2ShortArray(buffer19);
            for (int i = 0; i < temp.length; i++) {
                task.setValueR(i + 1, temp[i]);
            }
        }
        task.setValue(32, 1);// 零点校正默认1：回起始点模式
        task.setValue(33, TaskParam.INSTANCE.getnAccelerate());
        task.setValue(34, TaskParam.INSTANCE.getnDecelerate());
        task.setValue(35, TaskParam.INSTANCE.getnTurnSpeed());
        task.setValue(36, 5);// 基点调整单位默认5档
        task.setValue(37, TaskParam.INSTANCE.getnXYNullSpeed());
        task.setValue(38, TaskParam.INSTANCE.getnZNullSpeed());
        task.setValue(39, TaskParam.INSTANCE.getnUNullSpeed());
        /*===================== 回锡参数设置 =====================*/
        task.setValue(43, TaskParam.INSTANCE.getnBackSnSpeedFir());
        task.setValue(44, TaskParam.INSTANCE.getnBackSnSumFir());
        task.setValue(45, TaskParam.INSTANCE.getnBackSnSpeedSec());
        task.setValue(46, TaskParam.INSTANCE.getnBackSnSumSec());
        task.setValue(47, TaskParam.INSTANCE.getnBackSnSpeedThird());
        task.setValue(48, TaskParam.INSTANCE.getnBackSnSumThird());
        task.setValue(49, TaskParam.INSTANCE.getnBackSnSpeedFour());
        task.setValue(50, TaskParam.INSTANCE.getnBackSnSumSecFour());
        task.setValue(51, TaskParam.INSTANCE.getnSnHeight());
        task.setValue(52, TaskParam.INSTANCE.getnWorkMode());

		/*=====================  end =====================*/


        task.setValue(14, TaskParam.INSTANCE.getnStartX());
        task.setValue(15, (short) ((TaskParam.INSTANCE.getnStartX() >>> 16) & 0x00ff));
        task.setValue(16, TaskParam.INSTANCE.getnStartY());
        task.setValue(17, (short) ((TaskParam.INSTANCE.getnStartY() >>> 16) & 0x00ff));
        task.setValue(18, TaskParam.INSTANCE.getnStartZ());
        task.setValue(19, (short) ((TaskParam.INSTANCE.getnStartZ() >>> 16) & 0x00ff));
        task.setValue(20, TaskParam.INSTANCE.getnStartU());
        task.setValue(21, (short) ((TaskParam.INSTANCE.getnStartU() >>> 16) & 0x00ff));
//		task.setValue(22,
//				(((short) TaskParam.INSTANCE.getnBackSnSumFir() & 0x00ff) << 8) | ((short) TaskParam.INSTANCE.getnBackSnSpeedFir() & 0x00ff));
//		task.setValue(23,
//				(((short) TaskParam.INSTANCE.getnBackSnSumSec() & 0x00ff) << 8) | ((short) TaskParam.INSTANCE.getnBackSnSpeedSec() & 0x00ff));
//		task.setValue(24,
//				(((short) TaskParam.INSTANCE.getnWorkMode() & 0x00ff) << 8) | ((short) TaskParam.INSTANCE.getnSnHeight() & 0x00ff));

        Point pLineStart = null;
        Point pFaceStart = null;
        // 任务点个数
        nNum = 0;
        for (pointCountNum = 0; pointCountNum < num; pointCountNum++) {
            Point p = pointList.get(pointCountNum);
            switch (p.getPointParam().getPointType()) {
                case POINT_WELD_BASE:
                    task.setValue(0, (task.getByte2ShortValue(0) | (1 << 14)));
                    task.setValue(14, p.getX());
                    task.setValue(15, p.getX() >>> 16);
                    task.setValue(16, p.getY());
                    task.setValue(17, p.getY() >>> 16);
                    task.setValue(18, p.getZ());
                    task.setValue(19, p.getZ() >>> 16);
                    task.setValue(20, p.getU());
                    task.setValue(21, p.getU() >>> 16);

                    break;
                case POINT_WELD_INPUT:
                    createTask400WeldInput(p, task);

                    break;
                case POINT_WELD_OUTPUT:
                    createTask400WeldOutput(p, task);

                    break;
                case POINT_WELD_BLOW:
                    createTask400WeldBlow(p, task);

                    break;
                case POINT_WELD_WORK:
                    createTask400GlueAlone(p, task);

                    break;
                case POINT_WELD_LINE_START:
                    createTask400GlueLineStart(p, task);

                    break;
                case POINT_WELD_LINE_MID:
                    createTask400GlueLineMid(pointList, pointCountNum, p, task, _pt);
                    break;
                case POINT_WELD_LINE_END:
                    createTask400GlueLineEnd(pointList, pointCountNum, p, task);
                    break;
                default:
                    break;
            }
        }
        //第22个字节，nNum为图元个数
        task.setValue(11, nNum);

        short temp = vector2ShortXOR(task);
        task.pushBack(temp);

        return task.size();
    }

    /**
     * @param _buf      需解析的任务数据流
     * @param _bufLen   需解析的任务数据流长度
     * @param _pointMgr 解析出的任务点集数据
     * @return true:解析成功 false:解析失败
     * @Title: analyseTask400
     * @Description: 解析任务数据流
     */
    public boolean analyseTask400(byte[] _buf, int _bufLen, List<Point> _pointMgr) {
        int nNum = Protocol_400_1.READ2BYTES(_buf, 22);//图元个数
        int primaryOffset = 0;//主偏移量

        int val = Protocol_400_1.READ2BYTES(_buf, 0);
        TaskParam.INSTANCE.setAllParamBacktoDefault();
        TaskParam.INSTANCE.setnSpeedCurve(val >>> 15);

        TaskParam.INSTANCE.setnAccelerate(Protocol_400_1.READ2BYTES(_buf, 66));
        TaskParam.INSTANCE.setnDecelerate(Protocol_400_1.READ2BYTES(_buf, 68));
        TaskParam.INSTANCE.setnTurnSpeed(Protocol_400_1.READ2BYTES(_buf, 70));

        TaskParam.INSTANCE.setnXYNullSpeed(Protocol_400_1.READ2BYTES(_buf, 74));
        TaskParam.INSTANCE.setnZNullSpeed(Protocol_400_1.READ2BYTES(_buf, 76));
        TaskParam.INSTANCE.setnUNullSpeed(Protocol_400_1.READ2BYTES(_buf, 78));

        TaskParam.INSTANCE.setnBackSnSpeedFir(Protocol_400_1.READ2BYTES(_buf, 86));
        TaskParam.INSTANCE.setnBackSnSumFir(Protocol_400_1.READ2BYTES(_buf, 88));
        TaskParam.INSTANCE.setnBackSnSpeedSec(Protocol_400_1.READ2BYTES(_buf, 90));
        TaskParam.INSTANCE.setnBackSnSumSec(Protocol_400_1.READ2BYTES(_buf, 92));
        TaskParam.INSTANCE.setnBackSnSpeedThird(Protocol_400_1.READ2BYTES(_buf, 94));
        TaskParam.INSTANCE.setnBackSnSumThird(Protocol_400_1.READ2BYTES(_buf, 96));
        TaskParam.INSTANCE.setnBackSnSpeedFour(Protocol_400_1.READ2BYTES(_buf, 98));
        TaskParam.INSTANCE.setnBackSnSumSecFour(Protocol_400_1.READ2BYTES(_buf, 100));
        TaskParam.INSTANCE.setnSnHeight(Protocol_400_1.READ2BYTES(_buf, 102));
        TaskParam.INSTANCE.setnWorkMode(Protocol_400_1.READ2BYTES(_buf, 104));
//		TaskParam.INSTANCE.setnTurnAccelerateMax(Protocol_400_1.READ2BYTES(_buf, 82));
        if ((val & 0x4000) > 0) {//判断有无基准点0：无基准点，1：有基准点
            if (isDH) {
				/*焊锡机*/
                Point pt = new Point(PointType.POINT_WELD_BASE);
                pt.setX(Protocol_400_1.READ4BYTES(_buf, 28));
                pt.setY(Protocol_400_1.READ4BYTES(_buf, 32));
                pt.setZ(Protocol_400_1.READ4BYTES(_buf, 36));
                pt.setU(Protocol_400_1.READ4BYTES(_buf, 40));
                _pointMgr.add(pt);
            } else {
				/*点胶机*/
                Point pt = new Point(PointType.POINT_GLUE_BASE);
                pt.setX(Protocol_400_1.READ4BYTES(_buf, 28));
                pt.setY(Protocol_400_1.READ4BYTES(_buf, 32));
                pt.setZ(Protocol_400_1.READ4BYTES(_buf, 36));
                pt.setU(Protocol_400_1.READ4BYTES(_buf, 40));
                _pointMgr.add(pt);
            }
        }
        Point pLineStart = null;
        Point pFaceStart = null;
        PointWeldLineEndParam pParamEnd = null;
        //C++源码为: BYTE* buf = (BYTE*)(_buf +110);
        // 需解析的任务数据流(前110个字节为任务头信息)
        primaryOffset = 110;
//		String strBuf = _buf.toString();
//		String strBuf2 = strBuf.substring(110);
//		byte[] buf = strBuf2.getBytes();
        byte[] buf = _buf;
        for (int i = 0; i < nNum; i++) {

            PointInfo400 info = new PointInfo400();
            info.setPointInfo(buf, primaryOffset);
            if (info.getFlag() == 1) {
                primaryOffset += info.getLen();
                continue;
            }

            switch (info.getPointType()) {
                case 0:
                    //作业点
                    if (isDH) {
                        Point pt = new Point(PointType.POINT_WELD_WORK);
                        PointWeldWorkParam pParam = (PointWeldWorkParam) pt.getPointParam();
                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
                        pParam.setSn(info.getIfSn());
                        pParam.setOut(info.getIfOut());
                        pParam.setSus(info.getifSus());
                        pParam.setPause(info.getIfPause());
                        pParam.setSendSnSpeedFir(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26));//一次送锡速度
					    pParam.setSendSnSumFir((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 28)+0.5));//一次送锡量
					    pParam.setPreHeatTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 30));//预热时间
					    pParam.setSendSnSpeedSec(Protocol_400_1.READ2BYTES(buf, primaryOffset, 32));//二次送锡速度
					    pParam.setSendSnSumSec((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 34)+0.5));//二次送锡量
					    pParam.setStopSnStimeSec(Protocol_400_1.READ2BYTES(buf, primaryOffset, 36));//二次停锡时间
					    pParam.setSendSnSpeedThird(Protocol_400_1.READ2BYTES(buf, primaryOffset, 38));//三次送锡速度
					    pParam.setSendSnSumThird((int)(Protocol_400_1.READ2BYTES(buf, primaryOffset, 40)+0.5));//三次送锡量
					    pParam.setStopSnTimeThird(Protocol_400_1.READ2BYTES(buf, primaryOffset, 42));//三次停锡时间
					    pParam.setSendSnSpeedFourth(Protocol_400_1.READ2BYTES(buf, primaryOffset, 44));//四次送锡速度
					    pParam.setSendSnSumFourth((int)(Protocol_400_1.READ2BYTES(buf, primaryOffset, 46)+0.5));//四次送锡量
					    pParam.setStopSnTimeFourth(Protocol_400_1.READ2BYTES(buf, primaryOffset, 48));//四次停锡时间
					    pParam.setDipDistance(Protocol_400_1.READ2BYTES(buf, primaryOffset, 50));//倾斜距离
					    pParam.setUpHeight((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 52) + 0.5));
                        _pointMgr.add(pt);
                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_ALONE);
//                        PointGlueAloneParam pParam = (PointGlueAloneParam) pt.getPointParam();
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//
//                        int tempY = 0;
//                        int tempZ = 0;
//                        int tempSpeed = 0;
//                        tempY = Protocol_400_1.READ1BYTE(buf, primaryOffset, 22);
//                        tempZ = Protocol_400_1.READ1BYTE(buf, primaryOffset, 23);
//                        tempSpeed = Protocol_400_1.READ2BYTES(buf, primaryOffset, 24);
////					pParam.nDipDistanceY = tempY;
////					pParam.nDipDistanceZ = tempZ;
////					pParam.nDipSpeed = tempSpeed;
//                        pParam.setnDipDistanceY(tempY);
//                        pParam.setnDipDistanceZ(tempZ);
//                        pParam.setnDipSpeed(tempSpeed);
//
//                        pParam.setPause(info.getIfPause());
//                        // C++源码为 SetGWOutput(*info, pt.sGlue);
//                        pParam.setGluePort(info.reverseBytePerBit(info.getIOPort()));
//
//                        pParam.setStopGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26));
//                        pParam.setDotGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 28));
//                        int upHeightPulse = Protocol_400_1.READ4BYTES(buf, primaryOffset, 30);
//                        pParam.setUpHeight(
//                                (int) (RobotParam.INSTANCE.ZPulse2Journey(upHeightPulse)
//                                        + 0.5));
//                        _pointMgr.add(pt);
                    }
                    break;
                case 1:
                    //线起始点
                    if (isDH) {
                        Point pt = new Point(PointType.POINT_WELD_LINE_START);
                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
					PointWeldLineStartParam pParam = (PointWeldLineStartParam) pt.getPointParam();
                        pParam.setSn(info.getIfSn());
					pParam.setMoveSpeed((int)(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26)+0.5));
					pParam.setSnSpeed((int)( Protocol_400_1.READ2BYTES(buf, primaryOffset, 28)+0.5));
					pParam.setPreSendSnSpeed(Protocol_400_1.READ2BYTES(buf, primaryOffset, 30));
					pParam.setPreSendSnSum((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 32)+0.5));
					pParam.setPreHeatTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 34));
                        _pointMgr.add(pt);
                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_LINE_START);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//
//                        PointGlueLineStartParam pParam = (PointGlueLineStartParam) pt.getPointParam();
//                        pParam.setTimeMode(info.getTimeMode() > 0 ? true : false);
//                        pParam.setMoveSpeed(Protocol_400_1.READ4BYTES(buf, primaryOffset, 26));
//                        pParam.setOutGlueTimePrev(Protocol_400_1.READ2BYTES(buf, primaryOffset, 30));
//                        pParam.setOutGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 32));
//                        pParam.setGluePort(info.reverseBytePerBit(info.getIOPort()));
//                        _pointMgr.add(pt);
//
//                        pLineStart = _pointMgr.get(_pointMgr.size() - 1);
                    }
                    break;
                case 2:
                    //中间点
                    int flag = info.getFlag();
                    if (info.getFlag() == 2) {//变化点
                        if (isDH) {
                            Point pt = new Point(PointType.POINT_WELD_LINE_END);
                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
                            PointWeldLineEndParam pParam = (PointWeldLineEndParam) pt.getPointParam();
                            pParam.setPause(info.getIfPause());
						    pParam.setStopSnTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26));
						    pParam.setUpHeight(Protocol_400_1.READ2BYTES(buf, primaryOffset, 28));
                            _pointMgr.add(pt);
                        } else {
//                            Point pt = new Point(PointType.POINT_GLUE_LINE_END);
//                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//
//                            pParamEnd = (PointGlueLineEndParam) pt.getPointParam();
//                            pParamEnd.setPause(info.getIfPause());
////						pParamEnd.setStopGlueTimePrev(Protocol_400_1.READ2BYTES(buf, primaryOffset, 36));
//                            pParamEnd.setStopGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 36));
//                            pParamEnd.setUpHeight((int) (RobotParam.INSTANCE.ZPulse2Journey(Protocol_400_1.READ4BYTES(buf, primaryOffset, 38)) + 0.5));
//
//                            pParamEnd.setBreakGlueLen(Protocol_400_1.READ2BYTES(buf, primaryOffset, 42));
//                            pParamEnd.setDrawDistance(Protocol_400_1.READ2BYTES(buf, primaryOffset, 44));
//                            pParamEnd.setDrawSpeed(Protocol_400_1.READ2BYTES(buf, primaryOffset, 46));
//
//                            _pointMgr.add(pt);

                        }
                    } else if (info.getFlag() == 3) {//变化点
                        if (isDH) {
                            Point pt = new Point(PointType.POINT_WELD_LINE_MID);
                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
						PointWeldLineMidParam pParam = (PointWeldLineMidParam) pt.getPointParam();
                            pParam.setSn(info.getIfSn());
						pParam.setMoveSpeed((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 26)+0.5));
						pParam.setSendSnSpeed((int)( Protocol_400_1.READ2BYTES(buf, primaryOffset, 28)+0.5));
						pParam.setStopSnTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 30));
                            _pointMgr.add(pt);
                        } else {
//                            Point pt = new Point(PointType.POINT_GLUE_LINE_MID);
//                            PointGlueLineMidParam pParam = (PointGlueLineMidParam) pt.getPointParam();
//                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6)
//                                    + Protocol_400_1.READ2BYTES(buf, primaryOffset, 36)
//                                    - OFFSET_VAL);
//                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10)
//                                    + Protocol_400_1.READ2BYTES(buf, primaryOffset, 38)
//                                    - OFFSET_VAL);
//                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14)
//                                    + Protocol_400_1.READ2BYTES(buf, primaryOffset, 40)
//                                    - OFFSET_VAL);
//                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18)
//                                    + Protocol_400_1.READ2BYTES(buf, primaryOffset, 42)
//                                    - OFFSET_VAL);
//                            // C++源码为 SetGWOutput(*info, pt.sGlue);
//                            pParam.setGluePort(info.reverseBytePerBit(info.getIOPort()));
//                            pParam.setMoveSpeed(Protocol_400_1.READ4BYTES(buf, primaryOffset, 26));
//                            pParam.setRadius(Protocol_400_1.READ2BYTES(buf, primaryOffset, 44));
//                            pParam.setRadius(pParam.getRadius() / 100);//下载的时候扩大了10倍，上传的时候缩小10倍
//                            pParam.setStopGlueDisPrev(Protocol_400_1.READ2BYTES(buf, primaryOffset, 46));
//                            pParam.setStopGlueDisPrev(pParam.getStopGlueDisPrev() / 100);//下载的时候扩大了10倍，上传的时候缩小10倍
//                            pParam.setStopGLueDisNext(Protocol_400_1.READ2BYTES(buf, primaryOffset, 48));
//                            pParam.setStopGLueDisNext(pParam.getStopGLueDisNext() / 100);//下载的时候扩大了10倍，上传的时候缩小10倍
//
//                            short sGlue = (short) Protocol_400_1.READ2BYTES(buf, primaryOffset, 50);
//                            boolean gluePort[] = pParam.getGluePort();
//                            if (sGlue != 0) {
//                                int j;
//                                for (j = 0; j < USER_O_NO_NUM; j++) {
//                                    gluePort[j] = (((sGlue >> j) & 0x01) > 0) ? true : false;
//                                }
//                            }
//                            _pointMgr.add(pt);
                        }
                    } else if (info.getFlag() == 0) {
                        if (isDH) {
                            Point pt = new Point(PointType.POINT_WELD_LINE_MID);
                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
                            PointWeldLineMidParam pParam = (PointWeldLineMidParam) pt.getPointParam();
                            pParam.setSn(info.getIfSn());
                            pParam.setMoveSpeed((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 26)+0.5));
                            pParam.setSendSnSpeed((int)( Protocol_400_1.READ2BYTES(buf, primaryOffset, 28)+0.5));
                            pParam.setStopSnTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 30));
                            _pointMgr.add(pt);
                        } else {
//                            Point pt = new Point(PointType.POINT_GLUE_LINE_MID);
//                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                            PointGlueLineMidParam pParam = (PointGlueLineMidParam) pt.getPointParam();
//                            // C++源码为 SetGWOutput(*info, pt.sGlue);
//                            pParam.setGluePort(info.reverseBytePerBit(info.getIOPort()));
//                            pParam.setMoveSpeed(Protocol_400_1.READ4BYTES(buf, primaryOffset, 26));
//                            _pointMgr.add(pt);
                        }
                    } else if (info.getFlag() == 1) {
                        if (isDH) {
                            Point pt = new Point(PointType.POINT_WELD_LINE_MID);
                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
                            PointWeldLineMidParam pParam = (PointWeldLineMidParam) pt.getPointParam();
                            pParam.setSn(info.getIfSn());
                            pParam.setMoveSpeed((int) (Protocol_400_1.READ2BYTES(buf, primaryOffset, 26)+0.5));
                            pParam.setSendSnSpeed((int)( Protocol_400_1.READ2BYTES(buf, primaryOffset, 28)+0.5));
                            pParam.setStopSnTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 30));
                            _pointMgr.add(pt);
                        } else {
//                            Point pt = new Point(PointType.POINT_GLUE_LINE_MID);
//                            pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                            pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                            pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                            pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                            PointGlueLineMidParam pParam = (PointGlueLineMidParam) pt.getPointParam();
//                            // C++源码为 SetGWOutput(*info, pt.sGlue);
//                            pParam.setGluePort(info.reverseBytePerBit(info.getIOPort()));
//                            pParam.setMoveSpeed(Protocol_400_1.READ4BYTES(buf, primaryOffset, 26));
//                            _pointMgr.add(pt);
                        }
                    }
                    break;
//                case 3:
//                    //圆弧点
//                    if (isDH) {
//                        Point pt = new Point(PointType.POINT_WELD_LINE_ARC);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                        _pointMgr.add(pt);
//                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_LINE_ARC);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                        _pointMgr.add(pt);
//                    }
//                    break;
                case 4:
                    //线结束点
                    if (isDH) {
                        Point pt = new Point(PointType.POINT_WELD_LINE_END);
                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
					    PointWeldLineEndParam pParam = (PointWeldLineEndParam) pt.getPointParam();
                        pParam.setPause(info.getIfPause());
                        pParam.setStopSnTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26));
                        pParam.setUpHeight(Protocol_400_1.READ2BYTES(buf, primaryOffset, 28));
                        _pointMgr.add(pt);
                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_LINE_END);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                        PointGlueLineEndParam pParam = (PointGlueLineEndParam) pt.getPointParam();
//                        pParam.setPause(info.getIfPause());
//                        pParam.setStopGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26));
//                        pParam.setUpHeight((int) (RobotParam.INSTANCE.ZPulse2Journey(Protocol_400_1.READ4BYTES(buf, primaryOffset, 28)) + 0.5));
//                        pt.setPointParam(pParam);
////					pParamEnd = null;
//                        _pointMgr.add(pt);
//                        if (pLineStart != null) {//将废弃
//                            PointGlueLineStartParam pLineStartParam = (PointGlueLineStartParam) pLineStart.getPointParam();
////						pLineStartParam.setStopGlueTime(pParam.getStopGlueTime());
////						pLineStartParam.setUpHeight(pParam.getUpHeight());
//                            pLineStartParam.setBreakGlueLen(pParam.getBreakGlueLen());
//                            pLineStartParam.setDrawDistance(pParam.getDrawDistance());
//                            pLineStartParam.setDrawSpeed(pParam.getDrawSpeed());
//                            pLineStart = null;
//                        }
                    }
                    break;
//                case 5:
//                    //面起点
//                    if (isDH) {
//                        //C++源码为 ASSERT(0);
//                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_FACE_START);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                        PointGlueFaceStartParam pParam = (PointGlueFaceStartParam) pt.getPointParam();
//                        pParam.setOutGlue(info.getSurfaceChangeDrop() > 0 ? true : false);
//                        pParam.setStartDir(info.getSurfaceDir() == 0 ? true : false);
//                        // C++源码为 SetGWOutput(*info, pt.sGlue);
//                        pParam.setGluePort(info.reverseBytePerBit(info.getIOPort()));
//                        pParam.setMoveSpeed(Protocol_400_1.READ4BYTES(buf, primaryOffset, 22));
//                        pParam.setOutGlueTimePrev(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26));
//                        pParam.setOutGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 28));
//                        _pointMgr.add(pt);
//
//                        pFaceStart = _pointMgr.get(_pointMgr.size() - 1);
//                    }
//                    break;
//                case 6:
//                    //面终点
//                    if (isDH) {
//                        //C++源码为 ASSERT(0);
//                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_FACE_END);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                        PointGlueFaceEndParam pParam = (PointGlueFaceEndParam) pt.getPointParam();
//                        pParam.setPause(info.getIfPause());
////					C++源码：pt.nStartDir = (EFaceDirection)info->surfaceDir;
////					pParam.setStartDir(info.getSurfaceDir());
//                        pParam.setLineNum(Protocol_400_1.READ2BYTES(buf, primaryOffset, 22));
//                        pParam.setStopGlueTime(Protocol_400_1.READ2BYTES(buf, primaryOffset, 24));
//                        pParam.setUpHeight((int) (RobotParam.INSTANCE.ZPulse2Journey(Protocol_400_1.READ2BYTES(buf, primaryOffset, 26)) + 0.5));
//                        _pointMgr.add(pt);
//
//                        if (pFaceStart != null) {//将废弃
//                            PointGlueFaceStartParam pFaceStartParam = (PointGlueFaceStartParam) pFaceStart.getPointParam();
////						C++源码:pFaceStart->nStartDir = pt.nStartDir;已废弃
////						pFaceStartParam.setStartDir(pParam.getStartDir);
////						pFaceStartParam.setLineNum(pParam.getLineNum());
//                            pFaceStartParam.setStopGlueTime(pParam.getStopGlueTime());
////						pFaceStartParam.setUpHeight(pParam.getUpHeight());
//                            pFaceStart = null;
//                        }
//                    }
//                    break;
                case 7:
                    if (isDH) {
                        //ASSERT(0);
                    } else {
                        if (info.getIoFlag() == 1) {
                            //输入IO
                            Point pt = new Point(PointType.POINT_WELD_INPUT);
                            PointWeldInputIOParam pParam = (PointWeldInputIOParam) pt.getPointParam();
                            pParam.setGoTimePrev(Protocol_400_1.READ2BYTES(buf, primaryOffset, 6));
                            pParam.setGoTimeNext(Protocol_400_1.READ2BYTES(buf, primaryOffset, 8));
                            int temp = Protocol_400_1.READ2BYTES(buf, primaryOffset, 3);
                            boolean[] ports = info.getOutputPort(temp);
                            pParam.setInputPort(ports);
                            _pointMgr.add(pt);
                        } else {
                            //输出IO
                            Point pt = new Point(PointType.POINT_WELD_OUTPUT);
                            PointWeldOutputIOParam pParam = (PointWeldOutputIOParam) pt.getPointParam();
                            pParam.setGoTimePrev(Protocol_400_1.READ2BYTES(buf, primaryOffset, 6));
                            pParam.setGoTimeNext(Protocol_400_1.READ2BYTES(buf, primaryOffset, 8));
                            int temp = Protocol_400_1.READ2BYTES(buf, primaryOffset, 4) >> 4;
                            boolean[] ports = info.getOutputPort(temp);
                            pParam.setInputPort(ports);
                            _pointMgr.add(pt);
                        }
                    }
                    break;
//                case 11://清胶点
//                    if (isDH) {
//					/**/
//                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_CLEAR);
//                        pt.setX(Protocol_400_1.READ4BYTES(buf, primaryOffset, 6));
//                        pt.setY(Protocol_400_1.READ4BYTES(buf, primaryOffset, 10));
//                        pt.setZ(Protocol_400_1.READ4BYTES(buf, primaryOffset, 14));
//                        pt.setU(Protocol_400_1.READ4BYTES(buf, primaryOffset, 18));
//                        PointGlueClearParam pParam = (PointGlueClearParam) pt.getPointParam();
//                        pParam.setClearGlueTime(Protocol_400_1.READ4BYTES(buf, primaryOffset, 26));
//
//                        _pointMgr.add(pt);
//                    }
//                    break;
//                case 12://清胶
//                    if (isDH) {
//					/**/
//                    } else {
//                        Point pt = new Point(PointType.POINT_GLUE_CLEARIO);
//
//                        _pointMgr.add(pt);
//                    }
//                    break;
            }
            primaryOffset += info.getLen();
        }
        return true;
    }

    /**
     * @param task 任务数据流
     * @return XOR校验值
     * @Title: vector2ShortXOR
     * @Description: 获取任务数据流XOR校验值
     */
    private short vector2ShortXOR(TaskDataStream task) {
        Object[] srcByteData = task.byteTask.toArray();
        short XORValue = DataCheckout.byteToXOR(srcByteData, 0, srcByteData.length);
        XORValue &= 0x00ff;
        srcByteData = null;
        return XORValue;
    }

    /**
     * @param pointList 点集信息
     * @Title: taskDownload
     * @Description: 任务下载
     */
    public void taskDownload(List<Point> pointList) {
        this.mPointList = pointList;
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_DownLoad;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @param pointList 点集信息
     * @Description: 任务模拟
     */
    public void taskDownloadDemo(List<Point> pointList) {
        this.mPointList = pointList;
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Demo;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @Title: moveCoord
     * @Description: 示教
     */
    public void moveCoord() {
        // orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Move);
        // writeData(buffer, orderLength);
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Move;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @Title: stopCoord
     * @Description: 停止
     */
    public void stopCoord() {
        // orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Stop);
        // writeData(buffer, orderLength);
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Stop;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @Title: resetCoord
     * @Description: 复位
     */
    public void resetCoord() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
//		stepCmd[1] = CmdParam.Cmd_Reset;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @Title: resetCoord
     * @Description: 复位
     */
    public void resetCoordDirect() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Reset;
        step = 0;
        writeData(buffer, orderLength);
    }


    /**
     * @Title: setCurCoord
     * @Description: 定位
     */
    public void setCurCoord() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Locate;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @Title: getCurCoord
     * @Description: 获取当前坐标
     */
    public void getCurCoord() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Coord;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @Title setTrackCoord
     * @Description 循迹定位
     * @deprecated
     */
    public void setTrackCoord() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Track;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @return 请求数据的长度
     * @Title: isBusy
     * @Description: 请求
     */
    private int isBusy() {
        buffer = new byte[ORDER_BUFFER_LENTH];
        return protocol.CreaterOrder(buffer, CmdParam.Cmd_Ask);
    }

    /**
     * @Title: getMachineParam
     * @Description: 获取机器参数
     */
    public void getMachineParam() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Device;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @param buffer 数据
     * @return 校验值
     * @Title: checkReturnBuffer
     * @Description: 检查收到的数据
     */
    private int checkReturnBuffer(byte[] buffer) {
        switch (buffer.length) {
            case 5:
            case 6:// 询问(忙信号)错误数据
                cmd = CmdParam.Cmd_Null;
                break;
            case 8: {
                int temp = (((int) (buffer[2] & 0x00ff) << 8) | ((int) (buffer[3] & 0x00ff)));
                cmd = CmdParam.ret8ByteDataCmd(buffer, temp);
            }
            break;
            case 10: {//写功能列表响应的字节
                int temp = (((int) (buffer[2] & 0x00ff) << 8) | ((int) (buffer[3] & 0x00ff)));
                cmd = CmdParam.ret8ByteDataCmd(buffer, temp);
            }
            default:
                switch (buffer[2]) {
                    case 0x32://获取读取功能列表响应的字节数字段1个字节
                        cmd = CmdParam.Cmd_Read_Funclist;
                        break;
                    case 0x4E://获取设备信息响应的字节数字段1个字节
                        cmd = CmdParam.Cmd_Device;
                        break;
                    case 0x1A://获取当前点坐标响应的字节数字段1个字节
                        cmd = CmdParam.Cmd_Coord;
                        break;
                    case 0x06://读任务预处理响应的字节数字段1个字节
                        cmd = CmdParam.Cmd_PreUpLoad;
                        break;
                    case 0x05://编程器发完命令，DSP回应后，编程器再发程序数据，写程序升级响应的子码字段1个字节
                        cmd = CmdParam.Cmd_UpdateDSP;
                        break;
                    case 0x79: {
                        switch (buffer[3]) {
                            case (byte) 0xE4://读取任务数据的寄存器地址字段两个字节
                                cmd = CmdParam.Cmd_UpLoad;
                                break;
                            case (byte) 0xE5://读取任务数据(接收错误，任务数据重发指令)的寄存器地址字段
                                cmd = CmdParam.Cmd_UpLoadRetry;
                                break;
                            case (byte) 0xE6://任务上传结束的寄存器地址字段
                                cmd = CmdParam.Cmd_UpLoadFail;
                                break;
                        }
                    }

                }
                break;
        }
        return protocol.CheckData(buffer, cmd);
    }
    // -----------------------------------

    /**
     * @param pointList 点集信息
     * @Title: taskUpload
     * @Description: 任务上传
     */
    public void taskUpload(List<Point> pointList) {
        this.mPointList = pointList;
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_PreUpLoad;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * 重新上传上一包数据
     */
    private void uploadRetry() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_UpLoadRetry;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * 上传完成
     */
    private void uploadFail() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_UpLoadFail;
        step = 0;
        cmdDelayFlag = CmdParam.Cmd_UpLoadFail;
        writeData(buffer, orderLength);
    }

    /**
     * 正在上传处理包
     */
    private void upload() {
        if (revTaskSize < upLoadLen) {
            upLoadLen = revTaskSize;
        }
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_UpLoad;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * 检查任务是否存在
     */
    public void isTaskExist() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_IsTaskExist;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * 获取下位机功能列表
     */
    public void getFunclist() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Read_Funclist;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * 写入下位机功能列表
     */
    public void writeFunclist() {
        orderLength = isBusy();
        stepCmd[0] = CmdParam.Cmd_Ask;
        stepCmd[1] = CmdParam.Cmd_Write_Funclist;
        step = 0;
        writeData(buffer, orderLength);
    }

    /**
     * @param revBuffer 获取到的数据
     * @return 校验值
     * @Title: managingMessage
     * @Description: 对收到数据进行校验以及后续操作
     */
    public int managingMessage(byte[] revBuffer) {
		/*------------------- 0:错误 1:正确 其他:错误码 -------------------*/
        int retValue = checkReturnBuffer(revBuffer);
        if (isUploading == true) {
            if (upLoadFlag == true) {
                //校验并处理任务上传数据包
                if (revBuffer.length != 8) {
                    if (revBuffer[upLoadLen + 8] == DataCheckout.byteToXOR(revBuffer, 8, upLoadLen)) {
                        for (int i = 0; i < upLoadLen; i++) {
                            revTaskBuffer.add((Byte) revBuffer[i + 8]);
                        }
                        // 数据包个数统计
                        revTaskSize -= upLoadLen;
                        if (revTaskSize > 0) {
                            upload();// 上传下一包
                            return UPLOADING;
                        } else {
                            uploadFail();// 上传完成
                            upLoadFlag = false;
                            upLoadSuccess = true;
                            return UPLOAD_SUCCESS;
                        }

                    } else {
                        // 校验失败相关处理,逻辑为校验失败后再重发三次，若三次均校验不成功则发送上传失败命令停止上传
                        // 若三次内校验成功则继续进行上传
                        upLoadRetryTimes = 3;// 设置重发次数
                        uploadRetry();
                    }
                    upLoadFlag = false;
                    return UPLOADING;
                } else {
                    //等于8的时候,下载一包之后的询问命令
                    if (step == 0) {
                        step = 1;
                        buffer = new byte[ORDER_BUFFER_LENTH];
                        switch (stepCmd[step]) {
                            case Cmd_UpLoad:// 任务上传，发下一包数据
                                cmdDelayFlag = CmdParam.Cmd_UpLoad;// 让connection判断是否需要延长数据接收时间
                                orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_UpLoad);
                                break;
                            case Cmd_UpLoadRetry:// 任务上传重发
                                orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_UpLoadRetry);
                                break;
                            case Cmd_UpLoadFail:// 任务上传完成或失败，停止上传
                                orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_UpLoadFail);
                                break;
                            default:
                                break;
                        }
                        writeData(buffer, orderLength);
                    }
                    return UPLOADING;
                }
            } else if (upLoadRetryFlag == true) {
                //校验并处理上传重发数据包
                if (upLoadRetryTimes > 0) {
                    if (revBuffer[upLoadLen + 8] == DataCheckout.byteToXOR(revBuffer, 8, upLoadLen)) {
                        for (int i = 0; i < upLoadLen; i++) {
                            revTaskBuffer.add((Byte) revBuffer[i + 8]);
                        }
                        //数据包个数统计
                        revTaskSize -= upLoadLen;
                        if (revTaskSize > 0) {
                            upload();//上传下一包
                        } else {
                            uploadFail();//上传完成
                        }
                    } else {
                        //校验失败相关处理
                        upLoadRetryTimes--;
                        uploadRetry();//重发次数未到3次可继续发重发命令
                    }
                } else {//超过三次重发则停止上传，说明上传失败
                    upLoadSuccess = false;//上传失败标记
                    uploadFail();//校验失败，停止上传
                }
                upLoadRetryFlag = false;
                return UPLOADING;
            } else {
                // 任务上传相关逻辑
                if (retValue == 1) {
                    //响应的第三四位字节
                    int cmdFlag = ((revBuffer[2] & 0x00ff) << 8) | (revBuffer[3] & 0x00ff);
                    if (step == 0) {
                        step = 1;
                        buffer = new byte[ORDER_BUFFER_LENTH];
                        switch (stepCmd[step]) {
                            case Cmd_UpLoad:// 任务上传，发下一包数据
                                cmdDelayFlag = CmdParam.Cmd_UpLoad;
                                orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_UpLoad);
                                break;
                            case Cmd_UpLoadRetry:// 任务上传重发
                                orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_UpLoadRetry);
                                break;
                            case Cmd_UpLoadFail:// 任务上传完成或失败，停止上传
                                orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_UpLoadFail);
                                break;
                            default:
                                break;
                        }
                        writeData(buffer, orderLength);
                    }
                    // -----------------------收到的长数据处理-----------------------
                    else if (revBuffer[2] == 0x06) {// Cmd_PreUpLoad返回读取任务预处理
                        revTaskSize = Protocol_400_1.READ4BYTES_R(revBuffer, 5);// 获取任务长度

                        // 检查下位机状态 略
                        // ...
                        // revTaskBuffer = new byte[revTaskSize +
                        // 2];//多申请两位用于接收校验位
                        // byte[] pBuf = revTaskBuffer;
                        upLoadLen = 600;// 600字节一包
                        if (revTaskSize < upLoadLen) {
                            upLoadLen = revTaskSize;
                        }
                        upload();// 开始进入数据分包上传步骤
                    } else if (cmdFlag == 0x79E4) {// upLoad上传数据
                        // 标志位设置正在接受upLoad数据包
                        upLoadFlag = true;
                        upLoadRetryFlag = false;
                        return managingMessage(revBuffer);
                    } else if (cmdFlag == 0x79E5) {// uploadRetry上传错误重发
                        // 标志位设置正在接受uploadRetry数据包
                        upLoadFlag = false;
                        upLoadRetryFlag = true;
                    } else if (cmdFlag == 0x79E6) {// uploadFail或者上传完成
                        if (upLoadSuccess == true) {
                            byte[] taskData = new byte[revTaskBuffer.size()];
                            for (int i = 0; i < revTaskBuffer.size(); i++) {// ArrayList转换byte[]
                                taskData[i] = revTaskBuffer.get(i);
                            }
                            analyseTask400(taskData, revTaskSize, mPointList);// 解析上传的任务
                            isUploading = false;
                            upLoadFlag = false;
                            upLoadRetryFlag = false;
                            upLoadSuccess = false;
                            upLoadRetryTimes = 0;
                            revTaskSize = 0;
                            revTaskBuffer.clear();
                            return UPLOAD_SUCCESS;
                        } else {
                            isUploading = false;
                            upLoadFlag = false;
                            upLoadRetryFlag = false;
                            upLoadSuccess = false;
                            upLoadRetryTimes = 0;
                            revTaskSize = 0;
                            revTaskBuffer.clear();
                            return UPLOAD_FAILURE;// 上传失败
                        }
                    }

                    return UPLOADING;// 返回任务上传中标志
                } else {
                    return retValue;// 返回错误码
                }

            }
        } else {//任务下载及其他相关通信逻辑
            if (retValue == 1) {
                int cmdFlag = ((revBuffer[2] & 0x00ff) << 8) | (revBuffer[3] & 0x00ff);
                if (step == 0) {
                    step = 1;
                    buffer = new byte[ORDER_BUFFER_LENTH];
                    switch (stepCmd[step]) {
                        case Cmd_Reset:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Reset);
                            break;
                        case Cmd_Move:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Move);
                            break;
                        case Cmd_Stop:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Stop);
                            break;
                        case Cmd_Coord:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Coord);
                            break;
                        case Cmd_Locate:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Locate);
                            break;
                        case Cmd_Device:
                            cmdDelayFlag = CmdParam.Cmd_Device;// 让connection判断是否需要延长数据接收时间
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Device);
                            break;
                        case Cmd_Read_Funclist:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Read_Funclist);
                            break;
                        case Cmd_Write_Funclist:
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Write_Funclist);
                            break;
                        case Cmd_DownLoad:
                            TaskDataStream task400 = new TaskDataStream();
                            Log.d(TAG, "下载1:" + DateUtil.getCurrentTime());
                            size = createTask400(mPointList, mPointList.size(), task400);
                            Log.d(TAG, "下载2:" + DateUtil.getCurrentTime());
                            Object[] temp = task400.getByteTask().toArray();
                            data = new byte[size];
                            for (int i = 0; i < size; i++) {
                                data[i] = (Byte) temp[i];
                            }
                            OrderParam.INSTANCE.setAllParamToZero();
                            //任务数据长度（字节）
                            OrderParam.INSTANCE.setnDataLen(data.length);
                            OrderParam.INSTANCE.setnTaskNum(TaskParam.INSTANCE.getnTaskNum());
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_DownLoad);
//						byte[] temp = buffer;
                            break;
                        case Cmd_PreUpLoad: {
                            isUploading = true;
                            cmdDelayFlag = CmdParam.Cmd_PreUpLoad;
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_PreUpLoad);
                        }
                        break;
                        case Cmd_Demo://模拟
                            TaskDataStream task401 = new TaskDataStream();
                            size = createTask400(mPointList, mPointList.size(), task401);
                            Object[] temp1 = task401.getByteTask().toArray();
                            data = new byte[size];
                            for (int i = 0; i < size; i++) {
                                data[i] = (Byte) temp1[i];
                            }
//						OrderParam.INSTANCE.setAllParamToZero();
                            OrderParam.INSTANCE.setnDataLen(data.length);
                            OrderParam.INSTANCE.setnTaskNum(121);
                            OrderParam.INSTANCE.setnSpeed(TaskParam.INSTANCE.getnDemoSpeed());
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Demo);
                            break;
                        case Cmd_IsTaskExist:
//						OrderParam.INSTANCE.setnTaskNum(nTaskNum);
                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_IsTaskExist);
                            break;
                        case Cmd_Track:

                            orderLength = protocol.CreaterOrder(buffer, CmdParam.Cmd_Track);
                            break;
                        default:
                            break;
                    }
                    writeData(buffer, orderLength);
					/*=================== begin ===================*/
                    stepCmd[1] = CmdParam.Cmd_Null;
					/*===================  add  ===================*/
                }
                //-----------------------收到的长数据处理-----------------------
                else if (cmdFlag == 0x7938) {// 若是示教停止或者复位,再发送获取坐标命令
                    getCurCoord();// 发送获取
                } else if (revBuffer[2] == 0x4E) {// 获取下位机参数
                    RobotParam.INSTANCE.InitRobot(revBuffer);
                    cmdDelayFlag = CmdParam.Cmd_Null;
                } else if (revBuffer[2] == 0x32) {//获取功能列表参数
                    OrderParam.INSTANCE.InitFunclist(revBuffer);

                } else if (cmdFlag == 0x7952) {//若是任务下载预处理命令返回成功,开始下载任务数据
                    //任务下载
//					short[] log = new short[data.length];
//					String str = "";
//					for(int i = 0; i < data.length; i++){
//						short temp = data[i];
//						temp = (short) (temp & (0x00ff));
//						log[i] = temp;
//						str+=log[i]+","; 
//					}
//					Log.d(TAG, str.toString());
                    cmdDelayFlag = CmdParam.Cmd_DownLoad;
                    step = 0;
                    writeData(data, data.length);
                } else if (cmdFlag == 0x793A) {//任务模拟预处理响应
                    step = 0;
                    writeData(data, data.length);
                }
                return 1;
            } else {
                return retValue;//返回错误码
            }
        }
    }

    /**
     * @param buffer 获取到的坐标数据
     * @return
     * @Title: analyseCurCoord
     * @Description: 解析获取到的点坐标
     */
    public Point analyseCurCoord(byte[] buffer) {
        point.setX(Protocol_400_1.READ4BYTES_R(buffer, 3));
        point.setY(Protocol_400_1.READ4BYTES_R(buffer, 7));
        point.setZ(Protocol_400_1.READ4BYTES_R(buffer, 11));
        point.setU(Protocol_400_1.READ4BYTES_R(buffer, 15));
        return point;
    }

/**
 * @author 李英骑
 * @ClassName: TaskDataStream
 * @Description: 数据任务流类
 * @date 2015年11月26日 下午2:20:35
 * @Companly Mingseal.Ltd
 */
private class TaskDataStream {
    //		private Vector<Short> task;
    private Vector<Byte> byteTask;

    public TaskDataStream() {
//			task = new Vector<Short>();
        byteTask = new Vector<Byte>();
    }

    public Vector<Byte> getByteTask() {
        return byteTask;
    }

//		public Vector<Short> getTask() {
//			return task;
//		}

    /**
     * @Title: clear
     * @Description: 清空数据
     */
    public void clear() {
//			task.clear();
        byteTask.clear();
    }

    /**
     * @param value 数据
     * @Title: pushBack
     * @Description: 添加数据，每次添加两个字节
     */
    public void pushBack(int value) {
//			task.add((short) value);
        byteTask.add((byte) (value & 0x00ff));
        byteTask.add((byte) ((value & 0xff00) >>> 8));
    }

    /**
     * @param value
     * @Title: pushBackByByte
     * @Description: 以字节添加数据
     */
    public void pushBackByByte(int value) {
        byteTask.add((byte) value);
    }

    /**
     * @param location 位置
     * @param value    数据
     * @Title: setValue
     * @Description: 设置相应位置数据，每个参数都是两个字节
     */
    public void setValue(int location, int value) {
//			task.set(location, (short) value);
        byteTask.set(location * 2, (byte) (value & 0x00ff));
        byteTask.set(location * 2 + 1, (byte) ((value & 0xff00) >>> 8));
    }

    /**
     * @param location 位置
     * @param value    数据
     * @Title: setValue
     * @Description: 设置相应位置数据
     */
    public void setValue(int location, short value) {
//			task.set(location, value);
        byteTask.set(location * 2, (byte) (value & 0x00ff));
        byteTask.set(location * 2 + 1, (byte) ((value & 0xff00) >>> 8));
    }

    /**
     * @param location 位置
     * @param value    数据
     * @Title: setValue
     * @Description: 设置相应位置数据
     */
    public void setValueR(int location, short value) {
//			task.set(location, value);
        byteTask.set(location * 2, (byte) ((value & 0xff00) >>> 8));
        byteTask.set(location * 2 + 1, (byte) (value & 0x00ff));
    }


    /**
     * @param location 位置
     * @return 相应位置的值
     * @Title: getByte2ShortValue
     * @Description: 获取指定位置的值
     */
    public short getByte2ShortValue(int location) {
//			return task.get(location);
        byte lowByte = byteTask.get(location * 2);
        byte hightByte = byteTask.get(location * 2 + 1);
        short byte2short = hightByte;
        byte2short = (short) (byte2short << 8);
        byte2short = (short) (byte2short | (lowByte & 0x00ff));
        return byte2short;
    }

    /**
     * @return 数据长度
     * @Title: size
     * @Description: 获取数据长度
     */
    public int size() {
        return byteTask.size();
    }

}

    /**
     * @param src byte[]数组
     * @return short[]数组
     * @Title: byteArray2ShortArray
     * @Description: byte[]数组转成short[]数组
     */
    private short[] byteArray2ShortArray(byte[] src) {
        int shorLength = (int) Math.ceil(src.length / 2);
        short[] dst = new short[shorLength];
        for (int i = 0; i < shorLength; i++) {
            if (i * 2 + 1 <= src.length) {
//				dst[i] = (short) (((src[i * 2] & 0x00ff) << 8) | (src[i * 2 + 1] & 0x00ff));
                dst[i] = (short) (src[i * 2] & 0x00ff);
                dst[i] = (short) (dst[i] << 8 | src[i * 2 + 1] & 0x00ff);
            } else {
                dst[i] = (short) (((src[i * 2] & 0x00ff) << 8) | 0x0000);
            }
        }
        return dst;
    }

    /**
     * @param src short[]数组
     * @return
     * @Title: shortArray2ByteArray
     * @Description: short数组转byte数组, 低字节在前
     */
    private byte[] shortArray2ByteArray(short[] src) {
        int byteLength = src.length * 2;
        byte[] dst = new byte[byteLength];
        for (int i = 0; i < src.length; i++) {
            dst[i * 2 + 1] = (byte) ((src[i] >>> 8) & 0x00ff);
            dst[i * 2] = (byte) (src[i] & 0x00ff);
        }
        return dst;
    }
}
