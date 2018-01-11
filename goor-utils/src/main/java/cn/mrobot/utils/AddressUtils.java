package cn.mrobot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

/**
 * @author Jelynn
 * @date 2018/1/11
 */
public class AddressUtils {

    private static final Logger logger = LoggerFactory.getLogger(AddressUtils.class);

    public static String getIp(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null) {
            if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null) {
            if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null) {
            if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null) {
            if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        ip = request.getRemoteAddr();
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public static String getMac(String sip) throws Exception {
        final UdpGetClientMacAddr umac = new UdpGetClientMacAddr(sip);
        //---长时间获取不到MAC地址则放弃
        ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<String> call = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return umac.GetRemoteMacAddr();
            }
        };
        try {
            Future<String> future = exec.submit(call);
            String smac = future.get(2, TimeUnit.SECONDS);
            return smac;
        } catch (TimeoutException ex) {
            logger.info("获取MAC地址超时");
            ex.printStackTrace();
        }
        // 关闭线程池
        exec.shutdown();
        //---
        return null;
    }
}
