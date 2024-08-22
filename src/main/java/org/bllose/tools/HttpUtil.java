package org.bllose.tools;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class HttpUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static final String HTTP_AUTH = "Basic YWRtaW46YWRtaW4xMjM=";

    public static final String host = "https://aurora-admin.tclpv.cn";

    public static CloseableHttpClient getClient() {
        SSLContextBuilder builder = new SSLContextBuilder();

        SSLContext sslContext = null;
        try {
            sslContext = builder.loadTrustMaterial(null, (chain, authType) -> true).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }

    public static String login(String env, CloseableHttpClient httpClient) {
        String url = host + "/" + env + "/login";

        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Accept", "text/html");
        httpPost.addHeader("Authorization", HTTP_AUTH);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("username", "admin"));
        nameValuePairs.add(new BasicNameValuePair("password", "admin123"));

        UrlEncodedFormEntity body;
        try {
            body = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try (CloseableHttpResponse response = httpClient.execute(httpPost);) {
            Header header = response.getFirstHeader("Set-Cookie");
            if (header == null) {
                return "";
            }

            return header.getValue();
        } catch (Exception e) {
            log.error("fetch {}, error: {}", url, e.getMessage());
        }
        return "";
    }

    public static String discovery(String env) {
        CloseableHttpClient httpClient = getClient();

        String cookie = login(env, httpClient);
        if (cookie == null || cookie.isEmpty()) {
            log.warn("获取服务注册中心实例失败, 登录失败");
            return "";
        }

        String url = host + "/" + env + "/applications";

        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("Cookie", cookie);
        httpGet.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(httpGet);) {
            if (response.getStatusLine().getStatusCode() != 200) {
                return "";
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
            log.error("fetch {}, error: {}", url, e.getMessage());
        }

        return "";
    }

}
