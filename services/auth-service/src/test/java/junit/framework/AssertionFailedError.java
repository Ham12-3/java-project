package junit.framework;

public class AssertionFailedError extends AssertionError {
    public AssertionFailedError() {
        super();
    }

    public AssertionFailedError(String message) {
        super(message);
    }
}
