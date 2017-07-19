package cn.muye.rosrocker.bean;

import cn.mrobot.bean.base.PubBean;

/**
 * Created by wlkfec on 17/07/2017.
 */
public class RosRockerPubBean extends PubBean {
    private String data;

    public RosRockerPubBean(){}
    public RosRockerPubBean(String pub_name, String data) {
        super(pub_name);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
