import com.github.lalyos.jfiglet.FigletFont;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintBanner {
    @Test
    public void testPrintBanner() throws IOException {
        File banner = new File("src/main/resources", "banner.txt");
        FileOutputStream fos = new FileOutputStream(banner);
        fos.write(FigletFont.convertOneLine("Birt-Engine").getBytes());
        fos.close();
    }
}
