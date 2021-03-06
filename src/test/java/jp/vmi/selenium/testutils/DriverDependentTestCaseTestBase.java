package jp.vmi.selenium.testutils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.remote.UnreachableBrowserException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assume.*;

/**
 * Driver dependent test.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public abstract class DriverDependentTestCaseTestBase extends TestCaseTestBase {

    @Parameters(name = "{index}: {0}")
    public static List<Object[]> getWebDriverFactories() {
        return TestUtils.getWebDriverFactories();
    }

    @Parameter
    public String currentFactoryName;

    protected final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    @Override
    protected void initDriver() {
        DriverOptions driverOptions = new DriverOptions();
        if (currentFactoryName.equals(WebDriverManager.FIREFOX)) {
            Path conf = Paths.get("tmp/firefox-bin.conf");
            if (Files.exists(conf)) {
                try {
                    List<String> lines = Files.readAllLines(conf, StandardCharsets.UTF_8);
                    if (!lines.isEmpty()) {
                        for (String line : lines) {
                            if (!line.startsWith("#")) {
                                driverOptions.set(DriverOption.FIREFOX, line);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    // no operation.
                }
            }
        }
        setWebDriverFactory(currentFactoryName, driverOptions);
        try {
            driver = manager.get();
        } catch (UnreachableBrowserException e) {
            Assume.assumeNoException(e);
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        }
    }

    public void assumeNot(String... factoryNames) {
        for (String factoryName : factoryNames)
            assumeThat(currentFactoryName, is(not(factoryName)));
    }
}
