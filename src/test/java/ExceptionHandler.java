public class ExceptionHandler {
    public static void exceptionHandler(Exception e) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            System.out.println(stackTraceElement);
        }
    }
}