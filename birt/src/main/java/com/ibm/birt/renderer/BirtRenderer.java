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

import com.ibm.birt.annotation.Renderer;
import com.ibm.birt.bean.BirtConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.engine.api.impl.ParameterValidationException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@Renderer(filetype = ".rptdesign")
public class BirtRenderer extends AbstractRenderer {
    public BirtRenderer(BirtConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void render(InputStream inputStream) throws Exception {
        IReportEngine engine;
        EngineConfig config = new EngineConfig();
        Platform.startup(config);

        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        engine = factory.createReportEngine(config);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        IReportRunnable design = engine.openReportDesign("RT001", inputStream);
        inputStream.close();

        IRunAndRenderTask task = engine.createRunAndRenderTask(design);
        IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
        for (Object o : paramTask.getParameterDefns(false)) {
            IParameterDefnBase rptParam = (IParameterDefnBase) o;
            if (rptParam.getParameterType() != IParameterDefnBase.SCALAR_PARAMETER) {
                continue; //only process scalar parameters
            }
            String paramName = rptParam.getName();
            String paramValue = configuration.getProperties().getReport().getParams().get(paramName);

            if (paramValue != null) {
                task.setParameterValue(paramName, paramValue);
                String paramValueLog = paramValue;
                if (paramName.contains("pwd")) {
                    paramValueLog = "*****";
                }
                log.info("ReportParam: {}={}", paramName, paramValueLog);
            } else {
                log.error("ReportParam: '{}' cannot be resolved", paramName);
                throw new ParameterValidationException(new BirtException("Report Parameter '" + paramName + "' cannot be resolved."));
            }
        }

        task.validateParameters();

        //Set rendering options - such as file or stream output,
        IRenderOption options;
        log.info("Output Format is: {}", configuration.getProperties().getOutputFormat());
        switch (configuration.getProperties().getOutputFormat()) {
            case HTML:
                options = new HTMLRenderOption();
                options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
                break;
            case PDF:
                options = new PDFRenderOption();
                options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
                break;
            case MS_WORD:
                options = new DocxRenderOption();
                options.setOutputFormat("docx");
                break;
            case TEXT:
                throw new UnsupportedFormatException("BIRT-001", new RuntimeException("OutputFormat 'text' is not supported for BIRT."));
            default:
                throw new UnsupportedFormatException("BIRT-001", new RuntimeException("OutputFormat 'text' is not supported for BIRT."));

        }
        options.setOutputStream(out);
        options.setImageHandler(new HTMLServerImageHandler() {
            @Override
            protected String handleImage(IImage image, Object context, String prefix, boolean needMap) {
                try {
                    final String content = Base64.getEncoder().encodeToString(IOUtils.toByteArray(image.getImageStream()));
                    return "data:" + image.getMimeType() + ";base64," + content;
                } catch (Exception ignore) {
                }
                return "";
            }
        });
        task.setRenderOption(options);
        task.setErrorHandlingOption(IEngineTask.CANCEL_ON_ERROR);
        task.run(); // Run the Render Task
        task.close();
        List errors = task.getErrors();
        for (Object error : errors) {
            log.error(error.toString());
        }
        log.debug(out.toString());

        File outputFile = configuration.getProperties().getOutputFile();

        if (!outputFile.getName().matches("^.*\\.(pdf|htm|html|txt|doc|docx)$"))
            outputFile = new File(outputFile.getParentFile(), outputFile.getName() + "." + getFileEnding(configuration.getProperties().getOutputFormat()));
        if (!outputFile.getParentFile().exists())
            Files.createDirectory(outputFile.getParentFile().toPath());
        if (!outputFile.exists())
            Files.createFile(outputFile.toPath());

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(out.toByteArray());
        fos.close();
        log.info("Report has been rendered to {}", outputFile.getAbsolutePath());
    }
}
