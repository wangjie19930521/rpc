package com.wj.tfht.rpc.client.net;

import com.wj.tfht.rpc.common.Service;
import com.wj.tfht.rpc.common.model.RpcRequest;
import com.wj.tfht.rpc.common.model.RpcResponse;
import com.wj.tfht.rpc.common.protocol.MessageProtocol;

public interface NetClient {

    RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol);
}
