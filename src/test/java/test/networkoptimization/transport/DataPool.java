package test.networkoptimization.transport;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 字符串队列
 */
public class DataPool {

    private static LinkedBlockingQueue<String> pool  ;

    public static void initPool(int poolSize){
        if(pool == null){
            pool = new LinkedBlockingQueue(poolSize);;
        }
    }
    public  static LinkedBlockingQueue<String> getPool() {
        return pool;
    }


}
