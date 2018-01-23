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
import org.eclipse.birt.report.engine.api.UnsupportedFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.Assert.assertTrue;

@SpringBootTest(
        classes = {
                BirtConfiguration.class,
                BirtRenderer.class
        },
        properties = {
                "birt.output-format=html",
                "birt.output-file=out/birt/test",
                "birt.report.file=classpath:simple.rptdesign",
                "birt.report.param.firstname=Test",
                "birt.report.param.lastname=Tester",
                "birt.report.param.company=JUnit"
        })
@RunWith(SpringRunner.class)
public class TestBirtRenderer extends AbstractRenderer {

    @Autowired
    private BirtConfiguration configuration;

    @Autowired
    private BirtRenderer renderer;

    @Test
    public void testBirtRendererPropertyOutputFormat() {
        assertTrue(configuration.getProperties().getOutputFormat().name() + " != HTML", configuration.getProperties().getOutputFormat().name().equals("HTML"));
    }

    @Test
    public void testBirtRendererPropertyOutputPath() {
        assertTrue(configuration.getProperties().getOutputPath() + " != out", configuration.getProperties().getOutputPath().toString().equals("out"));
    }

    @Test
    public void testBirtRendererRenderHTML() throws Exception {
        renderOutputFormat(renderer, configuration, BirtProperties.OutputFormat.HTML);
    }

    @Test
    public void testBirtRendererRenderPDF() throws Exception {
        renderOutputFormat(renderer, configuration, BirtProperties.OutputFormat.PDF);
    }

    @Test
    public void testBirtRendererRenderMSWord() throws Exception {
        renderOutputFormat(renderer, configuration, BirtProperties.OutputFormat.MS_WORD);
    }

    @Test
    public void testBirtRendererRenderText() throws Exception {
        renderOutputFormat(renderer, configuration, BirtProperties.OutputFormat.TEXT);
    }

}
