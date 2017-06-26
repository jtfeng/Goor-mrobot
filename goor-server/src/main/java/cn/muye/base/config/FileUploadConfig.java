package cn.muye.base.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * Created by Selim on 2017/6/26.
 */
@Configuration
public class FileUploadConfig {

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("500MB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("500MB");
        return factory.createMultipartConfig();
    }
}
