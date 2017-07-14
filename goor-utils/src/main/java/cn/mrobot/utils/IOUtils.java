package cn.mrobot.utils;

/**
 * Created by enva on 2017/7/14.
 */
import java.io.Closeable;

public final class IOUtils {
    public IOUtils() {
    }

    public static void close(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (Exception var2) {
                ;
            }
        }

    }

}
