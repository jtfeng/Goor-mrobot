package cn.mrobot.bean.log;

import javax.persistence.Table;

/**
 * Created by Jelynn on 2017/8/11.
 */
@Table(name = "LOG_INFO_XREF")
public class LogInfoXREF {

    protected String foreignKey; //通过ModuleEnums和id进行拼接

    protected Long logInfoId; //LogInfo ID

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public Long getLogInfoId() {
        return logInfoId;
    }

    public void setLogInfoId(Long logInfoId) {
        this.logInfoId = logInfoId;
    }
}
