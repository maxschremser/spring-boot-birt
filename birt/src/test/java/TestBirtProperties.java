import com.ibm.birt.BirtProperties;
import org.junit.Test;

import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.regex.Matcher;

import static junit.framework.Assert.assertTrue;

public class TestBirtProperties {

    @Test
    public void testDatasetPattern() throws NoSuchFieldException, IOException {
        String regexp = BirtProperties.Report.Param.class.getDeclaredField("dataset").getAnnotation(Pattern.class).regexp();
        String value = "{ \"firstname\": \"Test\", \"lastname\": \"Tester\", \"company\": \"Testing Company\" }";
        Matcher matcher = java.util.regex.Pattern.compile(regexp).matcher(value);
        assertTrue("BIRT Report Parameter value (" + value + ") must match regular expression (" + regexp + ")", matcher.matches());
    }

    @Test
    public void testEnvDatasetPattern() throws NoSuchFieldException, IOException {
        String regexp = BirtProperties.Report.Param.class.getDeclaredField("dataset").getAnnotation(Pattern.class).regexp();
        String value = "{ \"firstname\": \"Maximilian\", \"lastname\": \"Schremser\", \"company\": \"Funny way on the environment of the edge\" }";
        Matcher matcher = java.util.regex.Pattern.compile(regexp).matcher(value);
        assertTrue("BIRT Report Parameter value (" + value + ") must match regular expression (" + regexp + ")", matcher.matches());
    }

    @Test
    public void testOptionDatasetPattern() throws NoSuchFieldException, IOException {
        String regexp = BirtProperties.Report.Param.class.getDeclaredField("dataset").getAnnotation(Pattern.class).regexp();
        String value = "{ \"firstname\": \"Maximilian\", \"lastname\": \"Schremser\", \"company\": \"Funny way to use the option of the edge\" }";
        Matcher matcher = java.util.regex.Pattern.compile(regexp).matcher(value);
        assertTrue("BIRT Report Parameter value (" + value + ") must match regular expression (" + regexp + ")", matcher.matches());
    }
}
