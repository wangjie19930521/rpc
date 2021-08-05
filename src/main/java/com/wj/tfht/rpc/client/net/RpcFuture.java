package com.wj.tfht.rpc.client.net;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.isNull;

/**
 * @ClassName RpcFuture
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
public class RpcFuture<T> implements Future<T> {

    private T response;

    /**
     * 因为请求和响应是一一对应的，所以这里是1
     */
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    //设置数据时唤醒等待结果线程
    public void setResponse(T response) {
        this.response = response;
        countDownLatch.countDown();
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        if (isNull(response)) {
            return true;
        }
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        countDownLatch.await();

        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (countDownLatch.await(timeout, unit)) {
            return response;
        }
        return null;
    }
}
