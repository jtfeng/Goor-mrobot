package cn.muye.model.message;

import cn.muye.bean.MessageInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by enva on 2017/5/9.
 */
public class ReceiveMessage implements Serializable {

    private Long id;//自动生成Id,可做消息编号，和发送者ID配合使用

    private String senderId;//发送者ID

    private String sendDeviceType;//发送的设备类型

    private String receiverId;//接收者ID

    private String receiverDeviceType;//接收的设备类型

    private String webSocketId;//webSocketId，暂定回执时使用

    private boolean receiptWebSocket;//是否给webSocket发送消息，暂定回执时使用

    private boolean finish;//是否完成消息处理

    private Integer messageKind;//消息种类，默认为0，0：文本消息，1：二进制消息

    private String messageType;//消息类型

    private boolean failResend;//是否是否需要失败重新发送，true：需要，false：不需要

    private Integer sessionId;//sessionId

    private Integer messageStatusType;//下载状态默认为0，1：未下载，2：下载完成，3：发送ros消息完成

    private String version;//版本号

    private String relyMessage;//回执消息

    private String messageText;//文本消息

    private byte[] messageBinary;//二进制消息

    private Integer sendCount;//发送次数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;//发送时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//接收时间

    private boolean success;//是否发送成功

    public ReceiveMessage(){

    }

    public ReceiveMessage(Integer messageStatusType){
        this.messageStatusType = messageStatusType;
    }

    public ReceiveMessage(boolean success){
        this.success = success;
    }

    public ReceiveMessage(Integer messageStatusType, boolean success){
        this.messageStatusType = messageStatusType;
        this.success = success;
    }

    public ReceiveMessage(MessageInfo messageInfo){
        BeanUtils.copyProperties(messageInfo, this);
        if(messageInfo != null){
            if(messageInfo.getMessageType() != null && messageInfo.getMessageType().name() != null){
                this.messageType = messageInfo.getMessageType().name();
            }
            if(messageInfo.getSendDeviceType() != null && messageInfo.getSendDeviceType().name() != null){
                this.sendDeviceType = messageInfo.getSendDeviceType().name();
            }
            if(messageInfo.getReceiverDeviceType() != null && messageInfo.getReceiverDeviceType().name() != null){
                this.receiverDeviceType = messageInfo.getReceiverDeviceType().name();
            }
            if(messageInfo.getMessageStatusType() != null && messageInfo.getMessageStatusType().getIndex() > 0){
                this.messageStatusType = messageInfo.getMessageStatusType().getIndex();
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSendDeviceType() {
        return sendDeviceType;
    }

    public void setSendDeviceType(String sendDeviceType) {
        this.sendDeviceType = sendDeviceType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverDeviceType() {
        return receiverDeviceType;
    }

    public void setReceiverDeviceType(String receiverDeviceType) {
        this.receiverDeviceType = receiverDeviceType;
    }

    public String getWebSocketId() {
        return webSocketId;
    }

    public void setWebSocketId(String webSocketId) {
        this.webSocketId = webSocketId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public byte[] getMessageBinary() {
        return messageBinary;
    }

    public void setMessageBinary(byte[] messageBinary) {
        this.messageBinary = messageBinary;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isReceiptWebSocket() {
        return receiptWebSocket;
    }

    public void setReceiptWebSocket(boolean receiptWebSocket) {
        this.receiptWebSocket = receiptWebSocket;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isFailResend() {
        return failResend;
    }

    public void setFailResend(boolean failResend) {
        this.failResend = failResend;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRelyMessage() {
        return relyMessage;
    }

    public void setRelyMessage(String relyMessage) {
        this.relyMessage = relyMessage;
    }

    public Integer getMessageStatusType() {
        return messageStatusType;
    }

    public void setMessageStatusType(Integer messageStatusType) {
        this.messageStatusType = messageStatusType;
    }

    public Integer getMessageKind() {
        return messageKind;
    }

    public void setMessageKind(Integer messageKind) {
        this.messageKind = messageKind;
    }
}
