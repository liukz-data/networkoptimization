import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Test7 {
    public static void main(String[] args) {
        try {
            new FileInputStream(new File("/"));
        } catch (Exception e) {
            ExceptionHandler.exceptionHandler(e);
        }
    }
}
