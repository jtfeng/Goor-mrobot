package cn.muye.base.bean;

import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
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
public class MessageView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuId;//uuid

    private String receiverId;//接收者ID或机器序列号

    private String messageStatusType;//消息状态

    private String relyMessage;//回执消息

    private String messageText;//文本消息

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;//发送时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//接收时间

    private boolean success;//是否发送成功


    public MessageView(OffLineMessage message){
        BeanUtils.copyProperties(message, this);
        if(message != null){
            if(null != message.getMessageStatusType() && message.getMessageStatusType() > 0){
                this.messageStatusType = MessageStatusType.getType(message.getMessageStatusType()).getName();
            }
        }
    }

}

