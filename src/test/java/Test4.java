import cn.gz.cm.networkoptimization.props.PropertiesUtil;

import java.io.File;
import java.util.Properties;

public class Test4 {
    public static void main(String[] args) throws Exception {
        System.out.println("20181010101000".substring(0,8));

        File fileDir = new File("/data/ftp/data/csv/mro");
        String fileAbsolutePath = fileDir.getAbsolutePath();
        Properties pro = PropertiesUtil.getProperties("G:\\Users\\lkz\\IdeaProjects\\networkoptimization\\src\\main\\java\\cn\\gz\\cm\\networkoptimization\\props\\public.properties");
        pro.setProperty("time","20181010101000");
        PropertiesUtil.setPublicConf(pro);
        Properties publicConf = PropertiesUtil.getPublicConf();
        String filePrefix = publicConf.getProperty("file.prefix");
        String time = publicConf.getProperty("time");
        String date = time.substring(0,8);
        String fileNameCsv = filePrefix+time+".csv";

        String filePath = fileAbsolutePath+"\\"+date+"\\"+fileNameCsv;
        File fileCsv = new File(filePath);
        System.out.println(filePath);
    }
}
