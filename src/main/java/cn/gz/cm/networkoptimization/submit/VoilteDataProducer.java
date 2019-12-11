package cn.gz.cm.networkoptimization.submit;

import cn.gz.cm.networkoptimization.handler.DataHandler;
import cn.gz.cm.networkoptimization.handler.ExceptionHandler;
import cn.gz.cm.networkoptimization.log.Log4jUtil;
import cn.gz.cm.networkoptimization.props.PropertiesUtil;
import cn.gz.cm.networkoptimization.transport.DataPool;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * VoilteDataProducer为程序提交入口类
 */
public class VoilteDataProducer {

    public static void main(String[] args) {
        //public.properties路径
        String publicCongPath = args[0];
        //文件时间
        String time =args[1];
        Logger logger =null;
        DataHandler dataHandler=null;
        try {
            //配置文件加载
//            Properties publicConf = PropertiesUtil.getProperties("G:\\Users\\lkz\\IdeaProjects\\networkoptimization\\src\\main\\java\\cn\\gz\\cm\\networkoptimization\\props\\public.properties");
            Properties publicConf = PropertiesUtil.getProperties(publicCongPath);
            //设置时间
            publicConf.setProperty("time",time);
            String logConfPath = publicConf.getProperty("log4j.conf.path");
            String kafkaConfPath = publicConf.getProperty("kafka.conf.path");

            String loginConfig = publicConf.getProperty("java.security.auth.login.config");
            String krb5Conf = publicConf.getProperty("java.security.krb5.conf");
            String useSubjectCredsOnly = publicConf.getProperty("javax.security.auth.useSubjectCredsOnly");
            String krb5Debug = publicConf.getProperty("sun.security.krb5.debug");
            Log4jUtil.setConfPath(logConfPath);
            logger = Log4jUtil.getLogger(VoilteDataProducer.class);
            logger.info("Start execution Voilte Data Kafka Producer process......");
            Properties proKafka = PropertiesUtil.getProperties(kafkaConfPath);

            logger.info("Kafka config:"+proKafka.toString());

            logger.info("Public config:"+publicConf.toString());

            PropertiesUtil.setPublicConf(publicConf);//注册公有配置文件属性
            int kafkaPoolSize = Integer.parseInt(publicConf.getProperty("kafkapool.size"));
            int readPoolSize = Integer.parseInt(publicConf.getProperty("readpool.size"));
            int writePoolSize = Integer.parseInt(publicConf.getProperty("writepool.size"));
            int dataPoolSize = Integer.parseInt(publicConf.getProperty("datapool.size"));
            String fileDir = publicConf.getProperty("file.dir");


            System.setProperty("java.security.auth.login.config",loginConfig);
            System.setProperty("java.security.krb5.conf", krb5Conf);
            System.setProperty("javax.security.auth.useSubjectCredsOnly", useSubjectCredsOnly);
            System.setProperty("sun.security.krb5.debug", krb5Debug);

            DataPool.initPool(dataPoolSize);//初始化数据缓冲池

            File csvFile1 = new File(fileDir);

            dataHandler = new DataHandler(readPoolSize,writePoolSize);//数据处理类
            logger.info("Start read file......");
            dataHandler.fileReadHandler(csvFile1);//文件读取
            logger.info("Start send data......");
            dataHandler.sendDataToKafka(proKafka,kafkaPoolSize);//数据发送到kafka

        } catch (InterruptedException e) {
            ExceptionHandler.exceptionHandler(e,logger);
            e.printStackTrace();
        } catch (ExecutionException e) {
            ExceptionHandler.exceptionHandler(e,logger);
            e.printStackTrace();
        } catch (IOException e) {
            ExceptionHandler.exceptionHandler(e,logger);
            e.printStackTrace();
        } finally {
            try {
                if(dataHandler!=null) dataHandler.waitComplete();
            } catch (ExecutionException e) {
                ExceptionHandler.exceptionHandler(e,logger);
                e.printStackTrace();
            } catch (InterruptedException e) {
                ExceptionHandler.exceptionHandler(e,logger);
                e.printStackTrace();
            }
        }
    }
}
