package junit.framework;

public class Assert {
    protected Assert() {
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            fail("expected condition to be true");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            fail("expected condition to be false");
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        fail("expected <" + expected + "> but was <" + actual + ">");
    }

    public static void assertNotNull(Object value) {
        if (value == null) {
            fail("value should not be null");
        }
    }

    public static void fail(String message) {
        throw new AssertionFailedError(message);
    }
}
