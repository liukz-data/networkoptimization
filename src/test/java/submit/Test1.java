package submit;

import cn.gz.cm.networkoptimization.factory.KafkaProducerPool;
import cn.gz.cm.networkoptimization.factory.KafkaProducerUtil;
import cn.gz.cm.networkoptimization.log.Log4jUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test1 {
    public static void main(String[] args) {
        Log4jUtil.setConfPath("G:\\Users\\lkz\\IdeaProjects\\networkoptimization\\src\\main\\java\\cn\\gz\\cm\\networkoptimization\\log\\log4j.properties");

        Logger logger = Log4jUtil.getLogger( KafkaProducerTest.class);
        String ftpFileDir="";
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.43.3:9092");
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

       ExecutorService executorService = Executors.newFixedThreadPool(3);
        KafkaProducerPool<String,String> producerPool = new KafkaProducerPool<>();
        try {
            producerPool.initProducerPool(props,3);


            for(int i = 0; i < 10; i++) {
                KafkaProducerUtil<String,String> producer = producerPool.getKafkaProducer();
                logger.info("1111111");
                producer.send(new ProducerRecord<String, String>("test", Integer.toString(i),"zhangsan:"+ Integer.toString(i)));
                producer.disconnect();
            }

            producerPool.closeKafkaProducerPool();
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }
}
