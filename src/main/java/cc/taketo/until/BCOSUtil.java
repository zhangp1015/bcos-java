package cc.taketo.until;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.stereotype.Component;

/**
 * @Title: ConfigUtil
 * @Package: cc.taketo.until
 * @Description:
 * @Author: zhangp
 * @Date: 2022/11/14 - 15:02
 */

@Component
public class BCOSUtil {

    private final static String configFile = BCOSUtil.class.getClassLoader().getResource("config-example.toml").getPath();

    public static BcosSDK getBcosSDK(){
        return BcosSDK.build(configFile);
    }

}
