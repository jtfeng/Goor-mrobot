package cn.mrobot.utils;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http辅助工具类
 *
 * @author dj
 */
public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final String DEFAULT_CHARSET = "UTF-8";//默认请求编码
    private static final int DEFAULT_SOCKETTIMEOUT = 5000;//默认等待响应时间(毫秒)
    private static final int DEFAULT_RETRY_TIMES = 0;//默认执行重试的次数

    public HttpClientUtil() {
    }

    /**
     * 创建一个默认的可关闭的HttpClient
     *
     * @return
     */
    public static CloseableHttpClient createHttpClient() {
        return createHttpClient(DEFAULT_RETRY_TIMES, DEFAULT_SOCKETTIMEOUT);
    }

    /**
     * 创建一个可关闭的HttpClient
     *
     * @param socketTimeout 请求获取数据的超时时间
     * @return
     */
    public static CloseableHttpClient createHttpClient(int socketTimeout) {
        return createHttpClient(DEFAULT_RETRY_TIMES, socketTimeout);
    }

    /**
     * 创建一个可关闭的HttpClient
     *
     * @param socketTimeout 请求获取数据的超时时间
     * @param retryTimes    重试次数，小于等于0表示不重试
     * @return
     */
    public static CloseableHttpClient createHttpClient(int retryTimes, int socketTimeout) {
        Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(5000);// 设置连接超时时间，单位毫秒
        builder.setConnectionRequestTimeout(1000);// 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
        builder.setSocketTimeout(socketTimeout);// 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
        RequestConfig defaultRequestConfig = builder.setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
        // 开启HTTPS支持
        enableSSL();
        // 创建可用Scheme
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
        // 创建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (retryTimes > 0) {
            setRetryHandler(httpClientBuilder, retryTimes);
        }
        CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
        return httpClient;
    }

    /**
     * 执行HttpGet请求
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param url             请求的远程地址
     * @param reffer          reffer信息，可传null
     * @param cookie          cookies信息，可传null
     * @param charset         请求编码，默认UTF8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String executeGet(CloseableHttpClient httpClient, String accessToken, String url, String reffer, String cookie, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            HttpGet get = new HttpGet(url);
            if (cookie != null && !"".equals(cookie)) {
                get.setHeader("Cookie", cookie);
            }
            if (accessToken != null && !"".equals(accessToken)) {
                get.setHeader("Authorization", accessToken);
            }
            if (reffer != null && !"".equals(reffer)) {
                get.setHeader("Reffer", reffer);
            }
            charset = getCharset(charset);

            httpResponse = httpClient.execute(get);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 执行HttpPost请求
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param url             请求的远程地址
     * @param paramsObj       提交的参数信息，目前支持Map,和String(JSON\xml)
     * @param reffer          reffer信息，可传null
     * @param contentType     contentType信息，可传null
     * @param authorization   authorization信息，可传null
     * @param cookie          cookies信息，可传null
     * @param charset         请求编码，默认UTF8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String executePost(CloseableHttpClient httpClient, String url, Object paramsObj, String reffer, String cookie, String contentType, String authorization, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            HttpPost post = new HttpPost(url);
            if (cookie != null && !"".equals(cookie)) {
                post.setHeader("Cookie", cookie);
            }
            if (reffer != null && !"".equals(reffer)) {
                post.setHeader("Reffer", reffer);
            }
            if (contentType != null && !"".equals(contentType)) {
                post.setHeader("Content-Type", contentType);
            }
            if (authorization != null && !"".equals(authorization)) {
                post.setHeader("Authorization", authorization);
            }
            charset = getCharset(charset);
            // 设置参数
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(5000).build();
            post.setConfig(requestConfig);
            HttpEntity httpEntity = getEntity(paramsObj, charset);
            if (httpEntity != null) {
                post.setEntity(httpEntity);
            }
            httpResponse = httpClient.execute(post);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e2) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    /**
     * 执行文件上传
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param remoteFileUrl   远程接收文件的地址
     * @param localFilePath   本地文件地址
     * @param charset         请求编码，默认UTF-8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String executeUploadFile(CloseableHttpClient httpClient, String remoteFileUrl, String localFilePath, long jumpSize, String charset, boolean closeHttpClient, String otherInfo) throws ClientProtocolException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            // 把文件转换成流对象FileBody
            File localFile = new File(localFilePath);
            System.out.println(localFile.getName());
            InputStream in = new FileInputStream(localFile);
            in.skip(jumpSize);
            InputStreamEntity inEntity = new InputStreamEntity(in, localFile.length(), null);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(5000).build();
            HttpPost httpPost = new HttpPost(remoteFileUrl);
            httpPost.addHeader("otherInfo", URLEncoder.encode(otherInfo, "UTF-8"));
            httpPost.setEntity(inEntity);
            httpPost.setConfig(requestConfig);
            httpResponse = httpClient.execute(httpPost);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 执行文件下载
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param remoteFileUrl   远程下载文件地址
     * @param localFilePath   本地存储文件地址
     * @param charset         请求编码，默认UTF-8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static boolean executeDownloadFile(CloseableHttpClient httpClient, String remoteFileUrl, String localFilePath, String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            HttpGet httpget = new HttpGet(remoteFileUrl);
            response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return false;
            }
            in = entity.getContent();
            File file = new File(localFilePath);
            fout = new FileOutputStream(file);
            int l = -1;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
                // 注意这里如果用OutputStream.write(buff)的话，图片会失真
            }
            // 将文件输出到本地
            fout.flush();
            EntityUtils.consume(entity);
            return true;
        } finally {
            // 关闭低层流。
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 获取请求的
     *
     * @param paramsObj
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpEntity getEntity(Object paramsObj, String charset) throws UnsupportedEncodingException {
        if (paramsObj == null) {
            logger.info("当前未传入参数信息，无法生成HttpEntity");
            return null;
        }
        if (Map.class.isInstance(paramsObj)) {// 当前是map数据
            @SuppressWarnings("unchecked")
            Map<String, String> paramsMap = (Map<String, String>) paramsObj;
            List<NameValuePair> list = getNameValuePairs(paramsMap);
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
            httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            return httpEntity;
        } else if (String.class.isInstance(paramsObj)) {// 当前是string对象，可能是
            String paramsStr = paramsObj.toString();
            StringEntity httpEntity = new StringEntity(paramsStr, charset);
            logger.info("数据:" + paramsStr);
            if (paramsStr.startsWith("{")) {
                httpEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            } else if (paramsStr.startsWith("<")) {
                httpEntity.setContentType(ContentType.APPLICATION_XML.getMimeType());
            } else {
                httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            }
            return httpEntity;
        } else {
            logger.info("当前传入参数不能识别类型，无法生成HttpEntity");
        }
        return null;
    }

    /**
     * 从结果中获取出String数据
     *
     * @param httpResponse
     * @param charset
     * @return
     * @throws ParseException
     * @throws IOException
     */
    private static String getResult(CloseableHttpResponse httpResponse, String charset) throws ParseException, IOException {
        String result = null;
        if (httpResponse == null) {
            return result;
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return result;
        }
        logger.info("StatusCode is " + httpResponse.getStatusLine().getStatusCode());
        result = EntityUtils.toString(entity, charset);
        EntityUtils.consume(entity);// 关闭应该关闭的资源，适当的释放资源 ;也可以把底层的流给关闭了
        return result;
    }

    /**
     * 转化请求编码
     *
     * @param charset
     * @return
     */
    private static String getCharset(String charset) {
        return charset == null ? DEFAULT_CHARSET : charset;
    }

    /**
     * 将map类型参数转化为NameValuePair集合方式
     *
     * @param paramsMap
     * @return
     */
    private static List<NameValuePair> getNameValuePairs(Map<String, String> paramsMap) {
        List<NameValuePair> list = new ArrayList<>();
        if (paramsMap == null || paramsMap.isEmpty()) {
            return list;
        }
        for (Entry<String, String> entry : paramsMap.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    /**
     * 开启SSL支持
     */
    private static void enableSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{manager}, null);
            socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static SSLConnectionSocketFactory socketFactory;

    // https网站一般情况下使用了安全系数较低的SHA-1签名，因此首先我们在调用SSL之前需要重写验证方法，取消检测SSL。
    private static TrustManager manager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            //

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            //

        }
    };

    /**
     * 为httpclient设置重试信息
     *
     * @param httpClientBuilder
     * @param retryTimes
     */
    private static void setRetryHandler(HttpClientBuilder httpClientBuilder, final int retryTimes) {
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= retryTimes) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求被认为是幂等的，那么就重试
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        httpClientBuilder.setRetryHandler(myRetryHandler);
    }

    public static void main(String[] args) throws Exception {
        String filePath = "http://172.16.0.95:9999/push/100/upload/map/maps_2017-07-08_11-35-20.zip";
        String savePath = "E:\\unzip" + File.separator + "maps_2017-07-08_11-35-20.zip";
        boolean downLoadResult = executeDownloadFile(null, filePath, savePath, "utf-8", false);
        if (downLoadResult){
            ZipUtils.unzip(savePath,"E:\\unzip",false);
        }
    }
}