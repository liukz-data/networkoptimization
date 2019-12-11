package submit;

import cn.gz.cm.networkoptimization.source.FileUtil;
import cn.gz.cm.networkoptimization.transport.DataPool;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) {

        ExecutorService readThreadPool = Executors.newFixedThreadPool(3);//生产者线程池
        ExecutorService writeThreadPool = Executors.newFixedThreadPool(1);//消费者线程池
        //初始化队列容量
        DataPool.initPool(500);
        LinkedBlockingQueue<String> pool = DataPool.getPool();


        //将所有文件对象加入列表,此处为测试，后期会更改
        File csvFile1 = new File("H:\\tmp\\1.txt");
        File csvFile2 = new File("H:\\tmp\\2.txt");
        LinkedList<File> listFiles = new LinkedList<>();
        listFiles.add(csvFile1);
        listFiles.add(csvFile2);

        //返回线程返回值，目的是执行Future.get()确认线程已完成任务
        LinkedList<Future<String>> listResultFiles = new LinkedList<>();

        Future<String> resultReadThreadPool = null;//读文件线程池的执行结果返回值
        Future<String> resultWriteThreadPool = null;//写线程池的执行结果返回值


        FileUtil fileUtil = new FileUtil();
        try {
           resultReadThreadPool = readThreadPool.submit(new Callable<String>() {
                @Override
                public String call() {
                    for (File csvFile:listFiles){
                    Future<String> result = readThreadPool.submit(new Callable<String>() {
                        @Override
                        public String call() {
                            try {
                                fileUtil.readLineUseMemMap(csvFile);//按行读文件，并将结果写入队列
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return "File "+csvFile.getName()+" Execution Complete";
                        }
                    });
                        try {
                            listResultFiles.add(result);//文件读取线程结果的返回值Feature添加到list集合
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    for (Future<String> resultFile:listResultFiles) {//确认执行Feature.get(),确认文件读取线程执行完成
                        try {
                            System.out.println(resultFile.get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        pool.put("stop");//将消费者停止标志发送到队列
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "ReadThreadPool Execution Complete!";
                }
            });

            resultWriteThreadPool = writeThreadPool.submit(new Callable<String>() {
                @Override
                public String call()  {
                    while(true){
                        try {
                            String line = pool.take();//从队列拉取消息
                            if (line.equals("stop")) {//若消息内容为stop停止消费者线程
                                System.out.println("line:"+line);
                                return "WriteThreadPool Execution Complete!";
                            }
                            System.out.println("take:"+line+"数据累积："+pool.size());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            System.out.println(resultReadThreadPool.get());
            readThreadPool.shutdown();//文件读取线程池结束
            System.out.println(resultWriteThreadPool.get());
            writeThreadPool.shutdown();//数据发送线程池结束
            System.out.println("Kafka Producer Process execution Complete");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
