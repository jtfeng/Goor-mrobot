package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by Selim on 2017/11/15.
 */
@Service
public class FixFilePathServiceImpl implements FixFilePathService {

    private Logger logger = Logger.getLogger(FixFilePathServiceImpl.class);

    @Override
    public AjaxResult handleFixFilePath(String sceneName) {
        logger.info("请求的场景名为"+ sceneName);
        //String url = "/maps/"+ sceneName + "/pathes.txt";
        try {
            String url = "D:\\pathes.txt";
            InputStream in = new FileInputStream(url);
            String returnString = IOUtils.toString(in);
            logger.info(returnString);
            return AjaxResult.success(returnString,"读取文件成功");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.failed("未获取相应的文件");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.failed("IO流处理异常");
        }

    }
}
