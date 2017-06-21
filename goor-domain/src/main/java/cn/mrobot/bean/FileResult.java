package cn.mrobot.bean;

/**
 * Created by Selim on 2017/1/4.
 */
public class FileResult {

    private int status; //状态码 0为ok 1为失败

    private boolean exist;

    private boolean temp;

    private String fileName;

    private long size;

    public FileResult() {
    }

    public FileResult(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
