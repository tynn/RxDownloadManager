package berlin.volders.rxdownload.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public final class EnvironmentRule implements TestRule {

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                String originalValue = DIRECTORY_DOWNLOADS;
                DIRECTORY_DOWNLOADS = "Download";
                try {
                    base.evaluate();
                } finally {
                    DIRECTORY_DOWNLOADS = originalValue;
                }
            }
        };
    }
}
