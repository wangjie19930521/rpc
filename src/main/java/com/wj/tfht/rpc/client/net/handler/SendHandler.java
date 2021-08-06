package com.wj.tfht.rpc.client.net.handler;

import com.wj.tfht.rpc.client.net.NettyNetClient;
import com.wj.tfht.rpc.client.net.RpcFuture;
import com.wj.tfht.rpc.common.model.RpcRequest;
import com.wj.tfht.rpc.common.model.RpcResponse;
import com.wj.tfht.rpc.common.protocol.MessageProtocol;
import com.wj.tfht.rpc.exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName SendHandler
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Slf4j
public class SendHandler extends ChannelInboundHandlerAdapter {

    /**
     * 等待通道建立最大时间
     */
    static final int CHANNEL_WAIT_TIME = 4;
    /**
     * 等待响应最大时间
     */
    static final int RESPONSE_WAIT_TIME = 8;

    private Channel channel;

    private String remoteAddress;

    /**
     * 请求缓存，
     */
    private static Map<String, RpcFuture<RpcResponse>> requestMap = new ConcurrentHashMap<>();

    private MessageProtocol messageProtocol;

    public SendHandler(MessageProtocol messageProtocol, String remoteAddress) {
        this.messageProtocol = messageProtocol;
        this.remoteAddress = remoteAddress;
    }

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        log.info("channel create...");
        countDownLatch.countDown();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active ....");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channel inactive ....");
        //连接失效，清楚缓存
        NettyNetClient.connectedServerNodes.remove(remoteAddress);
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
        ByteBuf byteBuf = ByteBuf.class.cast(msg);
        byte[] resp = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(resp);
        // 手动回收
        ReferenceCountUtil.release(byteBuf);
        //解码
        RpcResponse rpcResponse = messageProtocol.unmarshallingResponse(resp);

        RpcFuture<RpcResponse> rpcFuture = requestMap.get(rpcResponse.getRequestId());

        rpcFuture.setResponse(rpcResponse);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("Exception occurred:{}", cause.getMessage());
        ctx.close();
    }

    public RpcResponse sendRpcReauest(RpcRequest rpcRequest) {

        RpcFuture<RpcResponse> future = new RpcFuture<>();
        RpcResponse response = null;
        requestMap.put(rpcRequest.getRequestId(), future);
        try {
            byte[] data = messageProtocol.marshallingRequest(rpcRequest);
            ByteBuf buffer = Unpooled.buffer(data.length);
            buffer.writeBytes(buffer);

            /**
             * 第一次发送请求，考虑通道是否建立，   countDown 之后的次数就都是true
             */
            if (countDownLatch.await(CHANNEL_WAIT_TIME, TimeUnit.SECONDS)) {
                channel.writeAndFlush(buffer);
                response = future.get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
            } else {
                throw new RpcException("channel create fail...");
            }

        } catch (Exception e) {
            log.warn("发送请求错误：{}", e.getMessage());
            throw new RpcException(e.getMessage());
        } finally {
            requestMap.remove(rpcRequest.getRequestId());
        }

        return response;

    }
}
