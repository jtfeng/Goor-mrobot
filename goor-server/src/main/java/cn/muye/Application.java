package cn.muye;

import cn.muye.core.converter.FastJsonHttpMessageConverter;
import cn.muye.listener.ClientListenerImp;
import cn.muye.service.batch.ScheduledHandle;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.mpush.api.Client;
import com.mpush.api.ClientListener;
import com.mpush.client.ClientConfig;
import com.mpush.util.DefaultLogger;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@EnableScheduling
@MapperScan("cn.muye.**.mapper")
@EnableTransactionManagement
public class Application extends WebMvcConfigurerAdapter {
    private static Logger logger = Logger.getLogger(Application.class);

    @Value("${mpush.publicKey}")
    private String publicKey;

    @Value("${mpush.allocServer}")
    private String allocServer;

    @Value("${ros.path}")
    private String rosPath;

    @Value("${mpush.deviceId}")
    private String deviceId;

    @Value("${mpush.osName}")
    private String osName;

    @Value("${mpush.osVersion}")
    private String osVersion;

    @Value("${mpush.clientVersion}")
    private String clientVersion;

    @Value("${mpush.userId}")
    private String userId;

    @Value("${mpush.tags}")
    private String tags;

    @Value("${mpush.sessionStorageDir}")
    private String sessionStorageDir;

   @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource() {
        return new org.apache.tomcat.jdbc.pool.DataSource();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        //添加插件
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis*//**//*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public Client getClient() {
        int sleep = 1000;

        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Application ClientListenerImp");
            }
        });
        ClientListener listener = new ClientListenerImp(scheduledExecutor);
        Client client = null;
        String cacheDir = Application.class.getResource("/").getFile();
            client = ClientConfig
                    .build()
                    .setPublicKey(publicKey)
                    .setAllotServer(allocServer)
//                    .setServerHost(allocServer)
//                    .setServerPort(3000)
                    .setDeviceId(deviceId)
                    .setOsName(osName)
                    .setOsVersion(osVersion)
                    .setClientVersion(clientVersion)
                    .setUserId(userId)
                    .setTags(tags)
                    .setSessionStorageDir(cacheDir+sessionStorageDir)
                    .setLogger(new DefaultLogger())
                    .setLogEnabled(true)
                    .setEnableHttpProxy(true)
                    .setClientListener(listener)
                    .create();
            client.start();
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                logger.error("Application client start sleep error=========",e);
            }

        return client;
    }

    @Bean
    public ScheduledExecutorService scheduledHandle(){
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Application scheduledHandle");
            }
        });
        new ScheduledHandle(scheduledExecutor);
        return scheduledExecutor;
    }

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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);

        //核心代码
//       1、先定义一个convert转换消息的对象
//        2、添加fastjson的配置信息，比如是否要格式化返回的json数据；
//        3、在convert中添加配置信息
//        4、将convert添加到converters

        //1、先定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //2、添加fastjson的配置信息，比如是否要格式化返回的json数据；
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                //是否需要格式化
                SerializerFeature.PrettyFormat
        );
        //附加：处理中文乱码（后期添加）
        List<MediaType> fastMedisTypes = new ArrayList<MediaType>();
        fastMedisTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMedisTypes);
        //3、在convert中添加配置信息
//        fastConverter.setFastJsonConfig(fastJsonConfig);
        //4、将convert添加到converters
        converters.add(fastConverter);
    }

	/**
     * Start
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("SpringBoot Start Success");
    }

}
