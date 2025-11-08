package junit.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestResult {
    private final List<Throwable> failures = new ArrayList<>();

    public void addFailure(Test test, AssertionFailedError error) {
        failures.add(error);
    }

    public void addError(Test test, Throwable error) {
        failures.add(error);
    }

    public int failureCount() {
        return failures.size();
    }

    public void runProtected(Test test, Protectable protectable) {
        try {
            protectable.protect();
        } catch (AssertionFailedError assertionFailedError) {
            addFailure(test, assertionFailedError);
        } catch (Throwable throwable) {
            addError(test, throwable);
        }
    }

    public void run(TestCase test) {
        runProtected(test, () -> {
            test.runBare();
        });
    }

    public List<Throwable> failures() {
        return Collections.unmodifiableList(failures);
    }
}
