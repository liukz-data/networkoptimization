package test.networkoptimization.factory;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * 此为kafka生产者工具包
 * @param <K> kafka生产者的Key数据类型
 * @param <V> kafka生产者的Value数据类型
 */
public class KafkaProducerUtil<K,V> extends KafkaProducer<K,V> {

    private KafkaProducerPool producerPool;//生产者工厂

    /**
     *
     * @param properties kafka生产者配置文件
     * @param producerFactory kafka工厂类
     */
    public KafkaProducerUtil(Properties properties, KafkaProducerPool<K,V> producerFactory) {
        this(properties);
        this.producerPool=producerFactory;

    }

    /**
     *
     * @param properties 必须写的一个类
     */
    public KafkaProducerUtil(Properties properties) {
        super(properties);
    }

    /**
     * 将连接放回连接池
     * @throws InterruptedException
     */
    public void disconnect() throws InterruptedException {
        producerPool.closeKafkaProducer(this);
    }
}
