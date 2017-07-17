package cn.mrobot.bean.log.elevator;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by abel on 17-7-15.
 */
@Table(name = "LOG_ELEVATOR")
public class LogElevator extends BaseBean {
    private static final long serialVersionUID = 8129284695707771565L;

    private String addr;
    private String value;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
