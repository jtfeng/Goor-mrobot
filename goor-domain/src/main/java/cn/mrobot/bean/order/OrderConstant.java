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

    //是否需要签收
    public static final String ORDER_NEED_SIGN_NO = "0"; //不需要签收
    public static final String ORDER_NEED_SIGN_YES = "1"; //需要签收

    //消息推送类型
    public static final Integer MESSAGE_BELL_RECEIVE = 0; //收货站提醒
    public static final Integer MESSAGE_BELL_SEND = 1; //发货站提醒

    //消息推送状态
    public static final Integer MESSAGE_BELL_UNREAD = 0; //消息未读
    public static final Integer MESSAGE_BELL_READ = 1; //消息已读

    public static final Integer MESSAGE_DEFAULT_MAX_NUM = 99; //读取消息上限条数
}
