package cc.taketo.demo;

import cc.taketo.until.BCOSUtil;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;

/**
 * @Title: Main
 * @Package: cc.taketo.demo
 * @Description:
 * @Author: zhangp
 * @Date: 2022/11/14 - 14:20
 */

public class Main {


    public static void main(String[] args) {
        BcosSDK bcosSDK = BCOSUtil.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        BlockNumber blockNumber = client.getBlockNumber();
        System.out.println(blockNumber.getBlockNumber());
        bcosSDK.stopAll();
    }
}
