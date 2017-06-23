package cn.mrobot.bean.resource;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by Selim on 2017/6/12.
 */
@Table(name = "RE_RESOURCE")
public class Resource extends BaseBean{

    private Integer resourceType; //资源类型

    private String originName;  //原始名

    private String generateName;  //生成名

    private String fileType; //文件类型

    private String md5; //md5码

    private Long fileSize; //文件大小

    private String path; //文件的相对路径

    private String content; //备注 添加版本？

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Resource() {
    }

    public Resource(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Long getId() {
        return id;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getGenerateName() {
        return generateName;
    }

    public void setGenerateName(String generateName) {
        this.generateName = generateName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
