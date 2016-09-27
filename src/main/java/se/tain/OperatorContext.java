package se.tain;

/**
 * Created by ruel on 9/26/16.
 */
public class OperatorContext {
    private static ThreadLocal<String> currentOperatorContext = new ThreadLocal<>();

    public static void setCurrentOperator(String operator) {
        currentOperatorContext.set(operator);
    }

    public static String getCurrentOperator() {
        return currentOperatorContext.get();
    }

    public static void clear() {
        currentOperatorContext.remove();
    }
}
