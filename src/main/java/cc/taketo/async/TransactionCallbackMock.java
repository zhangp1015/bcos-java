package cc.taketo.async;

import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Title: TransactionCallbackMock
 * @Package: cc.taketo.async
 * @Description: 回调类
 * 异步发送交易的时候，可以自定义回调类，实现和重写回调处理函数。
 * 自定义的回调类需要继承抽象类TransactionCallback, 实现onResponse方法。同时，可按需决定是否需要重写onError、onTimeout等方法。
 * 例如，我们定义一个简单的回调类。该回调类实现了一个基于可重入锁的异步调用效果，可减少线程的同步等待时间。
 * @Author: zhangp
 * @Date: 2022/11/16 - 10:18
 */

public class TransactionCallbackMock extends TransactionCallback {
    private TransactionReceipt transactionReceipt;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition;

    public TransactionCallbackMock() {
        condition = reentrantLock.newCondition();
    }

    public TransactionReceipt getResult() {
        try {
            reentrantLock.lock();
            while (transactionReceipt == null) {
                condition.awaitUninterruptibly();
            }
            return transactionReceipt;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void onResponse(TransactionReceipt transactionReceipt) {
        try {
            reentrantLock.lock();
            this.transactionReceipt = transactionReceipt;
            condition.signal();
        } finally {
            reentrantLock.unlock();
        }
    }
}
