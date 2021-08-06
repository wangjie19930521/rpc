package com.wj.tfht.rpc.client.discovery;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wj.tfht.rpc.common.Service;
import com.wj.tfht.rpc.common.constant.RpcConstant;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName ZookeeperServerDiscovery
 * @Description: zk服务发现
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public class ZookeeperServerDiscovery implements ServerDiscovery {

    private ZkClient zkClient;

    public ZookeeperServerDiscovery(String zkAddr) {
        this.zkClient = new ZkClient(zkAddr);
        zkClient.setZkSerializer(new ZkSerializer() {

            /**
             * 序列化
             * @param data
             * @return
             * @throws ZkMarshallingError
             */
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return String.valueOf(data).getBytes(StandardCharsets.UTF_8);
            }

            /**
             * 反序列化
             * @param bytes
             * @return
             * @throws ZkMarshallingError
             */
            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        });
    }

    /**
     * 根据全类名 从zk获取服务
     * 获取子节点数据，转化为 实体信息
     *
     * @param name
     * @return
     */
    @Override
    public List<Service> discoveryServer(String name) {
        Objects.requireNonNull(zkClient, "zkClient is null");
        String path = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + name + "/service";

        return Optional.ofNullable(zkClient.getChildren(path))
                .orElse(Lists.newArrayList())
                .stream()
                .map(str -> {
                    String decode = null;
                    try {
                        decode = URLDecoder.decode(str, RpcConstant.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return JSON.parseObject(decode, Service.class);
                })
                .collect(Collectors.toList());
    }

    public ZkClient getZkClient() {
        return zkClient;
    }
}
