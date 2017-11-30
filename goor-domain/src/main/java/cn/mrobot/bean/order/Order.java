package cn.mrobot.bean.order;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 */
public class Order extends BaseBean{

    private OrderSetting orderSetting; //对应的setting

    private Robot robot;  //对应robot

    private Station startStation; //下单站

    private List<OrderDetail> detailList;  //下单配送详情

    private Shelf shelf;  //货架编号 若需要货架

    private Scene scene;  //场景

    private Integer status; //订单状态 0开启 1完成 2等待分配 3取消

    private Date finishDate;  //订单结束时间

    @Transient
    private String resscene; //资源场景

    @Transient
    private Long applyOrderId;  //申请订单id


    public Order() {
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public Order(Long id) {
        super(id);
    }

    public OrderSetting getOrderSetting() {
        return orderSetting;
    }

    public void setOrderSetting(OrderSetting orderSetting) {
        this.orderSetting = orderSetting;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public List<OrderDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<OrderDetail> detailList) {
        this.detailList = detailList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResscene() {
        return resscene;
    }

    public void setResscene(String resscene) {
        this.resscene = resscene;
    }

    public Long getApplyOrderId() {
        return applyOrderId;
    }

    public void setApplyOrderId(Long applyOrderId) {
        this.applyOrderId = applyOrderId;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
