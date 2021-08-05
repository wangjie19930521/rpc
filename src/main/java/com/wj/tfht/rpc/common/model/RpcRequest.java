package com.wj.tfht.rpc.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RpcRequest
 * @Description: 远程请求体
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/

@Builder
@Setter
@Getter
public class RpcRequest implements Serializable {

    private String requestId;
    /**
     * 请求的服务名
     */
    private String serviceName;
    /**
     * 请求调用的方法
     */
    private String method;

    private Map<String,String> headers = new HashMap<>();

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
