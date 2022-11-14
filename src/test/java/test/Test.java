package test;

import cc.taketo.contracts.HelloWorld;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;

/**
 * @Title: Test
 * @Package: test
 * @Description:
 * @Author: zhangp
 * @Date: 2022/11/14 - 14:36
 */

public class Test {


    // 获取配置文件路径
    public final String configFile = Test.class.getClassLoader().getResource("config-example.toml").getPath();

    @org.junit.Test
    public void BcosSDKTest() throws Exception {
        // 初始化BcosSDK
        BcosSDK sdk = BcosSDK.build(configFile);
        // 为群组1初始化client
        Client client = sdk.getClient(1);

        // 获取群组1的块高
        BlockNumber blockNumber = client.getBlockNumber();

//        // 向群组1部署HelloWorld合约
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);

//
//        // 调用HelloWorld合约的get接口
        String getValue = helloWorld.get();
        System.out.println(getValue);
//
//        // 调用HelloWorld合约的set接口
        TransactionReceipt receipt = helloWorld.set("Hello, fisco");
        System.out.println(receipt);
    }


}
