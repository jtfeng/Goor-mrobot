package cn.mrobot.bean.order;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.base.BaseBean;

import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 */
public class Order extends BaseBean{

    private OrderSetting orderSetting; //对应的setting

    private Robot robot;  //对应robot

    private Station startStation; //下单站

    private List<OrderDetail> detailList;  //下单配送详情

    private Boolean needShelf = Boolean.FALSE;  //默认不需要货架

    private Shelf shelf;  //货架编号 若需要货架

    private Scene scene;  //场景

    private Integer status; //订单状态 0未完成 1完成

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

    public Boolean getNeedShelf() {
        return needShelf;
    }

    public void setNeedShelf(Boolean needShelf) {
        this.needShelf = needShelf;
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
}
