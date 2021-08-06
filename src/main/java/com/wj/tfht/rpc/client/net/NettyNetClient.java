package com.wj.tfht.rpc.client.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wj.tfht.rpc.client.net.handler.SendHandler;
import com.wj.tfht.rpc.common.Service;
import com.wj.tfht.rpc.common.model.RpcRequest;
import com.wj.tfht.rpc.common.model.RpcResponse;
import com.wj.tfht.rpc.common.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

/**
 * @ClassName NettyNetClient
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/6
 * @Version V1.0
 **/
@Slf4j
public class NettyNetClient implements NetClient {

    private static ExecutorService threadPool = new ThreadPoolExecutor(4, 10, 200,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
            .setNameFormat("rpcClient-%d")
            .build());

    private EventLoopGroup loopGroup = new NioEventLoopGroup(4);

    /**
     * 已连接的服务缓存
     * key: 服务地址，格式：ip:port
     */
    public static Map<String, SendHandler> connectedServerNodes = new ConcurrentHashMap<>();


    /**
     * 发送请求时，将该地址的连接通道和处理器 缓存起来。下次请求直接用。 基于 长连接。
     *
     * @param rpcRequest
     * @param service
     * @param messageProtocol
     * @return
     */
    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol) {


        String address = service.getAddress();
        synchronized (address) {

            SendHandler handler = connectedServerNodes.get(address);
            if (nonNull(handler)) {
                log.info("使用缓存连接,地址{},请求信息：{}", address, rpcRequest);
                return handler.sendRpcReauest(rpcRequest);
            }

            log.info("新建连接,地址{},请求信息：{}", address, rpcRequest);

            String[] split = address.split(":");
            final String host = split[0];
            final String port = split[1];
            final SendHandler sendHandler = new SendHandler(messageProtocol, address);

            threadPool.submit(() -> {
                // 配置客户端
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(loopGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {


                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(sendHandler);
                            }
                        });
                //连接
                ChannelFuture channelFuture = bootstrap.connect(host, Integer.parseInt(port));

                channelFuture.addListener((future) -> {
                    connectedServerNodes.put(address, sendHandler);
                });


            });
            return sendHandler.sendRpcReauest(rpcRequest);
        }

    }
}
