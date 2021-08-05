package com.wj.tfht.rpc.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @ClassName Service
 * @Description: 服务模型
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Getter
@Setter
public class Service {

    /**
     * 服务名称
     */
    private String name;
    /**
     * 服务协议
     */
    private String protocol;
    /**
     * 服务地址，格式：ip:port
     */
    private String address;
    /**
     * 权重，越大优先级越高
     */
    private Integer weight;

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", protocol='" + protocol + '\'' +
                ", address='" + address + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(name, service.name) &&
                Objects.equals(protocol, service.protocol) &&
                Objects.equals(address, service.address) &&
                Objects.equals(weight, service.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocol, address, weight);
    }
}
