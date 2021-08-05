package com.wj.tfht.rpc.common.protocol;

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
    byte[] marshallingRequest(T request) throws Exception;

    /**
     * 解组请求
     * @param data
     * @return
     * @throws Exception
     */
    T unmarshallingRequest(byte[] data) throws Exception;
}
