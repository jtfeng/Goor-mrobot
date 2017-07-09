package cn.muye.base.bean;

import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by enva on 2017/5/9.
 */

public class MessageInfo implements Serializable {

    private String uuId;//uuid

    private String senderId;//发送ID或机器序列号

    private String receiverId;//接收者ID或机器序列号，发送给全部机器时，此值为空，否则为某台序列号

    private Integer messageKind;//消息种类，默认为0，0：文本消息，1：二进制消息

    private MessageType messageType;//消息类型

    private MessageStatusType messageStatusType;//消息状态

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

    public MessageInfo(){

    }

    public MessageInfo(OffLineMessage message){
        BeanUtils.copyProperties(message, this);
        if(message != null){
            if(!StringUtil.isEmpty(message.getMessageType())){
                this.messageType = MessageType.valueOf(message.getMessageType());
            }
            if(message.getMessageStatusType() > 0){
                this.messageStatusType = MessageStatusType.getType(message.getMessageStatusType());
            }
        }
    }

    public MessageInfo(ReceiveMessage message){
        BeanUtils.copyProperties(message, this);
        if(message != null){
            if(!StringUtil.isEmpty(message.getMessageType())){
                this.messageType = MessageType.valueOf(message.getMessageType());
            }
            if(message.getMessageStatusType() > 0){
                this.messageStatusType = MessageStatusType.getType(message.getMessageStatusType());
            }
        }
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getMessageKind() {
        return messageKind;
    }

    public void setMessageKind(Integer messageKind) {
        this.messageKind = messageKind;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageStatusType getMessageStatusType() {
        return messageStatusType;
    }

    public void setMessageStatusType(MessageStatusType messageStatusType) {
        this.messageStatusType = messageStatusType;
    }

    public String getRelyMessage() {
        return relyMessage;
    }

    public void setRelyMessage(String relyMessage) {
        this.relyMessage = relyMessage;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
