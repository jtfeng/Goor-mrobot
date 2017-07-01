package cn.muye;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.listener.*;
import cn.muye.base.service.batch.ScheduledHandle;
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
import org.springframework.amqp.core.*;
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
@MapperScan("cn.muye.base.mapper")
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
	public Queue directMessageCommand() {
		return new Queue("direct.command");
	}

	@Bean
	public Queue directMessageResource() {
		return new Queue("direct.resource");
	}

	@Bean
	public Queue directMessageCommon() {
		return new Queue("direct.common");
	}

	@Bean
	public Queue fanoutMessageCommand() {
		return new Queue("fanout.command");
	}

	@Bean
	public Queue fanoutMessageResource() {
		return new Queue("fanout.resource");
	}

	@Bean
	public Queue topicMessageCommand() {
		return new Queue("topic.command");
	}

	@Bean
	public Queue topicMessages() {
		return new Queue("topic.messages");
	}

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange("topicExchange");
	}

	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange("fanoutExchange");
	}

	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange("directExchange");
	}

	/**
	 * X86与云端时间同步队列
	 *
	 * */
	@Bean
	public Queue timeSynchronizedUp() {
		return new Queue("time.synchronized.up", false);
	}

	@Bean
	public Queue timeSynchronizedDown() {
		return new Queue("time.synchronized.down", false);
	}

	@Bean
	public Binding bindingTopicExchangeMessage(Queue topicMessageCommand, TopicExchange topicExchange) {
		return BindingBuilder.bind(topicMessageCommand).to(topicExchange).with("topic.message");
	}

	@Bean
	public Binding bindingTopicExchangeMessages(Queue topicMessages, TopicExchange topicExchange) {
		return BindingBuilder.bind(topicMessages).to(topicExchange).with("topic.#");
	}

	@Bean
	public Binding bindingDirectExchangeCommand(Queue directMessageCommand, DirectExchange directExchange) {
		return BindingBuilder.bind(directMessageCommand).to(directExchange).with("direct.common");
	}

	@Bean
	public Binding bindingDirectExchangeResource(Queue directMessageResource, DirectExchange directExchange) {
		return BindingBuilder.bind(directMessageResource).to(directExchange).with("direct.common");
	}

	@Bean
	public Binding bindingFanoutExchangeCommand(Queue fanoutMessageCommand, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(fanoutMessageCommand).to(fanoutExchange);
	}

	@Bean
	public Binding bindingFanoutExchangeResource(Queue fanoutMessageResource, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(fanoutMessageResource).to(fanoutExchange);
	}

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
		Topic checkHeartTopic = new Topic(ros, TopicConstants.CHECK_HEART_TOPIC, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback checkHeartCallback = new CheckHeartSubListenerImpl();
		checkHeartTopic.subscribe(checkHeartCallback);
		TopicSubscribeInfo.reSubScribeTopic(ros);
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
