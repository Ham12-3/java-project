package junit.framework;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class TestCase extends Assert implements Test {
    private String name;

    protected TestCase() {
    }

    protected TestCase(String name) {
        this.name = name;
    }

    @Override
    public int countTestCases() {
        return 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void run(TestResult result) {
        result.run(this);
    }

    protected void runTest() throws Throwable {
        if (name == null) {
            throw new IllegalStateException("No test name set");
        }
        Method method = getClass().getMethod(name);
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalStateException("Method " + name + " should be public");
        }
        method.invoke(this);
    }

    public void runBare() throws Throwable {
        setUp();
        try {
            runTest();
        } finally {
            tearDown();
        }
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
}
