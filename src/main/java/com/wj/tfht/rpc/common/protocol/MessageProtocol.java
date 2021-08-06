package com.wj.tfht.rpc.common.protocol;

import com.wj.tfht.rpc.common.model.RpcRequest;
import com.wj.tfht.rpc.common.model.RpcResponse;

/**
 * @ClassName MessageProtocol
 * @Description: 序列化协议接口
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public interface MessageProtocol<T> {

    /**
     * 编组请求
     * @param request 请求信息
     * @return 请求字节数组
     * @throws Exception
     */
    byte[] marshallingRequest(RpcRequest request) throws Exception;

    /**
     * 解组请求
     * @param data
     * @return
     * @throws Exception
     */
    RpcRequest unmarshallingRequest(byte[] data) throws Exception;

    /**
     * 编组响应
     * @param response
     * @return
     */
    byte[] marshallingResponse(RpcResponse response) throws Exception;

    /**
     * 解组响应
     * @param data
     * @return
     * @throws Exception
     */
    RpcResponse unmarshallingResponse(byte[] data) throws Exception;
}
