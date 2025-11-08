package junit.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSuite implements Test {
    private final List<Test> tests = new ArrayList<>();

    public TestSuite() {
    }

    public TestSuite(Class<? extends TestCase> testClass) {
        addTestMethods(testClass);
    }

    @Override
    public int countTestCases() {
        int count = 0;
        for (Test test : tests) {
            count += test.countTestCases();
        }
        return count;
    }

    @Override
    public void run(TestResult result) {
        for (Test test : tests) {
            test.run(result);
        }
    }

    public void addTest(Test test) {
        tests.add(test);
    }

    public List<Test> tests() {
        return Collections.unmodifiableList(tests);
    }

    private void addTestMethods(Class<? extends TestCase> testClass) {
        for (Method method : testClass.getMethods()) {
            if (isTestMethod(method)) {
                addTest(createTest(testClass, method.getName()));
            }
        }
    }

    protected Test createTest(Class<? extends TestCase> testClass, String name) {
        try {
            Constructor<? extends TestCase> constructor = testClass.getConstructor();
            TestCase testCase = constructor.newInstance();
            testCase.setName(name);
            return testCase;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to instantiate test class", e);
        }
    }

    private boolean isTestMethod(Method method) {
        return method.getName().startsWith("test")
                && method.getParameterCount() == 0
                && method.getReturnType() == Void.TYPE;
    }
}
