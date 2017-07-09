package cn.mrobot.bean.order;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by Selim on 2017/7/6.
 */
@Table(name = "OR_GOODS")
public class Goods extends BaseBean {

    private String name;

    private String unit;

    private Boolean deleteStatus = Boolean.FALSE;

    //关联大项
    private Long goodTypeId;

    public Goods() {
    }

    public Goods(Long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getGoodTypeId() {
        return goodTypeId;
    }

    public void setGoodTypeId(Long goodTypeId) {
        this.goodTypeId = goodTypeId;
    }
}
