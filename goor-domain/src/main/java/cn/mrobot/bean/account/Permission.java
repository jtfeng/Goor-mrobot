package cn.mrobot.bean.account;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
@Table(name = "AC_PERMISSION")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String enMenuName; //菜单英文名  EN_MENU_NAME

    private String cnMenuName; //菜单中文名 CN_MENU_NAME

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnMenuName() {
        return enMenuName;
    }

    public void setEnMenuName(String enMenuName) {
        this.enMenuName = enMenuName;
    }

    public String getCnMenuName() {
        return cnMenuName;
    }

    public void setCnMenuName(String cnMenuName) {
        this.cnMenuName = cnMenuName;
    }
}
