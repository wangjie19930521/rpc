package com.wj.tfht.rpc.common.model;

import com.wj.tfht.rpc.common.constant.RpcStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RpcResponse
 * @Description:  远程调用返回实体
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Setter
@Getter
public class RpcResponse implements Serializable {

    private String requestId;

    private Map<String, String> headers = new HashMap<>();

    private Object returnValue;

    private Exception exception;

    private RpcStatusEnum rpcStatus;
}
