package com.wj.tfht.rpc.exception;

/**
 * @ClassName RpcException
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public class RpcException extends RuntimeException {
    public RpcException(String message){
        super(message);
    }
}
