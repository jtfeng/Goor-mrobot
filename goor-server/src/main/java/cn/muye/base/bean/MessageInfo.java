package cn.muye.base.bean;

import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.RandomUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by enva on 2017/5/9.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuId;//uuid

    private String senderId;//发送ID或机器序列号

    private String receiverId;//接收者ID或机器序列号

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

    public MessageInfo(String senderId, String receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.uuId = RandomUtil.getUUID();
        this.sendTime = new Date();
    }

    public MessageInfo(OffLineMessage message){
        BeanUtils.copyProperties(message, this);
        if(message != null){
            if(!StringUtil.isEmpty(message.getMessageType())){
                this.messageType = MessageType.valueOf(message.getMessageType());
            }
            if(null != message.getMessageStatusType() && message.getMessageStatusType() > 0){
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
            if(null != message.getMessageStatusType() && message.getMessageStatusType() > 0){
                this.messageStatusType = MessageStatusType.getType(message.getMessageStatusType());
            }
        }
    }

}

