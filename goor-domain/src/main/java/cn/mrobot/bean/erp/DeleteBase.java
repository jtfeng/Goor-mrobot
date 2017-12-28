package cn.mrobot.bean.erp;

import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 *
 * @author Jelynn
 * @date 2017/12/28
 */
public abstract class DeleteBase extends BaseBean {

    private int deleteFlag;   //数据库删除状态 0 :正常 1：删除

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;   //数据库删除时间

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
}
