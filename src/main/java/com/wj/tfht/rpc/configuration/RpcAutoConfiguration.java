package com.wj.tfht.rpc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName Configuration
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Configuration
public class RpcAutoConfiguration {

    @Bean
    public RpcConfig rpcConfig(){
        return new RpcConfig();
    }


}
