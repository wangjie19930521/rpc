package com.wj.tfht.rpc.client.net.handler;

import com.wj.tfht.rpc.client.net.RpcFuture;
import com.wj.tfht.rpc.common.model.RpcResponse;
import com.wj.tfht.rpc.common.protocol.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SendHandler
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Slf4j
public class SendHandler extends ChannelInboundHandlerAdapter {

    private String remoteAddress;

    private static Map<String, RpcFuture<RpcResponse>> requestMap = new ConcurrentHashMap<>();

    private MessageProtocol messageProtocol;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active ....");
    }

    /**
     * 收到返回
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public void sendRpcReauest() {

    }
}
