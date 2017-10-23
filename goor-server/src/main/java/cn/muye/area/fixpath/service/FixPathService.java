package cn.muye.area.fixpath.service;

import cn.mrobot.bean.AjaxResult;

import java.util.Date;

/**
 * Created by Jelynn on 2017/9/18.
 */
public interface FixPathService {

    /**
     * 保存工控查询出的固定路径信息
     *
     * @param senderId    机器编号
     * @param sendTime    发送时间
     * @param messageData 数据
     */
    void saveFixpathQuery(String senderId, Date sendTime, String messageData) throws Exception;

    /**
     * 发送获取工控固定路径信息的请求
     * @param sceneId      场景id
     * @param robotCode      机器人编号
     */
    AjaxResult sendFixpathQuery(Long sceneId, String robotCode) throws Exception;
}
