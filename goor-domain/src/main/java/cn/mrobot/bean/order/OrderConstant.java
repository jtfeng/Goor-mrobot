package cn.mrobot.bean.order;

/**
 * Created by Selim on 2017/7/12.
 */
public class OrderConstant {

    //订单状态
    public static final Integer ORDER_STATUS_WAIT = 2; //等待分配
    public static final Integer ORDER_STATUS_BEGIN = 0; //开始任务
    public static final Integer ORDER_STATUS_DONE = 1; //完成
    public static final Integer ORDER_STATUS_EXPIRE = 3; //undone过期

    //订单节点状态
    public static final Integer ORDER_DETAIL_STATUS_TRANSFER = 0; //运输
    public static final Integer ORDER_DETAIL_STATUS_GET = 1; //到达
    public static final Integer ORDER_DETAIL_STATUS_SIGN = 2; //已签收

    //订单细节节点位置
    public static final Integer ORDER_DETAIL_PLACE_START = 0; //起始
    public static final Integer ORDER_DETAIL_PLACE_MIDDLE = 1; //中间
    public static final Integer ORDER_DETAIL_PLACE_END = 2; //结束
}
