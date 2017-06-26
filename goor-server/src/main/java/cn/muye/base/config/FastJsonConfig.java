package cn.muye.base.config;

/**
 * Created by Selim on 2017/6/26.
 */

import cn.muye.base.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastJsonConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        //1、先定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //附加：处理中文乱码（后期添加）
        List<MediaType> fastMedisTypes = new ArrayList<MediaType>();
        fastMedisTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMedisTypes);
        SerializerFeature[] serializerFeatures = {
                        SerializerFeature.PrettyFormat,
                        SerializerFeature.WriteMapNullValue
        };
        fastConverter.setSerializerFeature(serializerFeatures);
        //4、将convert添加到converters
        converters.add(fastConverter);
    }

}
