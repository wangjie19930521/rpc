package com.wj.tfht.rpc.client.discovery;

import com.wj.tfht.rpc.common.Service;

import java.util.List;

/**
 * @ClassName ServerDiscovery
 * @Description: 服务发现抽象类
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public interface ServerDiscovery {
    /**
     * 根据全类名查询服务列表
     * @param name
     * @return
     */
    List<Service> discoveryServer(String name);
}
