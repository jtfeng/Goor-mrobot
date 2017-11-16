package cn.mrobot.bean.assets.good;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.order.Goods;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/6.
 */
@Table(name = "AS_GOODS_TYPE")
public class GoodsType extends BaseBean{

    private String name;

    private String description;

    private Integer robotTypeId;

    private Boolean deleteStatus = Boolean.FALSE;

    @Transient
    private List<Goods> goodsList;

    public Boolean getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRobotTypeId() {
        return robotTypeId;
    }

    public void setRobotTypeId(Integer robotTypeId) {
        this.robotTypeId = robotTypeId;
    }
}
