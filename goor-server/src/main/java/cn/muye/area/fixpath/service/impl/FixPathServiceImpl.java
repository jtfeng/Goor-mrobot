package cn.muye.area.fixpath.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamRequestBody;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.fixpath.service.FixPathService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.service.consumer.topic.BaseMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Jelynn on 2017/9/18.
 */
@Service
public class FixPathServiceImpl implements FixPathService {

    private final static Logger logger = LoggerFactory.getLogger(FixPathServiceImpl.class);
    private static final int PATH_TYPE_IC = 1; //工控上传路径id
//    private static final String PATH = "path"; //RoadPath name前缀

    @Autowired
    private PointService pointService;

    @Autowired
    private RoadPathService roadPathService;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private BaseMessageService baseMessageService;

    @Override
    public void saveFixpathQuery(String senderId, Date sendTime, String messageData) throws Exception {

        if (StringUtil.isNullOrEmpty(messageData)){
            return;
        }

        JSONObject jsonObject = JSON.parseObject(messageData);
        String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
        //判断场景是否是云端下发的场景名，如果不是，对上传的fixpath数据不做入库处理
        //Boolean idCloudRequest = CacheInfoManager.getFixpathSceneNameCache(sceneName);
        //暂时除去场景名再次判定，临时方案
        Boolean idCloudRequest = true;
        if (idCloudRequest != null && idCloudRequest){
            //携带sceneName 请求agent端文件
            String paths = jsonObject.getString(TopicConstants.PATHS);
            /*SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.FIXPATH_FILE_QUERY);
            JSONObject dataObject = new JSONObject();
            dataObject.put(TopicConstants.SCENE_NAME, sceneName);
            slamRequestBody.setData(dataObject);
            MessageInfo messageInfo = new MessageInfo(Constant.GOOR_SERVER, senderId, JSON.toJSONString(slamRequestBody));
            messageInfo.setMessageType(MessageType.EXECUTOR_CLIENT);
            AjaxResult ajaxResult = messageSendHandleService.sendToX86Message(false, true, senderId, messageInfo);
            String filePaths = ajaxResult.getData().toString();
            JSONObject pathObject = JSON.parseObject(filePaths);
            String paths = pathObject.getString("paths");*/
            List<PathDTO> pathDTOList = JSONArray.parseArray(paths, PathDTO.class);
            //删除缓存
            CacheInfoManager.removeFixpathSceneNameCache(sceneName);
            //调用统一存储工控固定路径方法
            roadPathService.saveOrUpdateRoadPathByPathDTOListNoDuplicatePoint(pathDTOList, sceneName);
        }
    }

    @Override
    public AjaxResult sendFixpathQuery(Long sceneId, String robotCode) throws Exception {
        String mapSceneName = sceneService.getRelatedMapNameBySceneId(sceneId);
        Boolean online = CacheInfoManager.getRobotOnlineCache(robotCode);
        if (null == online || !online){
            return AjaxResult.failed("机器人"+robotCode+"不在线");
        }
        if (StringUtil.isNullOrEmpty(mapSceneName)){
            return  AjaxResult.failed("未获取到当前场景关联的地图场景名");
        }

        SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.FIXPATH_FILE_QUERY);
        JSONObject dataObject = new JSONObject();
        dataObject.put(TopicConstants.SCENE_NAME, mapSceneName);
        slamRequestBody.setData(dataObject);
        AjaxResult ajaxResult = baseMessageService.sendRobotMessage(robotCode, TopicConstants.APP_PUB, JSON.toJSONString(slamRequestBody));
        if (ajaxResult != null && ajaxResult.isSuccess()){
            //缓存请求发送成功的场景名
            CacheInfoManager.setFixpathSceneNameCache(mapSceneName);
        }
        return ajaxResult;
    }
}
