package demo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientStackOverflowErrorShutdownDemo {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(HttpClientStackOverflowErrorShutdownDemo.class);

    public static void main(String[] args) throws Exception {
        HttpClientBuilder httpBuilder = HttpClients.custom();

        RegistryBuilder<ConnectionSocketFactory> registerBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE);

        httpBuilder.setConnectionManagerShared(true);
        final int connTimeToLive = 60000000;
        Registry<ConnectionSocketFactory> socketFactoryRegistry = registerBuilder.build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,
                null, null, null, connTimeToLive, TimeUnit.SECONDS);

        connManager.setMaxTotal(1000);
        connManager.setDefaultMaxPerRoute(400);
        httpBuilder.setConnectionManager(connManager);
        CloseableHttpClient httpClient = httpBuilder.build();
        String url = "http://www.baidu.com/";
        try {
            try {
                // demo a Error on MainClientExec.execute
                doLoopGet(url, httpClient, null, 0);
            } catch (Error ex) {
                logger.info("==============================>invokeCount1: {}", invokeCount);
                logger.error("{}", ex.toString());
                doGet(url, httpClient);
            }

        } catch (Exception ex) {
            logger.info("==============================>invokeCount2: {}", invokeCount);
            throw ex;
        }
        httpClient.close();
    }

    private static int invokeCount;

    private static String doLoopGet(String url, CloseableHttpClient httpClient, final String tmp, int count)
            throws IOException, HttpException {
        String result = doGet(url, httpClient);
        if (count % 200 == 0) {
            logger.info("count: {} ==> {}", count, (result == null ? -1 : result.length()));
        }
        invokeCount = count;
        // ERROR MainClientExec HttpClientConnectionManager shutdown
        return doLoopGet(url, httpClient, result, ++count);
    }

    private static String doGet(String url, CloseableHttpClient httpClient) throws IOException, HttpException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-Type", "application/html;charset=utf8");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "utf-8");
                return result;
            } else {
                return null;
            }
        } finally {
            response.close();
        }
    }

}
