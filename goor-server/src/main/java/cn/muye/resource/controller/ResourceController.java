package cn.muye.resource.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.resource.Resource;
import cn.mrobot.bean.resource.VoiceFile;
import cn.mrobot.utils.MD5Utils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.controller.BaseController;
import cn.muye.resource.bean.ResourceToAgentBean;
import cn.muye.resource.service.ResourceService;
import cn.muye.resource.service.VoiceFileService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by Selim on 2017/6/13.
 */
@Controller
@RequestMapping("resource")
public class ResourceController extends BaseController {

    public static final Integer RESOURCE_TYPE_BASE = 1;


    @Autowired
    private ResourceService resourceService;
    @Autowired
    private VoiceFileService voiceFileService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RobotService robotService;

    /**
     * 保存一个resource (上传)
     *
     * @param file
     * @param resourceType
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult saveResource(@RequestParam("file") MultipartFile file,
                                    @RequestParam("resourceType") Integer resourceType) {
        try {
            //上传文件
            StringBuffer path = new StringBuffer("/" + resourceType.toString());
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + resourceType);
            dest.mkdirs();
            Resource resource = new Resource();
            boolean bool = false;
            String generateName = "";
            if (!file.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                String fileName = file.getOriginalFilename();
                String type = FilenameUtils.getExtension(fileName);
                if (StringUtil.isBlank(type)) {
                    generateName = uuid;
                } else {
                    generateName = uuid + "." + type;
                    ;
                }
                dest = FileUtils.getFile(dest.getPath() + File.separator + generateName);
                if (!dest.exists()) {
                    dest.createNewFile();
                }
                resource.setFileSize(file.getSize());
                resource.setOriginName(file.getOriginalFilename());
                resource.setFileType(file.getContentType());
                resource.setMd5(MD5Utils.encrypt(file.getBytes()));
                file.transferTo(dest);
                bool = true;
            }
            if (bool) {
                path.append("/" + generateName);
                //上传成功后添加入表
                resource.setGenerateName(generateName);
                resource.setResourceType(resourceType);
                resource.setPath(path.toString());
                //resource.setContent();
                resourceService.save(resource);
                return AjaxResult.success(resource, "资源上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("系统内部出错");
        }
        return AjaxResult.failed("文件不能为空");
    }

    /**
     * 分页查询资源
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "pageList", method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pageResourceList(WhereRequest whereRequest) {
        try {
            List<Resource> resourceList = resourceService.listByType(RESOURCE_TYPE_BASE, whereRequest.getPage(), whereRequest.getPageSize());
            PageInfo<Resource> pageList = new PageInfo<>(resourceList);
            return AjaxResult.success(pageList, "资源查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("系统内部出错");
        }

    }

    /**
     * 推送至agent
     *
     * @param resourceToAgentBean
     * @returnC
     */
    @RequestMapping(value = "pushToAgent", method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pushToAgent(@RequestBody ResourceToAgentBean resourceToAgentBean) {
        try {
            Resource resource = resourceService.findById(resourceToAgentBean.getResourceId());
            Robot robot = robotService.getById(resourceToAgentBean.getRobotId());

            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setLocalFileName(resource.getOriginName());
            commonInfo.setLocalPath(REMOTE_DOWNLOAD_HOME);
            commonInfo.setRemoteFileUrl(DOWNLOAD_HTTP + resource.getPath());
            commonInfo.setMD5(resource.getMd5());
            //设置ros 是否发送topic
            //commonInfo.setTopicName("/enva_test");
            //commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            //commonInfo.setPublishMessage(JSON.toJSONString(commonInfo));
            MessageInfo info = new MessageInfo(Constant.GOOR_SERVER, robot.getCode(),JSON.toJSONString(commonInfo));
            String backResultResourceRoutingKey = RabbitMqBean.getRoutingKey(info.getReceiverId(), true, MessageType.EXECUTOR_RESOURCE.name());
            logger.info("资源开始推送，推送机器人编号为"+ robot.getCode());
            AjaxResult ajaxResult = (AjaxResult)rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultResourceRoutingKey, info);
            if(ajaxResult == null){
                logger.info("资源推送连接客户端失败");
                return AjaxResult.failed("资源推送连接客户端失败");
            }else {
                if(ajaxResult.isSuccess()){
                    logger.info("资源推送成功");
                }else {
                    logger.info("资源推送失败，原因为"+ ajaxResult.getMessage());
                }
                return ajaxResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("系统内部出错");
        }

    }

    /**
     * 语音文件列表接口
     * @return
     */
    @RequestMapping(value = "listVoiceFile", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listVoiceFiles(WhereRequest whereRequest) {
        try {
            List<VoiceFile> list = voiceFileService.listVoiceFiles(whereRequest.getPage(), whereRequest.getPageSize());
            PageInfo<VoiceFile> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "语音文件查询成功");
        } catch (Exception e) {
            logger.error("{}", e);
            return AjaxResult.failed("系统内部出错");
        }
    }


}
