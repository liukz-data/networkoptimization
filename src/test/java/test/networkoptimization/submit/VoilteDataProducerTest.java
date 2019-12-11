package test.networkoptimization.submit;

import org.apache.log4j.Logger;
import test.networkoptimization.handler.DataHandler;
import test.networkoptimization.log.Log4jUtil;
import test.networkoptimization.props.PropertiesUtil;
import test.networkoptimization.transport.DataPool;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * VoilteDataProducer为程序提交入口类
 */
public class VoilteDataProducerTest {

    public static void main(String[] args) {
        //public.properties路径
        String publicCongPath = args[0];
        //文件时间
        String time =args[1];
        Logger logger =null;
        DataHandler dataHandler=null;
        System.out.println(111);
        try {
            //配置文件加载
//            Properties publicConf = PropertiesUtil.getProperties("G:\\Users\\lkz\\IdeaProjects\\networkoptimization\\src\\main\\java\\cn\\gz\\cm\\networkoptimization\\props\\public.properties");
            Properties publicConf = PropertiesUtil.getProperties(publicCongPath);
            //设置时间
            publicConf.setProperty("time",time);
            String logConfPath = publicConf.getProperty("log4j.conf.path");
            String kafkaConfPath = publicConf.getProperty("kafka.conf.path");
            Log4jUtil.setConfPath(logConfPath);
            logger = Log4jUtil.getLogger(DataHandler.class);
            logger.info("Start execution Voilte Data Kafka Producer process......");
            Properties proKafka = PropertiesUtil.getProperties(kafkaConfPath);

            logger.info("Kafka config:"+proKafka.toString());

            logger.info("Public config:"+proKafka.toString());

            PropertiesUtil.setPublicConf(publicConf);//注册公有配置文件属性
            int kafkaPoolSize = Integer.parseInt(publicConf.getProperty("kafkapool.size"));
            int readPoolSize = Integer.parseInt(publicConf.getProperty("readpool.size"));
            int writePoolSize = Integer.parseInt(publicConf.getProperty("writepool.size"));
            int dataPoolSize = Integer.parseInt(publicConf.getProperty("datapool.size"));
            String fileDir = publicConf.getProperty("file.dir");

            DataPool.initPool(dataPoolSize);//初始化数据缓冲池

            File csvFile1 = new File(fileDir);

            dataHandler = new DataHandler(readPoolSize,writePoolSize);//数据处理类
            logger.info("Start read file......");
            dataHandler.fileReadHandler(csvFile1);//文件读取
            logger.info("Start send data......");
            dataHandler.sendDataToKafka(proKafka,kafkaPoolSize);//数据发送到kafka

        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }finally {
            try {
                if(dataHandler!=null) dataHandler.waitComplete();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
