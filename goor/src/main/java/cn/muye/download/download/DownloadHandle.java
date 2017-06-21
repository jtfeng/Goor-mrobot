package cn.muye.download.download;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.utils.StringUtil;
import cn.muye.bean.CommonInfo;
import cn.muye.bean.MessageInfo;
import cn.muye.model.message.ReceiveMessage;
import cn.muye.service.MessageSendService;
import cn.muye.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;

import java.io.File;

public class DownloadHandle {
	private static Logger logger = Logger.getLogger(DownloadHandle.class);
	
	public static void downloadCheck(Ros ros, Client client, MessageInfo messageInfo, MessageSendService messageSendService, ReceiveMessageService receiveMessageService){
		if(StringUtil.isEmpty(messageInfo) || StringUtil.isNullOrEmpty(messageInfo.getMessageText())){
			logger.error("messageInfo or messageText is null");
			reSendRelyMessage(messageInfo, receiveMessageService, messageSendService);
			return;
		}
		CommonInfo commonInfo = JSON.parseObject(messageInfo.getMessageText(), CommonInfo.class);
		if(commonInfo == null
				|| StringUtil.isNullOrEmpty(commonInfo.getRemoteFileUrl())
				|| StringUtil.isNullOrEmpty(commonInfo.getLocalPath())
				|| StringUtil.isNullOrEmpty(commonInfo.getLocalFileName())
				|| StringUtil.isNullOrEmpty(commonInfo.getMD5())
				|| StringUtil.isNullOrEmpty(commonInfo.getTopicType())
				|| StringUtil.isNullOrEmpty(commonInfo.getPublishMessage())
				|| StringUtil.isNullOrEmpty(commonInfo.getTopicName())){
			logger.error("commonInfo field is null");
			reSendRelyMessage(messageInfo, receiveMessageService, messageSendService);
			return;
		}

		newFilePath(commonInfo.getLocalPath());
//		String fName = commonInfo.getRemoteFileUrl().trim();//路径去空格
//		String localName = fName.substring(fName.lastIndexOf("/")+1)+ Constant.TEMP;//获取本地名称
		String localName = commonInfo.getLocalFileName() + Constant.TEMP;//获取本地名称
		RemoteLocalPair pair = new RemoteLocalPair(commonInfo.getRemoteFileUrl(), commonInfo.getLocalPath(), localName);
		DownloadManager.HttpDownload(pair, 5, ros, client, messageInfo, messageSendService, commonInfo, receiveMessageService);
	}

	// 传入数据库有问题的数据，直接更新数据库，进入删除状态,将MessageStatusType.CANCEL_MESSAGE,如果无状态消息就直接给webSocket发消息
	private static void reSendRelyMessage(MessageInfo messageInfo, ReceiveMessageService receiveMessageService, MessageSendService messageSendService){
		if(!messageInfo.isFailResend()){
			messageSendService.sendWebSocketMessage(messageInfo, MessageStatusType.PARAMETER_ERROR, null);
			return;
		}
		if(messageInfo.isFailResend()){
			messageInfo.setMessageStatusType(MessageStatusType.CANCEL_MESSAGE);
			ReceiveMessage msg = new ReceiveMessage(messageInfo);
			msg.setRelyMessage("parameter error");
			msg.setSuccess(false);
			try {
				receiveMessageService.update(msg);
			} catch (Exception e) {
				logger.error("update receiveMessage Exception", e);
			}
			return;
		}
	}

	/**
	 * 新建目录
	 */
	private static void newFilePath(String localPath){
		File f = new File(localPath);
		f.mkdirs();
	}
	
}
