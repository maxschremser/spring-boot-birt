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
import com.ibm.birt.renderer.IRenderer;

import static junit.framework.Assert.assertNotNull;

class AbstractRenderer {

    void renderOutputFormat(IRenderer renderer, BirtConfiguration configuration, BirtProperties.OutputFormat outputFormat) throws Exception {
        // overwrite output-format property
        configuration.getProperties().setOutputFormat(outputFormat);

        assertNotNull("IRenderer must not be null", renderer);
        assertNotNull("configuration must not be null", configuration);
        renderer.render(configuration.getProperties().getReport().getFile().getInputStream());
        assertNotNull("rendered document must exist", configuration.getProperties().getOutputFile());
    }
}
