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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;

class AbstractRenderer {

    private final FileAttribute<?>[] fileAttributes = new FileAttribute<?>[]{};


    void renderOutputFormat(IRenderer renderer, BirtConfiguration configuration, BirtProperties.OutputFormat outputFormat) throws Exception {
        assertNotNull("IRenderer must not be null", renderer);
        assertNotNull("configuration must not be null", configuration);
        Path outputPath = configuration.getProperties().getOutputPath();
        Files.createDirectories(outputPath, fileAttributes);
        File outputFile = new File(outputPath.toFile(), UUID.randomUUID() + "." + outputFormat.name());
        FileOutputStream fos = new FileOutputStream(outputFile);
        renderer.render(
                configuration.getProperties().getReport().getFile().getInputStream(),
                configuration.getProperties().getOutputFormat(),
                configuration.getProperties().getReport().getParams()
        ).writeTo(fos);
        fos.close();
        assertNotNull("rendered document must exist", outputFile);
    }

}
