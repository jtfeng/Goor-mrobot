package cn.mrobot.bean.slam;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 命令请求body
 * Created by jelynn on 2017/6/3.
 */

public class SlamRequestBody<T> {

    //方法名
    @JSONField(name = "pub_name")
    private String pubName;

    @JSONField(name = "data")
    private T data;

    @JSONField(name = "uuid")
    private String uuid;

    @JSONField(name = "publisher")
    private String publisher;

    public SlamRequestBody(String pubName) {
        this.pubName = pubName;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "SlamRequestBody{" +
                "pubName='" + pubName + '\'' +
                "publisher='" + publisher + '\'' +
                ", data=" + data +
                '}';
    }

}
