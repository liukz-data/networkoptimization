package factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 连接池
 * @param <T> 连接类型
 */
public class ConnectFactory<T>  {

    private  final LinkedBlockingDeque<T>  producerPool = new LinkedBlockingDeque<T>();

    /**
     *
     * @param Connect 连接类型
     * @param numConnects 连接池大小
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InterruptedException
     */
    public void initConnectFactory(Class<T> Connect,int numConnects) throws IllegalAccessException, InstantiationException, InterruptedException {
        for (int i = 0; i <numConnects ; i++) {
            final T t = Connect.newInstance();
            producerPool.put(t);
        }

    }

    //获取连接
    public T getConnect() throws InterruptedException {
        return producerPool.take();
    }


    /**
     * 将连接放回连接池
     * @param connect
     * @throws InterruptedException
     */
    public void close(T connect) throws InterruptedException {
        producerPool.put(connect);
    }

    /**
     * 遍历连接池，将连接关闭
     * 注意：在此前需要将所有连接放回队列
     * @throws InterruptedException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void closeConnectFactory() throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        while(producerPool.size()>0){
            final T connect = producerPool.take();
            Method method = connect.getClass().getMethod("close");
            method.invoke(connect);
        }
    }
}

