package cn.gz.cm.networkoptimization.factory;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * kafka生产者连接池
 * @param <K> kafka生产者的Key数据类型
 * @param <V> kafka生产者的Value数据类型
 */
public class KafkaProducerPool<K,V> {

    private final LinkedBlockingQueue<KafkaProducerUtil<K,V>> pool = new LinkedBlockingQueue<>();//存放连接的队列

    /**
     * 初始化连接池
     * @param kafkaPros kafka生产者配置属性
     * @param poolSize 连接池大小
     * @throws InterruptedException
     */
    public void initProducerPool(Properties kafkaPros, int poolSize) throws InterruptedException {
        for (int i = 0; i < poolSize; i++) {
            KafkaProducerUtil<K,V> producer =  new KafkaProducerUtil<>(kafkaPros,this);
            pool.put(producer);
        }

    }

    public KafkaProducerUtil<K,V> getKafkaProducer() throws InterruptedException {
        return pool.take();
    }

    public void closeKafkaProducer(KafkaProducerUtil<K,V> kafkaProducer) throws InterruptedException {
         pool.put(kafkaProducer);
    }

    /**
     * 关闭所有连接
     * @throws InterruptedException
     */
    public void closeKafkaProducerPool() throws InterruptedException {
        while(pool.iterator().hasNext()){
            pool.take().close();
        }
    }
}
