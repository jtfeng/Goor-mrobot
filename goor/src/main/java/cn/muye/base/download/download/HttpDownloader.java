package cn.muye.base.download.download;

import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.FileValidCreateUtil;
import cn.mrobot.utils.ZipUtils;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.download.file.FileHelper;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public class HttpDownloader extends Thread {

    private Logger logger = LoggerFactory.getLogger(HttpDownloader.class);
    private IDownloadInfo info;
    private int maxRetry = 5;
    private Ros ros;
    private MessageInfo messageInfo;
    private CommonInfo commonInfo;
    private ReceiveMessageService receiveMessageService;

    public HttpDownloader(IDownloadInfo info,
                          int maxRetry,
                          Ros ros,
                          MessageInfo messageInfo,
                          CommonInfo commonInfo,
                          ReceiveMessageService receiveMessageService) {
        this.info = info;
        this.maxRetry = maxRetry;
        this.ros = ros;
        this.messageInfo = messageInfo;
        this.commonInfo = commonInfo;
        this.receiveMessageService = receiveMessageService;
    }

    public HttpDownloader(IDownloadInfo info) {
        this.info = info;
    }

    @Override
    public void run() {
        FileCheckPoints chp = getInitedCheckPoint();
        if (chp.totalSize < 0) {
            logger.info(info.getPair().localName + " The size of file is " + chp.totalSize);
            return;
        }
        if (info.isNeedDownload(chp)) {
            RetriveSingleStream[] rss = new RetriveSingleStream[info.getSplitNum()];
            for (int i = 0; i < info.getSplitNum(); i++) {
                rss[i] = new RetriveSingleStream(info,
                        info.getCurCheckPoints(), i, maxRetry);
                Thread th = new Thread(rss[i]);
                th.start();
            }
            boolean isDone = false;
            try {
                while (!isDone) {
                    Thread.sleep(1000);
                    isDone = true;
                    for (int i = 0; i < info.getSplitNum(); i++) {
                        isDone &= rss[i].isDone();
                    }
                }

                this.fileCheck();
//                if (isDone && MessageType.EXECUTOR_UPGRADE.equals(messageInfo.getMessageType())) {//升级下载
//                    this.fileCheck();
//                }
//                if (isDone && MessageType.EXECUTOR_UPGRADE.equals(messageInfo.getMessageType())) {//普通资源下载
//                    this.fileCheck();
//                }
                if (isDone && MessageType.EXECUTOR_MAP.equals(messageInfo.getMessageType())) {//地图下载
//                    this.fileCheck();
                    logger.info("地图文件下载完成，开始解压");
                    unzipMapFile();
                }
                logger.info(info.getPair().localName + " Download is done! isDone==" + isDone + "");
            } catch (InterruptedException e) {
                logger.debug(info.getPair().localName, e);
            } catch (Exception e) {
                logger.debug(info.getPair().localName, e);
            }

        } else {
            //下载完成了，判断：1.是temp文件(则重新执行fileCheck)，2.已经去掉temp的文件(直接发布消息【分为有状态消息和无状态消息】)；
            String localTempPath = info.getPair().getLocalFullPath();
            if (FileHelper.existsFile(localTempPath)) {
                fileCheck();//文件未处理的，再处理一次
            } else if (
//					messageInfo.isFailResend()
//					&&
                    FileHelper.existsFile(localTempPath.substring(0, localTempPath.lastIndexOf(Constant.TEMP)))) {
                updateReplyMessage();//存库下载完成的资源
                if (!StringUtils.isEmpty(commonInfo.getTopicName())) {
                    noticeRos();
                }
            }
//			else if(!messageInfo.isFailResend()
//					&& FileHelper.existsFile(localTempPath.substring(0, localTempPath.lastIndexOf(Constant.TEMP)))){//
//				noticeRos();//未存库下载完成的资源，直接通知
//			}
            else {
                logger.error("-->> no find local file, please check.....");
            }
            if (MessageType.EXECUTOR_MAP.equals(messageInfo.getMessageType())) {//地图下载
                this.fileCheck();
                unzipMapFile();
            }
            logger.info(info.getPair().localName + " Need not to download!");
        }
    }


    private void unzipMapFile() {
        try {
            String localPath = commonInfo.getLocalPath();
            String zipFilePath = localPath + File.separator + commonInfo.getLocalFileName();
            //移动文件到上层目录，删除地图文件夹下所有文件
            String moveFilePath = new File(localPath).getParent() + File.separator + commonInfo.getLocalFileName();
            logger.info("移动文件到上层目录。path= " + moveFilePath);
            long copyTime = FileUtils.copyFile(new File(zipFilePath), new File(moveFilePath));
            if (copyTime <= 0) {
                logger.info("移动文件出错 ");
            }
            FileUtils.deleteDir(new File(localPath));

            boolean unzipFlag = ZipUtils.unzip(moveFilePath, localPath, false);
            if (unzipFlag) {
                //更改文件夹权限 将maps文件夹下所有文件的owner更改为robot
//                String os = System.getProperty("os.name");
//                logger.info("系统,更改权限，os=" + os);
//                if (os.toLowerCase().startsWith("lin")) {
//                    logger.info("Linux系统,更改权限");
////                    Runtime.getRuntime().exec("chmod 777 -R /home/robot/catkin_ws/install/share/map_server/maps");
//                    changePermission(localPath);
//                }
                //更新数据库状态为解压完成
                logger.info("解压完成。回执消息 ");
                messageInfo.setRelyMessage("resource unzip success");
                messageInfo.setMessageStatusType(MessageStatusType.FILE_UMZIP_COMPLETE);
                messageInfo.setSuccess(false);//重新发送回执消息
                ReceiveMessage receiveMessage = new ReceiveMessage(messageInfo);
                receiveMessageService.update(receiveMessage);
            } else {
                logger.info("解压失败");
            }
        } catch (Exception e) {
            logger.info("更新数据库状态为解压失败 ", e);
        }
    }

    public void fileCheck() {
        try {
            String localTempPath = info.getPair().getLocalFullPath();
            if (!FileValidCreateUtil.fileMD5(localTempPath).equals(commonInfo.getMD5())) {//判断md5码是否和云端一致
                logger.error("-->> localMD5 cloudMD5 inequality");
                return;
            }
            String localPath = localTempPath.substring(0, localTempPath.lastIndexOf(Constant.TEMP));
            File file = new File(localPath);
            if (file.exists() && !FileHelper.delete(localPath)) {//删除资源文件
                logger.error("-->> delete local file fail");
                return;
            }

            if (!FileHelper.updateFileName(localTempPath, localPath)) {//修改文件名，去掉.temp后缀
                logger.error("-->> update local file name fail");
                return;
            }
            this.updateReplyMessage();
            //需要发ros的时候，publish to ros
            if (!StringUtils.isEmpty(commonInfo.getTopicName())) {
                noticeRos();
            }
        } catch (IOException e) {
            logger.error("-->> fileCheck error", e);
        }
    }

    public void updateReplyMessage() {
        try {
            messageInfo.setRelyMessage("resource download success");
            messageInfo.setMessageStatusType(MessageStatusType.FILE_DOWNLOAD_COMPLETE);
            messageInfo.setSuccess(false);//重新发送回执消息
            ReceiveMessage receiveMessage = new ReceiveMessage(messageInfo);
            receiveMessageService.update(receiveMessage);
        } catch (Exception e) {
            logger.error("-->> updateReplyMessage Exception", e);
        }
    }


    public void noticeRos() {
        try {
            logger.error("-->>download publishMessage start");
            CommonInfo commonInfo = JSON.parseObject(messageInfo.getMessageText(), CommonInfo.class);
            if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) < TopicConstants.CHECK_HEART_TOPIC_MAX) {
                Topic echo = new Topic(ros, commonInfo.getTopicName(), commonInfo.getTopicType());
                Message toSend = new Message(commonInfo.getPublishMessage());
                echo.publish(toSend);

                ReceiveMessage receiveMessage = new ReceiveMessage(messageInfo);
                receiveMessage.setMessageStatusType(MessageStatusType.PUBLISH_ROS_MESSAGE.getIndex());
                receiveMessage.setSuccess(false);
                receiveMessageService.update(receiveMessage);
            } else {
                logger.error("-->>download publishMessage fail, ros not connect");
                ReceiveMessage receiveMessage = new ReceiveMessage(messageInfo);
                receiveMessage.setMessageStatusType(MessageStatusType.ROS_OFF_LINE.getIndex());
                receiveMessage.setSuccess(false);
                receiveMessageService.update(receiveMessage);
            }
        } catch (Exception e) {
            logger.error("-->> download publishMessage Exception", e);
        }
    }

    public static FileCheckPoints initCheckPoint(int splitNum, long totalSize, long timeStamp) {
        long[] startPos = new long[splitNum];
        long[] endPos = new long[splitNum];
        for (int i = 0, len = startPos.length; i < len; i++) {
            long size = i * (totalSize / len);
            startPos[i] = size;
            // 设置最后一个结束点的位置
            if (i == len - 1) {
                endPos[i] = totalSize;
            } else {
                size = (i + 1) * (totalSize / len);
                endPos[i] = size;
            }
        }
        FileCheckPoints chp = new FileCheckPoints();
        chp.setEndPos(endPos);
        chp.setStartPos(startPos);
        chp.totalSize = totalSize;
        chp.timestamp = timeStamp;
        return chp;
    }

    private FileCheckPoints getInitedCheckPoint() {
        long fileLength = -1;
        long timeStamp = -1;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(this.info.getPair().remoteUrl);
            conn = (HttpURLConnection) url.openConnection();
            HttpDownloader.RetriveSingleStream.setHeader(conn);
            int stateCode = conn.getResponseCode();
            // 判断http status是否为HTTP/1.1 206 Partial Content或者200 OK
            if (stateCode != HttpURLConnection.HTTP_OK
                    && stateCode != HttpURLConnection.HTTP_PARTIAL) {
                logger.warn(info.getPair().localName + " #Error Code:# "
                        + stateCode);
                fileLength = -2;
            } else if (stateCode >= 400) {
                logger.warn(info.getPair().localName + " #Error Code:# "
                        + stateCode);
                fileLength = -2;
            } else {
                // 获取长度
                fileLength = conn.getContentLengthLong();
                timeStamp = conn.getLastModified();
                logger.info(info.getPair().localName + " #FileLength:# "
                        + fileLength);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        FileCheckPoints chp;
        if (fileLength > 0) {
            chp = initCheckPoint(info.getSplitNum(), fileLength, timeStamp);
            chp.timestamp = timeStamp;
        } else {
            chp = new FileCheckPoints();
        }
        return chp;
    }

    /**
     * bug fixed change the RandomAccessFile size
     */


    protected static class RetriveSingleStream implements Runnable {
        private boolean isDone = false;
        private FileCheckPoints chp;
        private int curIndex;
        private SaveFileItem file;
        private long startPos;
        private long endPos;
        byte[] buffer = new byte[1024 * 12];
        private IDownloadInfo __info;
        private int maxRetry;
        private Logger logger = LoggerFactory.getLogger(RetriveSingleStream.class);

        public boolean isDone() {
            return isDone;
        }

        public RetriveSingleStream(IDownloadInfo info, FileCheckPoints chp,
                                   int curIndex, int maxRetry) {
            this.__info = info;
            this.chp = chp;
            this.curIndex = curIndex;
            this.startPos = chp.getStartPos()[curIndex];
            this.endPos = chp.getEndPos()[curIndex];
            this.maxRetry = maxRetry;
        }

        @Override
        public void run() {
            InputStream in = null;
            HttpURLConnection conn = null;
            int curRetry = 0;

            while (curRetry < maxRetry && !isDone) {
                try {
                    URL url = new URL(__info.getPair().remoteUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(30000);
                    setHeader(conn);
                    String property = "bytes=" + startPos + "-";
                    conn.setRequestProperty("RANGE", property);
                    logger.info(__info.getPair().localName + " #Block"
                            + (curIndex + 1) + "# begin downloading...");
                    int length;
                    long counter = 0;
                    InputStream is = conn.getInputStream();
                    file = new SaveFileItem(__info.getPair().getLocalFullPath(), startPos);
                    //--bug fixed
                    file.setLength(__info.getCurCheckPoints().totalSize);
                    //--bug fixed
                    while (!isDone && startPos < endPos && (length = is.read(buffer)) > 0) {
                        startPos += file.write(buffer, 0, length);
                        counter += 1;
                        chp.getStartPos()[curIndex] = Math.min(startPos, endPos);
                        if (counter % 20 == 0) {
                            __info.writeInfo(chp);
                            logger.info(__info.getPair().localName + " #Block"
                                    + (curIndex + 1) + "# download "
                                    + getPercentage() + "%...");
                            Thread.yield();
                        }
                    }
                    __info.writeInfo(chp);
                    isDone = true;
                } catch (IOException e) {
                    isDone = false;
                    logger.debug(__info.getPair().localName, e);
                } finally {
                    if (!isDone) {
                        curRetry++;
                        logger.debug(__info.getPair().localName + " download failed, retry again!");
                        if (curRetry >= maxRetry) {
                            //保证循环跳出
                            isDone = true;
                        }
                    } else {
                        curRetry = maxRetry;
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (file != null) {
                            file.close();
                        }
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        logger.debug(__info.getPair().localName, e);
                    }
                }
            }
        }


        public static void setHeader(URLConnection conn) {
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 BIDUBrowser/7.0 Safari/537.36");
            conn.setRequestProperty("Accept-Language",
                    "en-us,en;q=0.7,zh-cn;q=0.3");
            conn.setRequestProperty("Accept-Encoding", "utf-8");
            conn.setRequestProperty("Accept-Charset",
                    "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            conn.setRequestProperty("Keep-Alive", "300");
            conn.setRequestProperty("connnection", "keep-alive");
            // conn.setRequestProperty("If-Modified-Since",
            // "Fri, 02 Jan 2009 17:00:05 GMT");
            // conn.setRequestProperty("If-None-Match",
            // "\"1261d8-4290-df64d224\"");
            conn.setRequestProperty("Cache-conntrol", "max-age=0");
            conn.setRequestProperty("Referer", "http://www.baidu.com");
        }

        private int getPercentage() {
            long total = 0;
            for (int i = 0; i < chp.getSplit(); i++) {
                total += chp.getEndPos()[i] - chp.getStartPos()[i];
            }
            return (int) ((chp.totalSize - total) * 100 / chp.totalSize);
        }

    }
}
