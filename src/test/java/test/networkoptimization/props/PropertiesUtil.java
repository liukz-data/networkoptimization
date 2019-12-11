package test.networkoptimization.props;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    private static Properties publicConf;
    public static Properties getProperties(String confPath) throws IOException {
        Properties pro = new Properties();
        FileInputStream finalConfFileInputStream = new FileInputStream(confPath);
        pro.load(finalConfFileInputStream);
        return pro;
    }

    public static Properties getPublicConf() {
        return publicConf;
    }

    public static void setPublicConf(Properties publicConf) {
        PropertiesUtil.publicConf = publicConf;
    }
}
