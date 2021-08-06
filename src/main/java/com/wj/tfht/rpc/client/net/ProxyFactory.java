package com.wj.tfht.rpc.client.net;


import com.wj.tfht.rpc.client.balance.LoadBalance;
import com.wj.tfht.rpc.client.discovery.ServerDiscovery;
import com.wj.tfht.rpc.common.Service;
import com.wj.tfht.rpc.common.model.RpcRequest;
import com.wj.tfht.rpc.common.model.RpcResponse;
import com.wj.tfht.rpc.common.protocol.MessageProtocol;
import com.wj.tfht.rpc.exception.RpcException;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

/**
 * @ClassName ProxyFactory
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public class ProxyFactory {


    private ServerDiscovery serverDiscovery;

    @Setter
    private LoadBalance loadBalance;

    @Setter
    private NetClient netClient;

    //缓存代理对象
    private Map<Class<?>, Object> objectCache = new HashMap<>();

    //支持的协议
    @Setter
    private Map<String, MessageProtocol> supportMessageProtocols;


    public ProxyFactory(ServerDiscovery serverDiscovery) {
        this.serverDiscovery = serverDiscovery;
    }

    /**
     * 获取代理对象,并缓存
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> clazz) {

        return (T) objectCache.computeIfAbsent(clazz, klass -> Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ProxyInvocationHandler(clazz)));
    }

    /**
     * 1.构造器需要klass ，根据名称取拉取服务
     * 2.需要服务发现对象
     */
    private class ProxyInvocationHandler implements InvocationHandler {

        private Class<?> klass;

        public ProxyInvocationHandler(Class<?> klass) {
            this.klass = klass;
        }

        /**
         * 根据逻辑需要什么就通过构造器传入
         *
         * @param proxy
         * @param method
         * @param args
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //1.拉去服务列表, 需要属性 serverDiscovery
            List<Service> services = serverDiscovery.discoveryServer(klass.getName());
            //2.选择一个调用 需要属性 loadBalance
            Service service = loadBalance.select(services);
            //3.远程调用
            //3.1 构建请求
            RpcRequest request = RpcRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .serviceName(service.getName())
                    .method(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .parameters(method.getParameters())
                    .build();


            MessageProtocol messageProtocol = supportMessageProtocols.get(service.getProtocol());

            //发送请求
            RpcResponse response = netClient.sendRequest(request, service, messageProtocol);

            Optional.ofNullable(response)
                    .orElseThrow(() -> new RpcException("response is null"));

            if (nonNull(response.getException())) {
                return response.getException();
            }

            return response.getReturnValue();
        }
    }

}
