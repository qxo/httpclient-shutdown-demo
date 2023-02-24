apache httpclient Connection pool shut down demo
===============================================================================
Why shutdown Connection pool if MainClientExec.execute has a Error ?
This is very unreasonable, one application shares the same httpclient,
and a problem with one call will make the entire application unusable.
Not robust enough!!!

```
信息: Retrying request to {}->http://www.baidu.com:80
21:54:20.099 [main] INFO  d.HttpClientStackOverflowErrorShutdownDemo - count: 1200 ==> 2287
21:54:21.921 [main] INFO  d.HttpClientStackOverflowErrorShutdownDemo - ==============================>invokeCount1: 1330
21:54:21.921 [main] ERROR d.HttpClientStackOverflowErrorShutdownDemo - java.lang.StackOverflowError
21:54:21.921 [main] INFO  d.HttpClientStackOverflowErrorShutdownDemo - ==============================>invokeCount2: 1330
Exception in thread "main" java.lang.IllegalStateException: Connection pool shut down
    at org.apache.http.util.Asserts.check(Asserts.java:34)
    at org.apache.http.pool.AbstractConnPool.lease(AbstractConnPool.java:191)
    at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.requestConnection(PoolingHttpClientConnectionManager.java:267)
    at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:176)
    at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:185)
    at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:89)
    at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
    at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:185)
    at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:83)
    at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:108)
    at demo.HttpClientStackOverflowErrorShutdownDemo.doGet(HttpClientStackOverflowErrorShutdownDemo.java:80)
    at demo.HttpClientStackOverflowErrorShutdownDemo.doLoopGet(HttpClientStackOverflowErrorShutdownDemo.java:65)
    at demo.HttpClientStackOverflowErrorShutdownDemo.main(HttpClientStackOverflowErrorShutdownDemo.java:51)
```

ref: https://github.com/apache/httpcomponents-client/blob/0f1df5c04fdce791faf4a148feb2097f8941bafd/httpclient/src/main/java/org/apache/http/impl/execchain/MainClientExec.java#L368
