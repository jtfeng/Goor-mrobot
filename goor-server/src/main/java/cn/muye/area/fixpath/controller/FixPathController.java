package cn.muye.area.fixpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.muye.area.fixpath.service.FixPathService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.controller.BaseController;
import cn.muye.i18n.service.LocaleMessageSourceService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

/**
 * Created by Jelynn on 2017/9/18.
 */
@RestController
public class FixPathController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FixPathController.class);

    @Autowired
    private FixPathService fixPathService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    @RequestMapping(value = "area/fixpath", method = RequestMethod.GET)
    public AjaxResult sendFixPathQuery(@RequestParam("sceneId") Long sceneId,
                                       @RequestParam("robotCode") String robotCode) {
        try {
            AjaxResult ajaxResult =  fixPathService.sendFixpathQuery(sceneId, robotCode);
            return ajaxResult;
        } catch (Exception e) {
            logger.error(" 获取工控固定路径出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_fixpath_controller_FixPathController_java_QQSB"));
        }
    }

    /**
     * 接收从agent端发送的path文件 请求
     * @param file
     * @param sendId
     * @return
     */
    @RequestMapping(value = "area/fixpath/upload", method = RequestMethod.POST)
    public AjaxResult uploadFixPath(@RequestParam("file")MultipartFile file,
                                    @RequestParam("sendId")String sendId){
        try {
            //判定是否为txt文件
            String fileName = file.getOriginalFilename();
            if(!fileName.endsWith(".txt")){
                logger.info("上传文件名为{},非txt文档", fileName);
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_fixpath_controller_FixPathController_java_SCWJFTXTWD"));
            }
            //转存至后台服务器内
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator + "pathFile");
            dest.mkdirs();
            String fileSubName = fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis();
            String newFileName = fileSubName + fileName.substring(fileName.lastIndexOf("."));
            dest = FileUtils.getFile(dest.getPath()+ File.separator + newFileName);
            if(!dest.exists()){
                dest.createNewFile();
            }
            file.transferTo(dest);
            //数据库存储
            String pathString = IOUtils.toString(file.getInputStream());
            fixPathService.saveFixpathQuery(sendId, new Date(), pathString);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("读取path文件出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_fixpath_controller_FixPathController_java_QQSB"));
        }
    }
}
