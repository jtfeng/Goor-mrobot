package cn.mrobot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * Created with IntelliJ IDEA.
 * Project Name : tinker-agent
 * User: Jelynn
 * Date: 2017/4/10
 * Time: 13:55
 * Describe:
 * Version:1.0
 */
public class ZipUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 递归压缩文件夹
     *
     * @param srcRootDir 压缩文件夹根目录的子路径
     * @param file       当前递归压缩的文件或目录对象
     * @param zos        压缩文件存储对象
     * @throws Exception
     */
    private static void zip(String srcRootDir, File file, ZipOutputStream zos) {
        if (file == null) {
            return;
        }
        try {
            //如果是文件，则直接压缩该文件
            if (file.isFile()) {
                int count, bufferLen = 1024 * 2;
                byte data[] = new byte[bufferLen];

                //获取文件相对于压缩文件夹根目录的子路径
                String subPath = file.getAbsolutePath();
                int index = subPath.indexOf(srcRootDir);
                if (index != -1) {
                    subPath = subPath.substring(srcRootDir.length() + File.separator.length());
                }
                ZipEntry entry = new ZipEntry(subPath);
                zos.putNextEntry(entry);
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                while ((count = bis.read(data, 0, bufferLen)) != -1) {
                    zos.write(data, 0, count);
                }
                bis.close();
                zos.closeEntry();
            }
            //如果是目录，则压缩整个目录
            else {
                //压缩目录中的文件或子目录
                File[] childFileList = file.listFiles();
                for (int n = 0; n < childFileList.length; n++) {
                    childFileList[n].getAbsolutePath().indexOf(file.getAbsolutePath());
                    zip(srcRootDir, childFileList[n], zos);
                }
            }
            zos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("文件压缩出错", e);
        }
    }

    /**
     * 对文件或文件目录进行压缩
     *
     * @param zipPath    要压缩的源文件路径。
     * @param savePath    压缩文件保存的路径。注意：zipPath不能是srcPath路径下的子文件夹
     * @param zipFileName 压缩文件名
     * @throws Exception
     */
    public static boolean zip(String zipPath, String savePath, String zipFileName) throws Exception {
        if (StringUtil.isNullOrEmpty(savePath) || StringUtil.isNullOrEmpty(zipPath) || StringUtil.isNullOrEmpty(zipFileName)) {
            LOGGER.error("参数不能为空");
            return false;
        }
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            final File srcFile = new File(zipPath);
            //判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
            if (srcFile.isDirectory() && savePath.indexOf(zipPath) != -1) {
                LOGGER.error("savePath must not be the child directory of zipPath");
                return false;
            }

            //判断压缩文件保存的路径是否存在，如果不存在，则创建目录
            File saveDir = new File(savePath);
            if (!saveDir.exists() || !saveDir.isDirectory()) {
                saveDir.mkdirs();
            }

            //创建压缩文件保存的文件对象
            String saveFilePath = savePath + File.separator + zipFileName;
            File saveFile = new File(saveFilePath);
            if (saveFile.exists()) {
                //删除已存在的目标文件
                saveFile.delete();
            }

            cos = new CheckedOutputStream(new FileOutputStream(saveFile), new CRC32());
            zos = new ZipOutputStream(cos);

            //如果只是压缩一个文件，则需要截取该文件的父目录
            String srcRootDir = zipPath;
            if (srcFile.isFile()) {
                int index = zipPath.lastIndexOf(File.separator);
                if (index != -1) {
                    srcRootDir = zipPath.substring(0, index);
                }
            }
            //调用递归压缩方法进行目录或文件压缩
            zip(srcRootDir, srcFile, zos);
            return true;
        } catch (Exception e) {
            LOGGER.error("压缩文件出错", e);
        } finally {
            if (null != zos) {
                zos.close();
            }
        }
        return false;
    }

    /**
     * 解压缩zip包
     *
     * @param zipFilePath        zip文件的全路径
     * @param unzipFilePath      解压后的文件保存的路径
     * @param includeZipFileName 解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含
     */
    @SuppressWarnings("unchecked")
    public static boolean unzip(String zipFilePath, String unzipFilePath, boolean includeZipFileName) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ZipFile zip = null;
        try {
            if (StringUtil.isNullOrEmpty(zipFilePath) || StringUtil.isNullOrEmpty(unzipFilePath)) {
                LOGGER.error("参数不能为空");
            }
            File dir = new File(unzipFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File zipFile = new File(zipFilePath);
            //如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径
            if (includeZipFileName) {
                String fileName = zipFile.getName();
                if (!StringUtil.isNullOrEmpty(fileName)) {
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                }
                unzipFilePath = unzipFilePath + File.separator + fileName;
            }

            //开始解压
            ZipEntry entry = null;
            String entryFilePath = null, entryDirPath = null;
            File entryFile = null, entryDir = null;
            int index = 0, count = 0, bufferSize = 1024 * 2;
            byte[] buffer = new byte[bufferSize];
            zip = new ZipFile(zipFile);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
            //循环对压缩包里的每一个文件进行解压
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                //构建压缩包中一个文件解压后保存的文件全路径
                entryFilePath = unzipFilePath + File.separator + entry.getName();
                //构建解压后保存的文件夹路径
                index = entryFilePath.lastIndexOf(File.separator);
                if (index != -1) {
                    entryDirPath = entryFilePath.substring(0, index);
                } else {
                    entryDirPath = "";
                }
                entryDir = new File(entryDirPath);
                //如果文件夹路径不存在，则创建文件夹
                if (!entryDir.exists() || !entryDir.isDirectory()) {
                    entryDir.mkdirs();
                }
                //创建解压文件
                entryFile = new File(entryFilePath);
                if (entryFile.exists()) {
                    //删除已存在的目标文件
                    entryFile.delete();
                }
                String parent = entryFile.getParent();
                File parentFile = new File(parent);
                if (!parentFile.exists())
                    parentFile.mkdirs();

                entryFile.createNewFile();
                //写入文件
                bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                bis = new BufferedInputStream(zip.getInputStream(entry));
                while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("解压文件出错，zipFilePath = " + zipFilePath, e);
            return false;
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
                if (null != bis) {
                    bis.close();
                }
                if (null != zip) {
                    zip.close();
                }
            } catch (IOException e) {
                LOGGER.error("解压文件出错，zipFilePath = " + zipFilePath, e);
            }
        }
    }

    public static void main(String[] args) {
//        String zipPath = "E:\\documents\\upload";
//        String savePath = "E:\\documents";
//        String zipFileName = "upload.zip";
//        try {
//            zip(zipPath, savePath, zipFileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String zipFilePath = "E:\\share\\map_server\\maps\\maps_2017-07-15_05-28-13.zip";
        String unzipFilePath = "E:\\ziptest\\unzipPath";
        try {
            unzip(zipFilePath, unzipFilePath, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
