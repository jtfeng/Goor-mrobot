package cn.muye;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.listener.*;
import cn.muye.service.batch.ScheduledHandle;
import com.github.pagehelper.PageHelper;
import com.mpush.api.Client;
import com.mpush.api.ClientListener;
import com.mpush.client.ClientConfig;
import com.mpush.util.DefaultLogger;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableTransactionManagement
@MapperScan("cn.muye.mapper")
public class Application {
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
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return new org.apache.tomcat.jdbc.pool.DataSource();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		//分页插件
		PageHelper pageHelper = new PageHelper();
		Properties props = new Properties();
		props.setProperty("reasonable", "true");
		props.setProperty("supportMethodsArguments", "true");
		props.setProperty("returnPageInfo", "check");
		props.setProperty("params", "count=countSql");
		pageHelper.setProperties(props);
		//添加插件
		sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
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
				.setSessionStorageDir(cacheDir + sessionStorageDir)
				.setLogger(new DefaultLogger())
				.setLogEnabled(true)
				.setEnableHttpProxy(true)
				.setClientListener(listener)
				.create();
		client.start();

		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			logger.error("Application client start sleep error=========", e);
		}

		return client;
	}

	@Bean
	public Ros ros() {
		Ros ros = new Ros(rosPath);
		ros.connect();
//		Topic echoBack = new Topic(ros, Constant.ODOM, Constant.TOPIC_NAV_MSGS);
//		TopicCallback topicCallback = new TopicDemoListenerImp();
//		echoBack.subscribe(topicCallback);
		//订阅工控的topic。所有工控信息全发布在这个topic中，通过sub_name进行区分
		Topic appSubTopic = new Topic(ros, TopicConstants.APP_SUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback appSubCallback = new AppSubListenerImpl();
		appSubTopic.subscribe(appSubCallback);
		//cloud topic
//		Topic cloudBack = new Topic(ros, Constant.CLOUD_SUB, Constant.TOPIC_TYPE_STRING);
//		TopicCallback cloudTopicCallback = new TopicCloudListenerImp();
//		cloudBack.subscribe(cloudTopicCallback);
		//订阅应用发布、工控接收的topic。所有应用信息全发布在这个topic中，通过pub_name进行区分
		Topic appPubTopic = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback appPubCallback = new AppPubListenerImpl();
		appPubTopic.subscribe(appPubCallback);
		//订阅agent发布的topic。所有agent发布信息全发布在这个topic中，通过pub_name进行区分
		Topic agentPubTopic = new Topic(ros, TopicConstants.AGENT_PUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback agentPubCallback = new AgentPubListenerImpl();
		agentPubTopic.subscribe(agentPubCallback);
		//订阅agent接收的topic。所有agent接收信息全发布在这个topic中，通过sub_name进行区分
		Topic agentSubTopic = new Topic(ros, TopicConstants.AGENT_SUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback agentSubCallback = new AgentSubListenerImpl();
		agentSubTopic.subscribe(agentSubCallback);
		return ros;
	}

	@Bean
	public ScheduledExecutorService scheduledHandle() {
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
	 * Start
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		logger.info("SpringBoot Start Success");
	}

}
