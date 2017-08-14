package cn.mrobot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

/**
 * Created by jelynn on 2016/5/23.
 */
public class FileUtils {

	private final static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    public static long copyFile(File srcFile, File desFile) throws Exception {
        long time = new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(srcFile);
        FileOutputStream out = new FileOutputStream(desFile);
        byte[] buffer = new byte[length];
        while (true) {
            int ins = in.read(buffer);
            if (ins == -1) {
                in.close();
                out.flush();
                out.close();
                return new Date().getTime() - time;
            } else
                out.write(buffer, 0, ins);
        }
    }

    /**
     * 将版本信息写入本地文件
     *
     * @param text
     * @param path
     */
    public static void writeToTXT(String text, String path) {
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                createFileAndDir(path);
            }
            fos = new FileOutputStream(file);
            byte[] bytes = text.getBytes();
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            logger.error(" write version info file ", e);
        } catch (IOException e) {
            logger.error(" write version info file ", e);
        } catch (Exception e) {
            logger.error(" write version info file ", e);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error(" write version info file ", e);
                }
            }
        }
    }

    /**
     * 读取txt文件中的内容
     *
     * @param path
     * @return
     */
    public static String readTXT(String path) {
        logger.info("readTXT  file path={}", path);
        String res = "";
        FileInputStream fis = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                fis = new FileInputStream(path);
                int length = fis.available();
                byte[] buffer = new byte[length];
                fis.read(buffer);
                res = new String(buffer, "UTF-8");
            }
        } catch (FileNotFoundException e) {
            logger.error(" read version txt error ", e);
        } catch (Exception e) {
            logger.error(" read version txt error ", e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error(" read version txt error ", e);
                }
            }
        }
        return res;
    }

    private static void createFileAndDir(String path) {
        File file = new File(path);
        String dir = path.substring(0, path.lastIndexOf("/"));
        File fileDir = new File(dir);
        fileDir.mkdirs();
        try {
            file.createNewFile();
        } catch (Exception e) {
            logger.error(" create file error ", e);
        }
    }

    /**
     * 根据路径获取文件
     *
     * @param directory
     * @param names
     * @return
     */
    public static File getFile(File directory, String... names) {
        if (directory == null) {
            throw new NullPointerException("directorydirectory must not be null");
        } else if (names == null) {
            throw new NullPointerException("names must not be null");
        } else {
            File file = directory;
            String[] arr$ = names;
            int len$ = names.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String name = arr$[i$];
                file = new File(file, name);
            }

            return file;
        }
    }

    public static File getFile(String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        } else {
            File file = null;
            String[] arr$ = names;
            int len$ = names.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String name = arr$[i$];
                if (file == null) {
                    file = new File(name);
                } else {
                    file = new File(file, name);
                }
            }

            return file;
        }
    }

    /**
     * 向文件追加写入内容
     *
     * @param file
     * @param conent
     */
    public static void appendWriteToFile(String file, String conent) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            try {
                out.write(conent + "\n");
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向文件覆盖写入内容
     *
     * @param file
     * @param conent
     */
    public static void writeToFile(String file, String conent) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false)));
            try {
                out.write(conent);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本文件内容到一个字符串中
     *
     * @param file
     * @return
     */
    public static String readFileAsString(String file) {
        String ret = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String readLine;
            do {
                readLine = bufferedReader.readLine();
                if (readLine != null) {
                    stringBuffer.append(readLine);
                }
            } while (readLine != null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (fileReader != null)
                    fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ret = stringBuffer.toString();
        return ret;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful. If a
     * deletion fails, the method stops attempting to delete and returns
     * "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();//递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

//    /**
//     * 递归删除目录下除特定文件名或者后缀的所有文件及子目录下所有文件
//     *
//     * @param dir 将要删除的文件目录
//     * fileName 不删除的文件名或后缀
//     */
//    public static boolean deleteDirExcept(File dir, String fileName) {
//        if (dir.isDirectory()) {
//            String[] children = dir.list();//递归删除目录中的子目录下
//            for (int i = 0; i < children.length; i++) {
//                boolean success = deleteDirExcept(new File(dir, children[i]), fileName);
//                if (!success) {
//                    return false;
//                }
//            }
//        }
//        if(dir.getName().indexOf(fileName) >=0){
//            return false;
//        }
//        return dir.delete();
//    }
//
//    public static void main(String[] args) {
//        deleteDirExcept(new File("E:\\share\\map_server\\maps"),"agv");
//    }
    /**
     * 将地图名和场景名封装成单一的key
     */
    public static String parseMapAndSceneName(String mapName, String sceneName, Long storeId) {
        return mapName + "_" + sceneName + "_" + storeId;
    }

    /**
     * 将地图名和场景名封装成单一的key反解析
     */
    public static String[] resolveMapAndSceneName(String key) {
        return key.split("_");
    }
}
