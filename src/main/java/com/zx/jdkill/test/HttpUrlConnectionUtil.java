package com.zx.jdkill.test;

import com.alibaba.fastjson.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * @author: zhaoxu
 * @date: 2021/1/5 22:26
 */
public class HttpUrlConnectionUtil {
    /**
     * get请求
     *
     * @param headers 请求头，可为空
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(JSONObject headers, String url) throws IOException {
        String response = "";
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
        httpURLConnection.setRequestMethod("GET");
        if (headers != null) {
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String headerName = iterator.next();
                httpURLConnection.setRequestProperty(headerName, headers.get(headerName).toString());
            }
        }
        httpURLConnection.connect();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] buffer;
            buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                response = response + new String(buffer, 0, length, "UTF-8");
            }
            httpURLConnection.disconnect();
        }
        return response;
    }

    /**
     * post请求
     *
     * @param headers 请求头，可为空
     * @param url
     * @param params  post请求体，可为空
     * @return
     * @throws IOException
     */
    public static String post(JSONObject headers, String url, JSONObject params) throws IOException {
        String response = "";
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
        httpURLConnection.setRequestMethod("POST");
        if (headers != null) {
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String headerName = iterator.next();
                httpURLConnection.setRequestProperty(headerName, headers.get(headerName).toString());
            }
        }
        httpURLConnection.setDoOutput(true);
        httpURLConnection.connect();
        if (params != null) {
            httpURLConnection.getOutputStream().write(params.toJSONString().getBytes("UTF-8"));
        }
        httpURLConnection.getInputStream();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] buffer;
            buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                response = response + new String(buffer, 0, length, "UTF-8");
            }
            httpURLConnection.disconnect();
        }
        httpURLConnection.disconnect();
        return response;
    }

    /**
     * 获取并保存二维码
     *
     * @param headers
     * @param url
     * @return
     * @throws IOException
     */
    public static String getQCode(JSONObject headers, String url) throws IOException {
        String response = "";
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
        httpURLConnection.setRequestMethod("GET");
        if (headers != null) {
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String headerName = iterator.next();
                httpURLConnection.setRequestProperty(headerName, headers.get(headerName).toString());
            }
        }
        httpURLConnection.connect();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream("QCode.png");
            byte[] buffer;
            int length;
            buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                response = response + new String(buffer, 0, length, "UTF-8");
            }
            outputStream.close();
            httpURLConnection.disconnect();
        }
        return response;
    }
}
