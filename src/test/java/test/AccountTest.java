package test;

import cc.taketo.until.BCOSUtil;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.junit.Test;

/**
 * @Title: AccountTest
 * @Package: test
 * @Description:
 * @Author: zhangp
 * @Date: 2022/11/16 - 11:21
 */

public class AccountTest {

    @Test
    public void createAccount(){
        // 创建非国密类型的CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 创建国密类型的CryptoSuite
        // CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);

        // 随机生成非国密公私钥对
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 获取账户地址
        String accountAddress = cryptoKeyPair.getAddress();
        System.out.println("accountAddress = " + accountAddress);
    }

    @Test
    public void P12Manager(){
        // 初始化BcosSDK
        BcosSDK bcosSDK = BCOSUtil.getBcosSDK();
        // 为群组1初始化client
        Client client = bcosSDK.getClient(Integer.valueOf(1));
        // 通过client获取CryptoSuite对象
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        // 加载pem账户文件
        cryptoSuite.loadAccount("p12", "src/main/resources/accounts/P12Manager/0xc671d7a2ef1f4165aa4814b0c4053dd2646c56d0.p12", "123456");
    }

    @Test
    public void PEMManager(){
        // 初始化BcosSDK
        BcosSDK bcosSDK = BCOSUtil.getBcosSDK();
        // 为群组1初始化client
        Client client = bcosSDK.getClient(Integer.valueOf(1));
        // 通过client获取CryptoSuite对象
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        // 加载pem账户文件
        cryptoSuite.loadAccount("pem", "src/main/resources/accounts/PEMManager/0xa98248d21cbb3fabdc462af8932a6fbad54364cc.pem", null);

    }
}
