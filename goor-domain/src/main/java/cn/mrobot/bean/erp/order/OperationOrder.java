package cn.mrobot.bean.erp.order;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.erp.operation.OperationType;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
@Table(name = "ERP_OPERATION_ORDER")
public class OperationOrder extends BaseBean {

    //下单手术室
    @Transient
    private Station station;

    @Transient
    private List<OperationOrderApplianceXREF> applianceList;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date receiveTime;//无菌器械室订单接收时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date handleTime;//立即处理时间

    private int state; // 1：待无菌器械包室受理；2：无菌器械包室取消；3：无菌器械包室受理完毕;4：手术室取消；

    @Transient
    private String stateCH;

    private int type; //手术室订单类型: 1:按手术类型申请  2:临时器械申请 3:按手术类型申请,但最终请求的手术包与默认的手术包不同

    @Transient
    private String typeCH;

    @Transient
    private OperationType operationType;

    private String comment; //备注

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public List<OperationOrderApplianceXREF> getApplianceList() {
        return applianceList;
    }

    public void setApplianceList(List<OperationOrderApplianceXREF> applianceList) {
        this.applianceList = applianceList;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getStateCH() {
        return stateCH;
    }

    public void setStateCH(String stateCH) {
        this.stateCH = stateCH;
    }

    public String getTypeCH() {
        return typeCH;
    }

    public void setTypeCH(String typeCH) {
        this.typeCH = typeCH;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OperationOrder init() {
        this.setCreateTime(new Date());
        this.setStoreId(100L);
        this.setState(State.WAITING.getCode());
        return this;
    }

    public enum State {
        WAITING(1, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_DWJQXBSSL"),
        ASEPTIC_APPARATUS_ROOM_CANCEL(2, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_WJQXBSQX"),
        ASEPTIC_APPARATUS_ROOM_HANDLED(3, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_WJQXBSSLWB"),
        OPERATION_CANCEL(4, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_SSSQX"),
        ORDER_FAIL(5, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_SSSXDSB");

        private int code;
        private String name;

        State(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static State getByCode(int code){
            State[] states = State.values();
            for (State state : states){
                if (code == state.getCode()){
                    return state;
                }
            }
            return null;
        }
    }

    public enum Type {
        OPERATION_TYPE_ORDER(1, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_ASSLXSQ"),
        INTERIM_ORDER(2, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_LSSSBSQ"),
        OPERATION_TYPE_ORDER_WITHOUT_DEFAULT_APPLIANCE(3, "goor_domain_src_main_java_cn_mrobot_bean_erp_order_OperationOrder_java_ASSLXSQFMRQX");

        private int code;
        private String name;

        Type(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static Type getByCode(int code){
            Type[] types = Type.values();
            for (Type type : types){
                if (code == type.getCode()){
                    return type;
                }
            }
            return null;
        }
    }
}
