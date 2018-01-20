import com.github.lalyos.jfiglet.FigletFont;
import com.ibm.birt.bean.BirtConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                BirtConfiguration.class
        })
@TestPropertySource(locations = {"classpath:application.properties"})
public class PrintBanner {
    @Value("${spring.application.name}")
    String applicationName;

    @Test
    public void testPrintBanner() throws IOException {
        File banner = new File("banner.txt");
        FileOutputStream fos = new FileOutputStream(banner);
        fos.write(FigletFont.convertOneLine(applicationName).getBytes());
        fos.close();
    }
}
