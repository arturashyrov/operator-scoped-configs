package se.tain.autoconfigure;

/**
 * Created by ruel on 9/26/16.
 */
public class ScopeContext {
    private static ThreadLocal<String> currentOperatorContext = new ThreadLocal<>();

    public static void set(String operator) {
        currentOperatorContext.set(operator);
    }

    public static String get() {
        return currentOperatorContext.get();
    }

    public static void clear() {
        currentOperatorContext.remove();
    }
}
