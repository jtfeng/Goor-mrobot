package cn.muye.base.download.download;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;

import java.io.File;

public class DownloadHandle {
	private static Logger logger = Logger.getLogger(DownloadHandle.class);
	
	public static AjaxResult downloadCheck(Ros ros, MessageInfo messageInfo, ReceiveMessageService receiveMessageService){
		if(StringUtil.isEmpty(messageInfo) || StringUtil.isNullOrEmpty(messageInfo.getMessageText())){
			logger.error("messageInfo or messageText is null");
			reSendRelyMessage(messageInfo, receiveMessageService);
			return AjaxResult.failed(MessageStatusType.PARAMETER_ERROR.getName());
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
			reSendRelyMessage(messageInfo, receiveMessageService);
			return AjaxResult.failed(MessageStatusType.PARAMETER_ERROR.getName());
		}

		newFilePath(commonInfo.getLocalPath());
		String localName = commonInfo.getLocalFileName() + Constant.TEMP;//获取本地名称
		RemoteLocalPair pair = new RemoteLocalPair(commonInfo.getRemoteFileUrl(), commonInfo.getLocalPath(), localName);
		DownloadManager.HttpDownload(pair, 5, ros, messageInfo, commonInfo, receiveMessageService);
		return AjaxResult.success(MessageStatusType.SUCCESS_MESSAGE.getName());
	}

	// 传入数据库有问题的数据，直接更新数据库，进入删除状态,将MessageStatusType.CANCEL_MESSAGE,如果无状态消息就直接给webSocket发消息
	private static void reSendRelyMessage(MessageInfo messageInfo, ReceiveMessageService receiveMessageService){
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

	/**
	 * 新建目录
	 */
	private static void newFilePath(String localPath){
		File f = new File(localPath);
		f.mkdirs();
	}
	
}
