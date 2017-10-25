package cn.mrobot.bean.dijkstra;

import cn.mrobot.bean.base.BaseBean;

import java.util.List;

/**
 * Created by chay on 2017/10/13.
 */
public class RoadPathResult extends BaseBean {
    List<Long> pointIds;//最优路径点ID序列
    Long totalWeight;//最优路径总权值

    public List<Long> getPointIds() {
        return pointIds;
    }

    public void setPointIds(List<Long> pointIds) {
        this.pointIds = pointIds;
    }

    public Long getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Long totalWeight) {
        this.totalWeight = totalWeight;
    }
}
