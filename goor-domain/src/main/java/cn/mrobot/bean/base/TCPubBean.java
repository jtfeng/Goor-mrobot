package cn.mrobot.bean.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by abel on 17-7-12.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TCPubBean implements Serializable {
    private static final long serialVersionUID = -8579283759264722191L;

    private String pub_name;
    private String data;
    private String error_code;
    private String msg;
    private String topicName;//需要发布的topic名称，必须字段
    private String topicType;//需要发布的topic消息类型，必须字段

}
