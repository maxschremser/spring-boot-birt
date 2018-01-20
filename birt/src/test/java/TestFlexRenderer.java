/*
 *
 *  *   Copyright 2018 Maximilian Schremser
 *  *
 *  *   Licensed under the Apache License, Version 2.0 (the "License");
 *  *   you may not use this file except in compliance with the License.
 *  *   You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *   Unless required by applicable law or agreed to in writing, software
 *  *   distributed under the License is distributed on an "AS IS" BASIS,
 *  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *   See the License for the specific language governing permissions and
 *  *   limitations under the License.
 *
 */

import com.ibm.birt.bean.BirtConfiguration;
import com.ibm.birt.bean.BirtProperties;
import com.ibm.birt.renderer.BirtRenderer;
import com.ibm.birt.renderer.FlexRenderer;
import org.eclipse.birt.report.engine.api.UnsupportedFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@SpringBootTest(
        classes = {
                BirtConfiguration.class,
                FlexRenderer.class
        },
        properties = {
                "birt.output-format=html",
                "birt.output-file=out/flex/test",
                "birt.report.file=classpath:simple.md",
                "birt.report.param.firstname=Test",
                "birt.report.param.lastname=Tester",
                "birt.report.param.company=JUnit"
        })
@RunWith(SpringRunner.class)
public class TestFlexRenderer {
    @Autowired
    private BirtConfiguration configuration;

    @Autowired
    private FlexRenderer flexRenderer;


    @Test
    public void testBirtRendererPropertyOutputFormat() {
        assertTrue(configuration.getProperties().getOutputFormat().name() + " != HTML", configuration.getProperties().getOutputFormat().name().equals("HTML"));
    }

    @Test
    public void testBirtRendererPropertyOutputFile() {
        assertTrue(configuration.getProperties().getOutputFile().getPath() + " != out/flex/test", configuration.getProperties().getOutputFile().getPath().equals("out/flex/test"));
    }

    private void renderOutputFormat(BirtProperties.OutputFormat outputFormat) throws Exception {
        // overwrite output-format property
        configuration.getProperties().setOutputFormat(outputFormat);

        assertNotNull("birtRenderer must not be null", flexRenderer);
        assertNotNull("birtRenderer.configuration must not be null", flexRenderer.getConfiguration());
        flexRenderer.render(configuration.getProperties().getReport().getFile().getInputStream());
        assertNotNull("rendered document must exist", configuration.getProperties().getOutputFile());
    }

    @Test
    public void testBirtRendererRenderHTMLF() throws Exception {
        renderOutputFormat(BirtProperties.OutputFormat.HTML);
    }

    @Test
    public void testBirtRendererRenderPDF() throws Exception {
        renderOutputFormat(BirtProperties.OutputFormat.PDF);
    }

    @Test
    public void testBirtRendererRenderMSWord() throws Exception {
        renderOutputFormat(BirtProperties.OutputFormat.MS_WORD);
    }

    @Test
    public void testBirtRendererRenderText() throws Exception {
        renderOutputFormat(BirtProperties.OutputFormat.TEXT);
    }

}