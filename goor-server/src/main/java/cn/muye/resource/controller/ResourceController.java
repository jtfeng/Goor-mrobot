package cn.muye.resource.controller;

import cn.mrobot.bean.resource.Resource;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.service.MessageSendService;
import cn.muye.resource.bean.ResourceToAgentBean;
import cn.muye.resource.service.ResourceService;
import com.github.pagehelper.PageInfo;
import com.mpush.util.crypto.MD5Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ResourceController {

    public static final Integer RESOURCE_TYPE_BASE = 1;

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private MessageSendService messageSendService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

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
                file.transferTo(dest);
                bool = true;
            }
            if(bool){
                path.append("/" + generateName);
                //上传成功后添加入表
                Resource resource = new Resource();
                resource.setResourceType(resourceType);
                resource.setFileSize(file.getSize());
                resource.setOriginName(file.getOriginalFilename());
                resource.setGenerateName(generateName);
                resource.setFileType(file.getContentType());
                resource.setPath(path.toString());
                resource.setMd5(MD5Utils.encrypt(file.getBytes()));
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
            return AjaxResult.success(resource,"资源推送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("系统内部出错");
        }

    }


}
