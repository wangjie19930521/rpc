package com.wj.tfht.rpc.configuration;

import com.wj.tfht.rpc.annotation.LoadBalanceAno;
import com.wj.tfht.rpc.annotation.MessageProtocolAno;
import com.wj.tfht.rpc.client.balance.LoadBalance;
import com.wj.tfht.rpc.client.discovery.ZookeeperServerDiscovery;
import com.wj.tfht.rpc.client.net.NettyNetClient;
import com.wj.tfht.rpc.client.net.ProxyFactory;
import com.wj.tfht.rpc.common.protocol.MessageProtocol;
import com.wj.tfht.rpc.exception.RpcException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

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
    public RpcConfig rpcConfig() {
        return new RpcConfig();
    }

    @Bean
    public ProxyFactory proxyFactory(RpcConfig rpcConfig) {
        ProxyFactory proxyFactory = new ProxyFactory(new ZookeeperServerDiscovery(rpcConfig.getRegisterAddress()));

        proxyFactory.setNetClient(new NettyNetClient());

        // 设置负载均衡算法  spi
        LoadBalance loadBalance = getLoadBalance(rpcConfig.getLoadBalance());
        proxyFactory.setLoadBalance(loadBalance);

        // 设置支持的协议
        Map<String, MessageProtocol> supportMessageProtocols = buildSupportMessageProtocols();
        proxyFactory.setSupportMessageProtocols(supportMessageProtocols);

        return proxyFactory;
    }

    private Map<String, MessageProtocol> buildSupportMessageProtocols() {
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<>();
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while (iterator.hasNext()) {
            MessageProtocol messageProtocol = iterator.next();
            MessageProtocolAno ano = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(ano, "message protocol name can not be empty!");
            supportMessageProtocols.put(ano.value(), messageProtocol);
        }
        return supportMessageProtocols;

    }

    private LoadBalance getLoadBalance(String name) {
        ServiceLoader<LoadBalance> loader = ServiceLoader.load(LoadBalance.class);
        Iterator<LoadBalance> iterator = loader.iterator();
        while (iterator.hasNext()) {
            LoadBalance loadBalance = iterator.next();
            LoadBalanceAno ano = loadBalance.getClass().getAnnotation(LoadBalanceAno.class);
            Assert.notNull(ano, "load balance name can not be empty!");
            if (name.equals(ano.value())) {
                return loadBalance;
            }
        }
        throw new RpcException("invalid load balance config");
    }


}
