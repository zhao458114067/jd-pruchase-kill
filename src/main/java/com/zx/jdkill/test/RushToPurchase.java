package com.zx.jdkill.test;

import com.alibaba.fastjson.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author: zhaoxu
 * @date: 2021/1/8 20:51
 */
public class RushToPurchase implements Job {
    /**
     * 请求头
     */
    static Map<String, List<String>> stringListMap = new HashMap<>(16);

    public static void setIpProxy() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.getProperties().setProperty("http.proxyHost", hostAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String response = HttpUrlConnectionUtil.get(null, Start.getIpUrl);
            System.out.println("设置代理：" + response);
            String[] split = response.split(":");
            System.getProperties().setProperty("http.proxyHost", split[0]);
            System.getProperties().setProperty("http.proxyPort", split[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JSONObject headers = new JSONObject();
        headers.put(Start.HEADER_AGENT, Start.HEADER_AGENT_ARG);
        headers.put(Start.REFERER, Start.REFERER_ARG);

        //抢购
        String gate = null;
        List<String> cookie = new ArrayList<>();
        try {
            gate = HttpUrlConnectionUtil.get(headers, "https://cart.jd.com/gate.action?pcount=1&ptype=1&pid=" + Start.pid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //订单信息
        stringListMap.clear();
        try {
            stringListMap = Start.manager.get(new URI("https://trade.jd.com/shopping/order/getOrderInfo.action"), stringListMap);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cookie = stringListMap.get("Cookie");
        headers.put("Cookie", cookie.get(0).toString());
        try {
            String orderInfo = HttpUrlConnectionUtil.get(headers, "https://trade.jd.com/shopping/order/getOrderInfo.action");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //提交订单
        JSONObject subData = new JSONObject();
        headers = new JSONObject();
        subData.put("overseaPurchaseCookies", "");
        subData.put("vendorRemarks", "[]");
        subData.put("submitOrderParam.sopNotPutInvoice", "false");
        subData.put("submitOrderParam.ignorePriceChange", "1");
        subData.put("submitOrderParam.btSupport", "0");
        subData.put("submitOrderParam.isBestCoupon", "1");
        subData.put("submitOrderParam.jxj", "1");
        subData.put("submitOrderParam.trackID", Login.ticket);
        subData.put("submitOrderParam.eid", Start.eid);
        subData.put("submitOrderParam.fp", Start.fp);
        subData.put("submitOrderParam.needCheck", "1");

        headers.put("Referer", "http://trade.jd.com/shopping/order/getOrderInfo.action");
        headers.put("origin", "https://trade.jd.com");
        headers.put("Content-Type", "application/json");
        headers.put("x-requested-with", "XMLHttpRequest");
        headers.put("upgrade-insecure-requests", "1");
        headers.put("sec-fetch-user", "?1");
        stringListMap.clear();

        try {
            stringListMap = Start.manager.get(new URI("https://trade.jd.com/shopping/order/getOrderInfo.action"), stringListMap);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cookie = stringListMap.get("Cookie");
        headers.put("Cookie", cookie.get(0).toString());
        String submitOrder = null;
        try {
            //获取ip，使用的是免费的 携趣代理 ，不需要或者不会用可以注释掉
            if (!StringUtils.isEmpty(Start.getIpUrl)) {
                setIpProxy();
            }
            submitOrder = HttpUrlConnectionUtil.post(headers, "https://trade.jd.com/shopping/order/submitOrder.action", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (submitOrder.contains("刷新太频繁了") || submitOrder.contains("抱歉，您访问的内容不存在")) {
            System.out.println("刷新太频繁了,您访问的内容不存在");
        }
        JSONObject jsonObject = JSONObject.parseObject(submitOrder);
        String success = null;
        String message = null;
        if (jsonObject != null && jsonObject.get("success") != null) {
            success = jsonObject.get("success").toString();
        }
        if (jsonObject != null && jsonObject.get("message") != null) {
            message = jsonObject.get("message").toString();
        }
        if ("true".equals(success)) {
            System.out.println("已成功抢购，请尽快完成付款");
        } else {
            if (message != null) {
                System.out.println(message);
            }
        }
    }
}
