package cn.gz.cm.networkoptimization.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jUtil {


    public static void setConfPath(String confPath) {
        PropertyConfigurator.configure(confPath);
    }

    public static Logger getLogger(Class clazz){
       // PropertyConfigurator.configure("G:\\Users\\lkz\\IdeaProjects\\networkoptimization\\src\\main\\java\\cn\\gz\\cm\\networkoptimization\\log\\log4j.properties");
        //PropertyConfigurator.configure(confPath);
        return Logger.getLogger(clazz);
    }
}
