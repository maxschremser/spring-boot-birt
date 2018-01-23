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

package com.ibm.birt.renderer;

import com.ibm.birt.bean.BirtProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

@Getter
@Slf4j
public abstract class AbstractRenderer implements IRenderer {

    @Override
    public String getFileEnding(BirtProperties.OutputFormat outputFormat) {
        switch (outputFormat) {
            case HTML:
                return "html";
            case PDF:
                return "pdf";
            case MS_WORD:
                return "docx";
            default:
                return "txt";
        }
    }

    public void render(InputStream inputStream, File outputFile, BirtProperties.OutputFormat outputFormat, Map<String, String> params) throws Exception {
        if (!outputFile.getName().matches("^.*\\.(pdf|htm|html|txt|doc|docx)$"))
            outputFile = new File(outputFile.getParentFile(), outputFile.getName() + "." + getFileEnding(outputFormat));
        if (!outputFile.getParentFile().exists())
            Files.createDirectories(outputFile.getParentFile().toPath());
        if (!outputFile.exists())
            Files.createFile(outputFile.toPath());

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(render(inputStream, outputFormat, params).toByteArray());
        fos.close();
        log.info("Report has been rendered to {}", outputFile.getAbsolutePath());
    }
}
