package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.order.GoodsInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataUnload implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sign_in_mode;  //0 表示不签收 1表示签收

    private List<String> employee_num_list; //本地员工校验列表

    private List<GoodsInfo> goodsInfos;

    public List<GoodsInfo> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfo> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public String getSign_in_mode() {
        return sign_in_mode;
    }

    public void setSign_in_mode(String sign_in_mode) {
        this.sign_in_mode = sign_in_mode;
    }

    public List<String> getEmployee_num_list() {
        return employee_num_list;
    }

    public void setEmployee_num_list(List<String> employee_num_list) {
        this.employee_num_list = employee_num_list;
    }
}
