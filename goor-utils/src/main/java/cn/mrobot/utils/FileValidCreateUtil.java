package cn.mrobot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chay on 2016/9/6.
 */
public class FileValidCreateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileValidCreateUtil.class);

    /**
     * 保存base64图片
     * @param imgBase64
     * @param path 保存路径及文件名
     */
    public static Boolean createBase64Img(
            String imgBase64,
            String path){
        // 对字节数组字符串进行Base64解码并生成图片
        if (imgBase64 == null) { // 图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            //拆分头和数据，java解码不能有头，而html显示必须有头
            String[] base64String = imgBase64.split(",");
            if(base64String.length != 2){
                return false;
            }
            byte[] b = decoder.decodeBuffer(base64String[1]);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片
            File dest = FileUtils.getFile(path);
            if(!dest.exists()){
                FileUtils.getFile(dest.getParent()).mkdirs();
                dest.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();

//            ByteArrayInputStream bais = new ByteArrayInputStream(b);
//            BufferedImage bi1 =ImageIO.read(bais);
//            ImageIO.write(bi1, "png", dest);//不管输出什么格式图片，此处不需改动

            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return false;
        }
    }

    //测试
//    public static void main(String[] args) {
//        try {
//            System.out.println(fileMD5("D:/Documents/muye/desktop/Cooky-C001M01A007-RK3288_V2-20161130OTA.zip"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     *
     * @param inputFile
     * @return
     * @throws IOException
     */
    public static String fileMD5(String inputFile) throws IOException {
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest =MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer =new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0);
            // 获取最终的MessageDigest
            messageDigest= digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            String md5 = byteArrayToHex(resultByteArray);
            LOGGER.info("文件"+ inputFile +"的md5:" + md5);
            return md5;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(),e);
            return null;
        } finally {
            try {
                if(digestInputStream != null)
                    digestInputStream.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
            try {
                if(fileInputStream != null)
                    fileInputStream.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
    }

    //下面这个函数用于将字节数组换成成16进制的字符串
    public static String byteArrayToHex(byte[] byteArray) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < byteArray.length; n++) {
            stmp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < byteArray.length - 1) {
                hs = hs + "";
            }
        }
        // return hs.toUpperCase();
        return hs;

        // 首先初始化一个字符数组，用来存放每个16进制字符
      /*char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
      // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
      char[] resultCharArray =new char[byteArray.length * 2];
      // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
      int index = 0;
      for (byte b : byteArray) {
         resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
         resultCharArray[index++] = hexDigits[b& 0xf];
      }
      // 字符数组组合成字符串返回
      return new String(resultCharArray);*/

    }



}
