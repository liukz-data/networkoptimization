import cn.gz.cm.networkoptimization.handler.DataHandler;
import cn.gz.cm.networkoptimization.log.Log4jUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Test6 {
    private  static Future future;
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       ExecutorService e1 = Executors.newFixedThreadPool(2);
       ExecutorService e2 =Executors.newFixedThreadPool(5);
        Logger logger = Log4jUtil.getLogger(DataHandler.class);
        Log4jUtil.setConfPath("G:\\Users\\lkz\\IdeaProjects\\networkoptimization\\src\\main\\java\\cn\\gz\\cm\\networkoptimization\\log\\log4j.properties");
        Future f =e1.submit(new Callable<Integer>() {
           @Override
           public Integer call() throws Exception {
               List<Future> list = new ArrayList<>();
               for (int i = 0; i < 3; i++) {
                   //Thread.sleep(1000);
                   Future f =  e1.submit(new CallBackimpl());
                   list.add(f);
               }
              for (Future l:list) l.get();
               return 2;
           }
       });
        List<Future> list = new ArrayList<>();
        Future f1 =e2.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {


                for (int i = 0; i < 20; i++) {
                    Thread.sleep(500);
                    Future f =  e2.submit(new CallBackimpl());
                    list.add(f);
                }

                return 3;

            }
        });

        logger.info(f.get());

        e1.shutdown();
        System.out.println("1 complete");
        //for (Future l:list) l.get();
        future=f1;
        logger.info(future.get());
        e2.shutdown();
        System.out.println("complete");
    }
}
