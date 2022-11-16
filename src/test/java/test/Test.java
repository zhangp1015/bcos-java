package test;

import cc.taketo.async.TransactionCallbackMock;
import cc.taketo.contracts.HelloWorld;
import cc.taketo.until.BCOSUtil;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Title: Test
 * @Package: test
 * @Description:
 * @Author: zhangp
 * @Date: 2022/11/14 - 14:36
 */

public class Test {

    private Client client = null;

    private CryptoKeyPair keyPair = null;

    private AssembleTransactionProcessor transactionProcessor = null;

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

    @Before
    public void init() throws Exception {
        // 初始化BcosSDK对象
        BcosSDK bcosSDK = BCOSUtil.getBcosSDK();
        // 获取Client对象，此处传入的群组ID为1
        client = bcosSDK.getClient(1);
        // 构造AssembleTransactionProcessor对象，需要传入client对象，CryptoKeyPair对象和abi、binary文件存放的路径。
        // abi和binary文件需要在上一步复制到定义的文件夹中。
        keyPair = client.getCryptoSuite().createKeyPair();
        //基于abi和binary文件来直接部署和调用合约,通过创建和使用AssembleTransactionProcessor对象来完成合约相关的部署、调用和查询等操作。
        transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "src/main/resources/bin/");
        //只交易和查询，而不部署合约，那么就不需要复制binary文件，且在构造时无需传入binary文件的路径，例如构造方法的最后一个参数可传入空字符串。
//        transactionProcessor =
//                TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "");
    }


    /**
     * 同步方式部署合约
     *
     * @throws ABICodecException
     * @throws TransactionBaseException
     */
    @org.junit.Test
    public void syncDeployByContract() throws ABICodecException, TransactionBaseException {
        //部署合约调用了deployByContractLoader方法，传入合约名和构造函数的参数，上链部署合约，并获得TransactionResponse的结果。
        // 部署HelloWorld合约。第一个参数为合约名称，第二个参数为合约构造函数的列表，是List<Object>类型。
        TransactionResponse response = transactionProcessor.deployByContractLoader("HelloWorld", new ArrayList<>());
        /*
        TransactionResponse
        returnCode: 返回的响应码。其中0为成功。
        returnMessages: 返回的错误信息。
        TransactionReceipt：上链返回的交易回执。
        ContractAddress: 部署或调用的合约地址。
        values: 如果调用的函数存在返回值，则返回解析后的交易返回值，返回Json格式的字符串。
        events: 如果有触发日志记录，则返回解析后的日志返回值，返回Json格式的字符串。
        receiptMessages: 返回解析后的交易回执信息。
        */
        System.out.println(response);
    }

    /**
     * 同步方式发送交易
     */
    @org.junit.Test
    public void syncSet() throws ABICodecException, TransactionBaseException {
        // 创建调用交易函数的参数，此处为传入一个参数
        List<Object> params = new ArrayList<>();
        params.add("Hello");
        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader("HelloWorld", "0xe34e5803014ad38bbee1a2a071eb4df39171b994", "set", params);
        System.out.println("transactionResponse = " + transactionResponse);
    }

    /**
     * 调用合约查询接口
     */
    @org.junit.Test
    public void syncGet() throws ABICodecException, TransactionBaseException {
        // 查询HelloWorld合约的『name』函数，合约地址为helloWorldAddress，参数为空
        CallResponse callResponse = transactionProcessor.sendCallByContractLoader("HelloWorld", "0xe34e5803014ad38bbee1a2a071eb4df39171b994", "get", new ArrayList<>());
        String values = callResponse.getValues();
        System.out.println("values = " + values);
    }

    /**
     * 拼接签名的方式发送交易
     * 对于特殊的场景，可以通过接口签名的方式DIY拼装交易和发送交易。
     */
    @org.junit.Test
    public void signSend() throws ABICodecException {
        ABICodec abiCodec = new ABICodec(client.getCryptoSuite());
        String setMethodSignature = "set(string)";
        String abiEncoded = abiCodec.encodeMethodByInterface(setMethodSignature, Arrays.asList(new Object[]{new String("Hello World")}));
        //构造TransactionProcessor
        //由于通过构造接口签名的方式无需提供abi，故可以构造一个TransactionProcessor来操作。同样可使用TransactionProcessorFactory来构造。
        TransactionProcessor transactionProcessor = TransactionProcessorFactory.createTransactionProcessor(client, keyPair);
        //发送交易到FISCO BCOS节点并接收回执。
        TransactionReceipt transactionReceipt = transactionProcessor.sendTransactionAndGetReceipt("0xe34e5803014ad38bbee1a2a071eb4df39171b994", abiEncoded, keyPair);
        System.out.println(transactionReceipt);
    }

    /**
     * 采用callback的方式异步部署合约
     */
    @org.junit.Test
    public void asyncDeploy() throws ABICodecException, NoSuchTransactionFileException {
        //创建一个回调类的实例。然后使用deployByContractLoaderAsync方法，异步部署合约。
        // 创建回调类的实例
        TransactionCallbackMock callbackMock = new TransactionCallbackMock();
        // 异步部署合约
        transactionProcessor.deployByContractLoaderAsync("HelloWorld", new ArrayList<>(), callbackMock);
        // 异步等待获取回执
        TransactionReceipt transactionReceipt = callbackMock.getResult();
        System.out.println("transactionReceipt = " + transactionReceipt);
    }

    /**
     * 采用callback的方式发送交易
     */
    @org.junit.Test
    public void asyncSend() throws ABICodecException, TransactionBaseException {
        // 创建回调类的实例
        TransactionCallbackMock callbackMock = new TransactionCallbackMock();
        // 定义构造参数
        List<Object> params = new ArrayList<>();
        params.add("test");
        // 异步调用合约交易
        //todo
//        transactionProcessor.sendTransactionAsync("0xe34e5803014ad38bbee1a2a071eb4df39171b994", abi, "set", params, callbackMock);
        // 异步等待获取回执
//        TransactionReceipt transactionReceipt = callbackMock.getResult();
//        System.out.println("transactionReceipt = " + transactionReceipt);
    }

}
