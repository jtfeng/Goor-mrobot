package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.log.mission.JsonMissionStateResponse;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import cn.muye.order.service.OrderDetailService;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by abel on 17-7-18.
 */
@Service
public class X86MissionStateResponseServiceImpl
        implements X86MissionStateResponseService {


    private Logger logger = Logger
            .getLogger(X86MissionStateResponseServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    MissionListTaskService missionListTaskService;

    @Autowired
    MissionTaskService missionTaskService;

    @Autowired
    MissionItemTaskService missionItemTaskService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    RobotService robotService;

    @Override
    public AjaxResult handleX86MissionStateResponse(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
        String data = baseMessageService.getData(messageInfo);
        if (!StringUtil.isEmpty(data)){
            JsonMissionStateResponse jsonMissionStateResponse =
                    (JsonMissionStateResponse) JsonUtils.fromJson(data,
                            new TypeToken<JsonMissionStateResponse>(){}.getType());
            if (jsonMissionStateResponse != null &&
                    !StringUtil.isEmpty(jsonMissionStateResponse.getState())){
                switch (jsonMissionStateResponse.getState()){
                    case JsonMissionStateResponse.state_waiting:
                        //当前无任务执行
                        robotService.setRobotBusyAndOnline(baseMessageService.getSenderId(messageInfo),
                                false, true);
                        handleStateWaiting(jsonMissionStateResponse,
                                baseMessageService.getSenderId(messageInfo));
                        break;
                    case JsonMissionStateResponse.state_canceled:
                        //被取消
                        robotService.setRobotBusyAndOnline(baseMessageService.getSenderId(messageInfo),
                                false, true);
                        handleStateCanceled(jsonMissionStateResponse,
                                baseMessageService.getSenderId(messageInfo));
                        break;
                    case JsonMissionStateResponse.state_executing:
                        //正在执行
                        robotService.setRobotBusyAndOnline(baseMessageService.getSenderId(messageInfo),
                                true, true);
                        handleStateExecuting(jsonMissionStateResponse,
                                baseMessageService.getSenderId(messageInfo));
                        break;
                    case JsonMissionStateResponse.state_finished:
                        //已经完成
                        robotService.setRobotBusyAndOnline(baseMessageService.getSenderId(messageInfo),
                                false, true);
                        handleStateFinished(jsonMissionStateResponse,
                                baseMessageService.getSenderId(messageInfo));
                        break;
                    case JsonMissionStateResponse.state_paused:
                        //暂停中
                        robotService.setRobotBusyAndOnline(baseMessageService.getSenderId(messageInfo),
                                true, true);
                        handleStatePaused(jsonMissionStateResponse,
                                baseMessageService.getSenderId(messageInfo));
                        break;
                    default:
                        break;
                }
            }
        }
        return null;
    }

    /**
     * 更新task数据
     * @param jsonMissionStateResponse
     * @param senderId
     */
    private void updateTasks(
            JsonMissionStateResponse jsonMissionStateResponse,
            String senderId) {
        if (jsonMissionStateResponse == null ||
                jsonMissionStateResponse.getMission_list_id() == null ||
                StringUtil.isEmpty(senderId)){
            return;
        }

        //更新mission list task state
        MissionListTask missionListTask =
                missionListTaskService.findById(
                        jsonMissionStateResponse.getMission_list_id());
        if (missionListTask != null){
            missionListTask.setState(jsonMissionStateResponse.getState());
            if (jsonMissionStateResponse.getRepeat_times() != null){
                missionListTask.setRepeatTimesReal(jsonMissionStateResponse.getRepeat_times());
            }
            missionListTaskService.updateSelective(missionListTask);
        }

        //更新mission task state
        if (jsonMissionStateResponse.getMission_list() != null){
            for (JsonMissionStateResponse.Mission_listEntity en:
                    jsonMissionStateResponse.getMission_list()) {
                if (en != null &&
                        en.getMission_id() != null){
                    MissionTask missionTask =
                            missionTaskService.findById(en.getMission_id());
                    if (missionTask != null){
                        if (!StringUtil.isEmpty(en.getState())){
                            missionTask.setState(en.getState());
                        }
                        if (en.getRepeat_times() != null){
                            missionTask.setRepeatTimesReal(en.getRepeat_times());
                        }
                        missionTaskService.updateSelective(missionTask);
                        //判断当前如果关联了order detail,则置状态
                        if (!StringUtil.isNullOrEmpty(missionTask.getOrderDetailMission()) &&
                                !MissionFuncsServiceImpl.str_zero.equalsIgnoreCase(missionTask.getOrderDetailMission())){
                            Long id = null;
                            try {
                                id = Long.valueOf(missionTask.getOrderDetailMission());
                            } catch (NumberFormatException e) {
                                logger.error("设置order detail失败。id转换失败。mission id is: "+ missionTask.getId());
                            }
                            if(MissionFuncsServiceImpl.MissionStateFinished.equalsIgnoreCase(en.getState())){
                                if (id != null){
                                    //设置order detail的单子状态为完成
                                    logger.info("### set finishedDetailTask " + id);
                                    orderDetailService.finishedDetailTask(id, OrderConstant.ORDER_DETAIL_STATUS_GET);
                                }
                            } else if(MissionFuncsServiceImpl.MissionStateCanceled.equalsIgnoreCase(en.getState())){
                                //在此之前判断missionItem的状态值
                                List<MissionItemTask> missionItemTaskList = missionItemTaskService.findByListIdAndMissionId(missionListTask.getId(), missionTask.getId());
                                for (MissionItemTask missionItemTask : missionItemTaskList) {
                                    //遍历寻找到loadNoShelf或者unload状态下 修改为完成状态
                                    if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_loadNoShelf)
                                            ||missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_unload)){
                                        if (id != null){
                                            //设置order detail的单子状态为完成
                                            logger.info("### set finishedDetailTask " + id);
                                            orderDetailService.finishedDetailTask(id, OrderConstant.ORDER_DETAIL_STATUS_GET);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //更新mission item state
                    if (en.getMission_item_set() != null){
                        for (JsonMissionStateResponse.Mission_listEntity.Mission_item_setEntity item:
                                en.getMission_item_set()) {
                            if (item != null &&
                                    item.getMission_item_id() != null){
                                MissionItemTask missionItemTask =
                                        missionItemTaskService.findById(item.getMission_item_id());
                                if (missionItemTask != null){
                                    if (!StringUtil.isEmpty(item.getState())){
                                        missionItemTask.setState(item.getState());
                                    }
                                    missionItemTaskService.updateSelective(missionItemTask);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 任务暂停状态处理
     * @param jsonMissionStateResponse
     * @param senderId
     */
    private void handleStatePaused(
            JsonMissionStateResponse jsonMissionStateResponse,
            String senderId) {
        if (jsonMissionStateResponse == null ||
                jsonMissionStateResponse.getMission_list_id() == null ||
                StringUtil.isEmpty(senderId)){
            return;
        }
        //更新task数据
        updateTasks(jsonMissionStateResponse,
                senderId);
    }

    /**
     * 任务完成状态处理
     * @param jsonMissionStateResponse
     * @param senderId
     */
    private void handleStateFinished(
            JsonMissionStateResponse jsonMissionStateResponse,
            String senderId) {
        if (jsonMissionStateResponse == null ||
                jsonMissionStateResponse.getMission_list_id() == null ||
                StringUtil.isEmpty(senderId)){
            return;
        }
        //更新task数据
        updateTasks(jsonMissionStateResponse,
                senderId);
    }

    /**
     * 任务执行中状态处理
     * @param jsonMissionStateResponse
     * @param senderId
     */
    private void handleStateExecuting(
            JsonMissionStateResponse jsonMissionStateResponse,
            String senderId) {
        if (jsonMissionStateResponse == null ||
                jsonMissionStateResponse.getMission_list_id() == null ||
                StringUtil.isEmpty(senderId)){
            return;
        }
        //更新task数据
        updateTasks(jsonMissionStateResponse,
                senderId);

    }

    /**
     * 任务被取消状态处理
     * @param jsonMissionStateResponse
     * @param senderId
     */
    private void handleStateCanceled(
            JsonMissionStateResponse jsonMissionStateResponse,
            String senderId) {
        if (jsonMissionStateResponse == null ||
                jsonMissionStateResponse.getMission_list_id() == null ||
                StringUtil.isEmpty(senderId)){
            return;
        }
        //更新task数据
        updateTasks(jsonMissionStateResponse,
                senderId);

    }

    /**
     * 任务等待状态处理
     * @param jsonMissionStateResponse
     * @param senderId
     */
    private void handleStateWaiting(
            JsonMissionStateResponse jsonMissionStateResponse,
            String senderId) {
        if (jsonMissionStateResponse == null ||
                jsonMissionStateResponse.getMission_list_id() == null ||
                StringUtil.isEmpty(senderId)){
            return;
        }
        //更新task数据
        updateTasks(jsonMissionStateResponse,
                senderId);

    }
}
