package test.networkoptimization.submit;

import cn.gz.cm.networkoptimization.handler.DataHandler;
import cn.gz.cm.networkoptimization.log.Log4jUtil;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

public class CallBackImpl implements Callback {
    private Logger logger = Log4jUtil.getLogger(DataHandler.class);
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if(recordMetadata!=null){
            logger.info("partition:"+recordMetadata.partition()+" value size:"+recordMetadata.serializedValueSize());

        }else{
            logger.error(e);
            e.printStackTrace();
        }

    }
}
