package test.networkoptimization.handler;

import cn.gz.cm.networkoptimization.submit.CallBackImpl;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import test.networkoptimization.factory.KafkaProducerPool;
import test.networkoptimization.factory.KafkaProducerUtil;
import test.networkoptimization.log.Log4jUtil;
import test.networkoptimization.props.PropertiesUtil;
import test.networkoptimization.source.FileUtil;
import test.networkoptimization.transport.DataPool;

import java.io.File;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.*;

/***
 * 数据处理类
 */
public class DataHandler {
    private ExecutorService readThreadPool;//生产者线程池
    private ExecutorService writeThreadPool;//消费者线程池
    private LinkedBlockingQueue<String> pool = DataPool.getPool();
    private Future<String> resultReadThreadPool;
    private Future<String> resultWriteThreadPool;
    private KafkaProducerPool<String,String> producerPool = new KafkaProducerPool<>();
    private Random random = new Random();
    private Logger logger = Log4jUtil.getLogger(DataHandler.class);

    /**
     *
     * @param readPoolSize 读线程池大小
     * @param writePoolSize 写线程池大小
     */
    public DataHandler(int readPoolSize, int writePoolSize) {
        readThreadPool = Executors.newFixedThreadPool(readPoolSize);//生产者线程池
        writeThreadPool = Executors.newFixedThreadPool(writePoolSize);//消费者线程池

    }

    /**
     * 文件nio方式，多线程读
     *
     * @param fileDir 包含文件夹名称
     * @return resultReadThreadPool 读线程池的结果
     */
    public Future<String> fileReadHandler(File fileDir) {
        FileUtil fileUtil = new FileUtil();
        /*LinkedList<File> listFiles = fileUtil.getChildFiles(fileDir);*/

        File fileCsv = getCsvFile(fileDir);
        LinkedList<File> listFiles = new LinkedList();
        listFiles.add(fileCsv);

        LinkedList<Future<String>> listResultFiles = new LinkedList<>();//返回线程返回值，目的是执行Future.get()确认线程已完成任务
        //读文件线程池的执行结果返回值
        Future<String> resultReadThreadPool = readThreadPool.submit(new Callable<String>() {
            @Override
            public String call() {
                for (File csvFile : listFiles) {
                    Future<String> result = readThreadPool.submit(new Callable<String>() {
                        @Override
                        public String call() {
                            try {
                                fileUtil.readLineUseMemMap(csvFile);//按行读文件，并将结果写入队列
                            } catch (Exception e) {
                                logger.error(e);
                                e.printStackTrace();

                            }
                            return "File " + csvFile.getName() + " Execution Complete";
                        }
                    });
                    try {
                        listResultFiles.add(result);//文件读取线程结果的返回值Feature添加到list集合
                    } catch (Exception e) {
                        logger.error(e);
                        e.printStackTrace();
                    }

                }
                for (Future<String> resultFile : listResultFiles) {//确认执行Feature.get(),确认文件读取线程执行完成
                    try {
                        logger.info(resultFile.get());
                        System.out.println(resultFile.get());
                    } catch (Exception e) {
                        logger.error(e);
                        e.printStackTrace();
                    }
                }
                try {
                    logger.info("Send stop flag");
                    pool.put("stop");//将消费者停止标志发送到队列
                } catch (InterruptedException e) {
                    logger.error(e);
                    e.printStackTrace();
                }
                return "ReadThreadPool Execution Complete!";
            }
        });
        this.resultReadThreadPool = resultReadThreadPool;
        return resultReadThreadPool;
    }


    /**
     * 从数据队列中拉取消息，并将数据发送kafka
     *
     * @return resultWriteThreadPool 发送数据的执行结果
     */
    public Future<String> sendDataToKafka(Properties kafkaProps, int kafkaPoolSize) throws ExecutionException, InterruptedException {
        Properties publicConf = PropertiesUtil.getPublicConf();
        String topic = publicConf.getProperty("topic");
        String kafkaHeader = publicConf.getProperty("kafka.header.prefix");
        int partitons = Integer.parseInt(publicConf.getProperty("partitions"));
        CallBackImpl callBack = new CallBackImpl();
        producerPool.initProducerPool(kafkaProps,kafkaPoolSize);//初始化生产者连接池
        LinkedList<Future<String>> listResultSends = new LinkedList<>();//返回线程返回值，目的是执行Future.get()确认线程已完成任务
        //写线程池的执行结果返回值
        Future<String> resultWriteThreadPool = writeThreadPool.submit(new Callable<String>() {
            @Override
            public String call() {
                while (true) {
                    try {
                        String line = pool.take();//从队列拉取消息
                        if (line.equals("stop")) {//若消息内容为stop停止消费者线程
                           // System.out.println("line:" + line);
                            return "WriteThreadPool Execution Complete!";
                        }

                        Future<String> result = writeThreadPool.submit(new Callable<String>() {
                            @Override
                            public String call() {
                                try {
                                    KafkaProducerUtil<String,String> producer = producerPool.getKafkaProducer();
                                    producer.send(new ProducerRecord<String, String>(topic, String.valueOf(random.nextInt(partitons)),kafkaHeader+"`"+line),callBack);
                                    producer.disconnect();

                                } catch (InterruptedException e) {
                                    logger.error(e);
                                    e.printStackTrace();
                                }
                                logger.info("Data accumulation：" + pool.size());
                                //System.out.println("take:" + line + "数据累积：" + pool.size());
                                return "WriteThreadPool is Executing!";
                            }
                        });

                        listResultSends.add(result);
                    } catch (InterruptedException e) {
                        logger.error(e);
                        e.printStackTrace();
                    }
                }

            }
        });

        for (Future<String> listResultSend : listResultSends) {
            System.out.println(listResultSend.get());
        }
        this.resultWriteThreadPool = resultWriteThreadPool;
        return resultWriteThreadPool;
    }

    public File getCsvFile(File fileDir){
        String fileAbsolutePath = fileDir.getAbsolutePath();
        Properties publicConf = PropertiesUtil.getPublicConf();
        String filePrefix = publicConf.getProperty("file.prefix");
        String time = publicConf.getProperty("time");
        String date = time.substring(0,8);
        String fileNameCsv = filePrefix+time+".csv";

        String filePath = fileAbsolutePath+"/"+date+"/"+fileNameCsv;
        return new File(filePath);
    }
    public boolean waitComplete() throws ExecutionException, InterruptedException {
        logger.info(resultReadThreadPool.get());
        readThreadPool.shutdown();//文件读取线程池结束
        logger.info(resultWriteThreadPool.get());
        writeThreadPool.shutdown();//数据发送线程池结束
        logger.info("Kafka Producer Process execution Complete!!!");
        producerPool.closeKafkaProducerPool();
        logger.info("Kafka Producer Pool has stop!!!");
        return true;
    }

}
