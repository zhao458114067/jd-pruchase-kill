package com.zx.jdkill.test;

import com.alibaba.fastjson.JSONObject;
import com.sun.webkit.network.CookieManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhaoxu
 * @date: 2021/1/8 20:59
 */
public class Start {
    final static String headerAgent = "User-Agent";
    final static String headerAgentArg = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36";
    final static String Referer = "Referer";
    final static String RefererArg = "https://passport.jd.com/new/login.aspx";
    //商品id
    static String pid = "";
    //eid
    static String eid = "";
    //fp
    static String fp = "";
    //抢购数量
    static Integer ok = 2;
    //获取ip代理
    static String getIpUrl = "";

    static CookieManager manager = new CookieManager();

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ParseException {
        initData();
        CookieHandler.setDefault(manager);
        //获取venderId
//        String shopDetail = util.get(null, "https://item.jd.com/" + RushToPurchase.pid + ".html");
//        String venderID = shopDetail.split("isClosePCShow: false,\n" +
//                "                venderId:")[1].split(",")[0];
//        RushToPurchase.venderId = venderID;
        //登录
        Login.Login();
        //判断是否开始抢购
        judgePruchase();
        //开始抢购
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(15, 20, 1000, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < 15; i++) {
            threadPoolExecutor.execute(new RushToPurchase());
        }
        new RushToPurchase().run();
    }

    public static void initData() throws IOException {
        String fileData = readFile("initData.txt").toString();
        try {
            pid = fileData.split("pid=")[1].split(";")[0];
            eid = fileData.split("eid=")[1].split(";")[0];
            fp = fileData.split("fp=")[1].split(";")[0];
            ok = Integer.valueOf(fileData.split("ok=")[1].split(";")[0]);
            getIpUrl = fileData.split("getIpUrl=")[1].split(";")[0];
            HttpUrlConnectionUtil.ips(getIpUrl);
        } catch (Exception e) {
            System.out.println("参数错误，每个参数后面需要加分号");
        }

    }

    public static void judgePruchase() throws IOException, ParseException, InterruptedException {
        //获取开始时间
        JSONObject headers = new JSONObject();
        headers.put(Start.headerAgent, Start.headerAgentArg);
        headers.put(Start.Referer, Start.RefererArg);
        JSONObject shopDetail = JSONObject.parseObject(HttpUrlConnectionUtil.get(headers, "https://item-soa.jd.com/getWareBusiness?skuId=" + pid));
        if (shopDetail.get("yuyueInfo") != null) {
            String buyDate = JSONObject.parseObject(shopDetail.get("yuyueInfo").toString()).get("buyTime").toString();
            String startDate = buyDate.split("-202")[0] + ":00";
            Long startTime = HttpUrlConnectionUtil.dateToTime(startDate);
            //开始抢购
            while (true) {
                //获取京东时间
                JSONObject jdTime = JSONObject.parseObject(HttpUrlConnectionUtil.get(headers, "https://a.jd.com//ajax/queryServerData.html"));
                Long serverTime = Long.valueOf(jdTime.get("serverTime").toString());
                if (startTime >= serverTime + 100) {
                    System.out.println("正在等待抢购时间");
                    Thread.sleep(100);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 按行读取全部文件数据
     *
     * @param strFile
     */
    public static StringBuffer readFile(String strFile) throws IOException {
        StringBuffer strSb = new StringBuffer();
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
        // character streams
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while (line != null) {
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }
        return strSb;
    }
}
