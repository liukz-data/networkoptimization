import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

public class CallBackimpl implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println(Thread.currentThread());
        return 1;
    }
}
