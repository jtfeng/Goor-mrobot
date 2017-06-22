package cn.muye.base.listener;

import cn.mrobot.bean.FileResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.DeviceType;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.ExecutorLog;
import cn.mrobot.bean.log.ExecutorLogType;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.ZipUtils;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.config.AppConfig;
import cn.muye.base.service.MessageSendService;
import cn.muye.base.service.imp.MessageSendServiceImp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.json.JsonObject;
import java.io.File;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/14
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AgentPubListenerImpl implements TopicCallback, ApplicationContextAware {

	private static final String MAP_PATH = "E:\\documents\\upload";
	public final static String REMOTE_URL = "http://localhost:8060"; //正式服务器地址
	public final static String UPLOAD_URL = "/services/public/files/largeUpload"; //上传大文件的接口地址
	public final static String EXIST_URL = "/services/public/files/isExistFile"; //判断文件是否存在

	private static ApplicationContext applicationContext;
	private final Lock lock = new ReentrantLock();
	private static final Logger logger = LoggerFactory.getLogger(AgentPubListenerImpl.class);

	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== agent_pub topic  " + message.toString());
		JsonObject jsonObject = message.toJsonObject();
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject dataObject = JSON.parseObject(data);
		String pubName = dataObject.getString(TopicConstants.PUB_NAME);
		if (pubName.indexOf(TopicConstants.AGENT_PREFIX) >= 0) {//需要agent本地处理的topic
			handleAgentLocal(dataObject);
		} else {
			String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
			byte[] b = text.getBytes();
			ExecutorLog logInfo = new ExecutorLog();
			logInfo.setData(message.toString());
			logInfo.setType(ExecutorLogType.AGENT_PUB);

			MessageInfo info = new MessageInfo(MessageType.EXECUTOR_LOG, JSON.toJSONString(logInfo), b);
			info.setMessageStatusType(MessageStatusType.PUBLISH_ROS_MESSAGE);
			AppConfig appConfig = CacheInfoManager.getAppConfigCache(1L);
			info.setSenderId(appConfig.getMpushUserId());
			info.setSendDeviceType(DeviceType.GOOR);
			info.setReceiverDeviceType(DeviceType.GOOR_SERVER);
			MessageSendService messageSendService = new MessageSendServiceImp();
			messageSendService.sendReplyMessage(Constant.GOOR_SERVER, info);
		}
	}


	private void handleAgentLocal(JSONObject dataObject) {
		String pubName = dataObject.getString(TopicConstants.PUB_NAME);
		if (TopicConstants.AGENT_LOCAL_MAP_UPLOAD.equals(pubName)) {
			uploadMapFile();
		}

	}

	public void uploadMapFile() {
//		if (lock.tryLock()) {
//			try {
//				File dest = new File(MAP_PATH);
//				if (dest.isDirectory()) {
//					//压缩文件夹
//					String lastDirName = MAP_PATH.substring(MAP_PATH.lastIndexOf(File.separator) + 1);
//					String zipFileName = lastDirName + TopicConstants.ZIP_FILE_SUFFIX;
//
//					File zipFile = new File(dest.getParent() + File.separator + zipFileName);
//					zipFile.deleteOnExit(); //如果存在同名文件，则删除
//
//					boolean zipResult = ZipUtils.zip(MAP_PATH, dest.getParent(), zipFileName);
//					if(zipResult){
//						if(zipFile.length() > 500 * 1024 * 1024){
//							logger.info("文件"+ zipFileName + "大于500M，无法上传");
//							return;
//						}
//						Map<String,String> params = Maps.newHashMap();
//						params.put("fileName",zipFileName);
//						String jsonResult = HttpClientUtil.executePost(null, REMOTE_URL + EXIST_URL, params, null, null, null, true);
//						FileResult fileResult = JSON.parseObject(jsonResult, FileResult.class);
//						if(fileResult.getStatus() == 0) {
//							if(!fileResult.isExist()){
//								long jumpSize = 0;
//								if(fileResult.isTemp()){
//									jumpSize = fileResult.getSize();
//								}
//								//获取上传信息
//								String result = HttpClientUtil.executeUploadFile(null, REMOTE_URL+ UPLOAD_URL +"?fileName="+ fileName, localUrl,jumpSize, null, true);
//								AjaxResponse resp = JSON.parseObject(result, AjaxResponse.class);
//								if(resp.getStatus() == AjaxResponse.RESPONSE_STATUS_SUCCESS){
//									//上传成功
//									target.delete();
//									continue;
//								}else if(resp.getStatus() == AjaxResponse.RESPONSE_STATUS_FAIURE) {
//									//上传失败
//									continue;
//								}
//							}else {
//								//假提示上传成功 同时删除本地文件
//								target.delete();
//								continue;
//							}
//						}else {
//							continue;
//						}
//					}else {
//						continue;
//					}
//				}
//			} catch (SocketTimeoutException e) {
//				e.printStackTrace();
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				this.lock.unlock();
//			}
//		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AgentPubListenerImpl.applicationContext = applicationContext;
	}
}
