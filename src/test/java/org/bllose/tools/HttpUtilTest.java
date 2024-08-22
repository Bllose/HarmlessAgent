package org.bllose.tools;

public class HttpUtilTest {
    HttpUtil httpUtil = new HttpUtil();

    public static void main(String[] args) {
        String rsp = HttpUtil.discovery("test1");

        System.out.println(rsp);
    }
}
