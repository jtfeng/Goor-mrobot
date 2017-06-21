package cn.mrobot.bean.notification;


import java.util.Date;

/**
 * Created by Jelynn on 2016/11/23.
 */
public class Notification {

	private long id;

	private long userId; //用户Id

	private long orgId; //店铺Id

	private String boardNO;

	private String storagePath;

	private boolean success;

	private Integer appId;//推送应用

	private String content;

	private Date createTime;

	private String comment;

	private NotificationType notificationType;

	private String operationType;  //send:云端推送，get：主动从云端拉取

	private String uuid; //云端消息的uuid

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getBoardNO() {
		return boardNO;
	}

	public void setBoardNO(String boardNO) {
		this.boardNO = boardNO;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}
}
