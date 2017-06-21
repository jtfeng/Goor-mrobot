package cn.muye.cache;

import cn.muye.model.config.AppConfig;
import cn.muye.service.mapper.config.AppConfigService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CacheInfoManager implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static AppConfigService appConfigService;

	protected final static Logger logger = Logger.getLogger(CacheInfoManager.class);
	
	/** AppConfig 的缓存 */
	private static ConcurrentHashMapCache<Long, AppConfig> appConfigCache = new ConcurrentHashMapCache<Long, AppConfig>();

	static {

		// AppConfig对象缓存的最大生存时间，单位毫秒，永久保存
		appConfigCache.setMaxLifeTime(0);

	}

	private CacheInfoManager() {

	}

	public static void removeAppConfigCache(Long id) {
		appConfigCache.remove(id);
	}

	/**
	 * AppConfigCache
	 */
	public static AppConfig getAppConfigCache(Long id) {
		if (id == null) {
			return null;
		}
		AppConfig appConfigInfo = appConfigCache.get(id);
		if (appConfigInfo == null) {
			appConfigService = applicationContext.getBean(AppConfigService.class);
			AppConfig appConfig = appConfigService.get(1);
			appConfigCache.put(id, appConfig);
			return appConfig;
		}
		return appConfigInfo;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CacheInfoManager.applicationContext = applicationContext;
	}

}
