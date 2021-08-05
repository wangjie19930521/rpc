package com.wj.tfht.rpc.common.constant;

import java.util.Arrays;

public enum RpcStatusEnum {
    /**
     * SUCCESS
     */
    SUCCESS(200, "SUCCESS"),
    /**
     * ERROR
     */
    ERROR(500, "ERROR"),
    /**
     * NOT FOUND
     */
    NOT_FOUND(404, "NOT FOUND");

    private int code;

    private String desc;

    RpcStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public RpcStatusEnum parse(int code) {
        return Arrays.stream(values())
                .filter(rpcStatusEnum -> rpcStatusEnum.code == code)
                .findFirst().orElse(null);
    }

}
