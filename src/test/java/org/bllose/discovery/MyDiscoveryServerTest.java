package org.bllose.discovery;

public class MyDiscoveryServerTest {

    public static void main(String[] args) {
//        mds.fetchServers();
//        System.out.println(mds.findListOfServersByName("xk-order2"));

        System.out.println(ServerDiscover.fetchByServerName("xk-order2"));
    }
}
