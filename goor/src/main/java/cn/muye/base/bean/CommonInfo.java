package cn.muye.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 通用文件下载bean
 * Created by enva on 2017/5/9.
 */
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
public class CommonInfo implements Serializable {

    private String MD5;//md5,有文件时，必须字段

    private String remoteFileUrl;//下载路径,有文件时，必须字段

    private String localPath;//本地路径,有文件时，必须字段

    private String localFileName;//本地文件名称,有文件时，必须字段

    private String topicName;//需要发布的topic名称，必须字段

    private String topicType;//需要发布的topic消息类型，必须字段

    private String publishMessage;//需要发布的内容，必须字段

}
