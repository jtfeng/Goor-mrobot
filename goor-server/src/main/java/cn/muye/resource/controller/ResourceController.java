package cn.muye.resource.controller;

import cn.mrobot.bean.resource.Resource;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.controller.BaseController;
import cn.muye.resource.bean.ResourceToAgentBean;
import cn.muye.resource.service.ResourceService;
import com.github.pagehelper.PageInfo;
import com.mpush.util.crypto.MD5Utils;
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
public class ResourceController extends BaseController{

    public static final Integer RESOURCE_TYPE_BASE = 1;

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 保存一个resource (上传)
     * @param file
     * @param resourceType
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult saveResource(@RequestParam("file") MultipartFile file,
                                    @RequestParam("resourceType") Integer resourceType){
        try {
            //上传文件
            StringBuffer path = new StringBuffer("/"+ resourceType.toString());
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + resourceType);
            dest.mkdirs();
            Resource resource = new Resource();
            boolean bool = false;
            String generateName = "";
            if(!file.isEmpty()){
                String uuid = UUID.randomUUID().toString();
                String fileName = file.getOriginalFilename();
                String type = FilenameUtils.getExtension(fileName);
                if(StringUtil.isBlank(type)){
                    generateName = uuid;
                }else {
                    generateName = uuid + "." + type;;
                }
                dest = FileUtils.getFile(dest.getPath() + File.separator + generateName);
                if(!dest.exists()){
                    dest.createNewFile();
                }
                resource.setFileSize(file.getSize());
                resource.setOriginName(file.getOriginalFilename());
                resource.setFileType(file.getContentType());
                resource.setMd5(MD5Utils.encrypt(file.getBytes()));
                file.transferTo(dest);
                bool = true;
            }
            if(bool){
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
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "pageList",method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pageResourceList(WhereRequest whereRequest){
        try {
            List<Resource> resourceList = resourceService.listByType(RESOURCE_TYPE_BASE,whereRequest.getPage(),whereRequest.getPageSize());
            PageInfo<Resource> pageList = new PageInfo<>(resourceList);
            return AjaxResult.success(pageList,"资源查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("系统内部出错");
        }

    }

    /**
     * 推送至agent
     * @param resourceToAgentBean
     * @return
     */
    @RequestMapping(value = "pushToAgent",method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pushToAgent(@RequestBody ResourceToAgentBean resourceToAgentBean){
        try {
            Resource resource = resourceService.findById(resourceToAgentBean.getResourceId());
            //暂时去除todo中
            /*CommonInfo commonInfo = new CommonInfo();
            commonInfo.setTopicName("/enva_test");
            commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            commonInfo.setLocalFileName(resource.getOriginName());
            commonInfo.setLocalPath("/home");
            commonInfo.setRemoteFileUrl(resource.getPath());
            commonInfo.setMD5(resource.getMd5());
            commonInfo.setPublishMessage(JSON.toJSONString(commonInfo));
            String text = JSON.toJSONString(commonInfo);
            byte[] b = text.getBytes();
            MessageInfo info = new MessageInfo(MessageType.EXECUTOR_RESOURCE, text, b);
            info.setMessageStatusType(MessageStatusType.FILE_NOT_DOWNLOADED);
            info.setReceiptWebSocket(true);
            info.setWebSocketId("user-9");
            info.setSendDeviceType(DeviceType.GOOR_SERVER);
            info.setReceiverDeviceType(DeviceType.GOOR);
            info.setMessageKind(0);
            info.setMessageType(MessageType.EXECUTOR_RESOURCE);
            info.setMessageStatusType(MessageStatusType.INIT);
            info.setSendTime(new Date());
            info.setUpdateTime(info.getSendTime());
            info.setSendCount(0);
            return (AjaxResult)rabbitTemplate.convertSendAndReceive("directExchange", "direct.common", info);*/
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("系统内部出错");
        }

    }


}
