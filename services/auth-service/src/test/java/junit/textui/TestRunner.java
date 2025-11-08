package junit.textui;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class TestRunner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No test class provided");
            System.exit(1);
        }
        try {
            Class<?> clazz = Class.forName(args[0]);
            run(clazz);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static TestResult run(Test test) {
        TestResult result = new TestResult();
        test.run(result);
        if (result.failureCount() > 0) {
            for (Throwable failure : result.failures()) {
                failure.printStackTrace(System.err);
            }
            throw new AssertionError("Test failures: " + result.failureCount());
        }
        return result;
    }

    public static TestResult run(Class<?> testClass) {
        if (!Test.class.isAssignableFrom(testClass)) {
            throw new IllegalArgumentException("Not a junit.framework.Test class");
        }
        @SuppressWarnings("unchecked")
        Class<? extends Test> cast = (Class<? extends Test>) testClass;
        TestSuite suite = new TestSuite((Class) cast);
        return run(suite);
    }
}
