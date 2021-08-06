package com.wj.tfht.rpc.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName RpcConfig
 * @Description:配置文件
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "tfht.rpc")
public class RpcConfig {
    /**
     * 服务注册中心地址
     */
    private String registerAddress = "127.0.0.1:2181";

    /**
     * 服务暴露端口
     */
    private Integer serverPort = 9999;
    /**
     * 服务协议
     */
    private String protocol = "java";
    /**
     * 负载均衡算法
     */
    private String loadBalance = "random";
    /**
     * 权重，默认为1
     */
    private Integer weight = 1;

}
