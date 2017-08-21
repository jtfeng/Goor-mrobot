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
    private String userId;
    private WSMessageType messageType;

    public WSMessage() {
    }

    public WSMessage(String title, WSMessageType messageType, Object body, String userId) {
        this.title = title;
        this.messageType = messageType;
        this.body = body;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
            message.setUserId(userId);
            message.setBody(body);
            return message;
        }
    }
}
