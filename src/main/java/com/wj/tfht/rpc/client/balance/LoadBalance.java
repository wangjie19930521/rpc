package com.wj.tfht.rpc.client.balance;

import com.wj.tfht.rpc.common.Service;

import java.util.List;

/**
 * @ClassName LoadBalance
 * @Description: 负载均衡 接口
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public interface LoadBalance {

    Service select(List<Service> services);
}
