package cn.mrobot.bean.merchant;

import cn.mrobot.bean.area.point.MapPoint;

import javax.persistence.*;
import java.util.List;

/**
 * Created by chay on 2017/6/22.
 * 门店：理解为客户
 */
@Table(name = "M_MERCHANT_STORE")
public class MerchantStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 门店名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
