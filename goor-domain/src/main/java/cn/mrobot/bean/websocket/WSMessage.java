package cn.mrobot.bean.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

/**
 * Created by Jelynn on 2017/8/17.
 */
public class WSMessage implements Serializable {

    private String title;
    private Object body;
    private String deviceId; //机器人code，多个以逗号隔开
    private String module;
    private WSMessageType messageType;
    private String userId; //机器人code，多个以逗号隔开

    public WSMessage() {
    }

    public WSMessage(String title, WSMessageType messageType, Object body, String deviceId, String module,String userId) {
        this.title = title;
        this.messageType = messageType;
        this.body = body;
        this.deviceId = deviceId;
        this.module = module;
        this.userId = userId;
    }

    public WSMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(WSMessageType messageType) {
        this.messageType = messageType;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean is(WSMessageType messageType) {
        return this.messageType == messageType;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static WSMessage parse(String text) throws InvalidMessageException {
        try {
            return JSON.parseObject(text, WSMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidMessageException();
        }
    }

    public static String toRawString(WSMessage message) {
        return JSON.toJSONString(message);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private WSMessageType messageType = WSMessageType.NOTIFICATION;
        private String title;
        private Object body;
        private String deviceId;
        private String module;
        private String userId;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder body(Object data) {
            this.body = data;
            return this;
        }

        public Builder messageType(WSMessageType type) {
            this.messageType = type;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder module(String module) {
            this.module = module;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }


        public Builder json(Object data) {
            this.body = JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect);
            return this;
        }

        public WSMessage build() {
            WSMessage message = new WSMessage();
            message.setTitle(title);
            message.setMessageType(messageType);
            message.setDeviceId(deviceId);
            message.setBody(body);
            message.setModule(module);
            message.setUserId(userId);
            return message;
        }
    }
}
