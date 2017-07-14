package cn.mrobot.utils;

/**
 * Created by enva on 2017/7/14.
 */
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class MD5Utils {
    public MD5Utils() {
    }

    public static String encrypt(File file) {
        FileInputStream in = null;

        String var3;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            byte[] buffer = new byte[10240];

            int readLen;
            while((readLen = in.read(buffer)) != -1) {
                digest.update(buffer, 0, readLen);
            }

            String var5 = toHex(digest.digest());
            return var5;
        } catch (Exception var9) {
            var3 = "";
        } finally {
            IOUtils.close(in);
        }

        return var3;
    }

    public static String encrypt(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes(Charset.forName("UTF-8")));
            return toHex(digest.digest());
        } catch (Exception var2) {
            return "";
        }
    }

    public static String encrypt(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            return toHex(digest.digest());
        } catch (Exception var2) {
            return "";
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            buffer.append(Character.forDigit((bytes[i] & 240) >> 4, 16));
            buffer.append(Character.forDigit(bytes[i] & 15, 16));
        }

        return buffer.toString();
    }

    public static String hmacSha1(String data, String encryptKey) {
        String HMAC_SHA1 = "HmacSHA1";
        SecretKeySpec signingKey = new SecretKeySpec(encryptKey.getBytes(Charset.forName("UTF-8")), "HmacSHA1");

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            mac.update(data.getBytes(Charset.forName("UTF-8")));
            return toHex(mac.doFinal());
        } catch (Exception var5) {
            return "";
        }
    }

    public static String sha1(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return toHex(digest.digest(data.getBytes(Charset.forName("UTF-8"))));
        } catch (Exception var2) {
            return "";
        }
    }
}