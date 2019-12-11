package cn.gz.cm.networkoptimization.handler;

public class Accumulator {
     private final  Object lockObj = new Object();
     private  volatile long middleValue = 0L;
     public  void accumulation(){
        synchronized(lockObj){
            middleValue = middleValue+1L;
        }
    }
    public  long getValue(){
         return middleValue;
    }
}
