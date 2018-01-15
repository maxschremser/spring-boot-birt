/*
 *   Copyright 2018 Maximilian Schremser
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.ibm.birt;

import org.apache.commons.io.IOUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
public class BirtProcessor {
    private static Logger log = LoggerFactory.getLogger(BirtProcessor.class);
    private BirtConfiguration configuration;

    public BirtProcessor(BirtConfiguration configuration) {
        this.configuration = configuration;
    }

    public void renderReport() throws BirtException, IOException {
        IReportEngine engine;
        EngineConfig config = new EngineConfig();
        Platform.startup(config);

        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        engine = factory.createReportEngine(config);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        IReportRunnable design = engine.openReportDesign("RT001", getInputStream());
        IRunAndRenderTask task = engine.createRunAndRenderTask(design);
        IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
        for (Object o : paramTask.getParameterDefns(false)) {
            IParameterDefnBase rptParam = (IParameterDefnBase) o;
            if (rptParam.getParameterType() != IParameterDefnBase.SCALAR_PARAMETER) {
                continue; //only process scalar parameters
            }
            String paramName = rptParam.getName();
            String paramValue = configuration.getProperties().getReport().getParam().getDataset();

            if (paramValue != null) {
                task.setParameterValue(paramName, paramValue);
                String paramValueLog = paramValue;
                if (paramName.contains("pwd")) {
                    paramValueLog = "*****";
                }
                log.info("ReportParam: {} resolved to: {}", paramName, paramValueLog);
            } else {
                log.info("ReportParam: {} cannot be resolved", paramName);
            }
        }

        task.validateParameters();

        //Set rendering options - such as file or stream output,
        IRenderOption options;
        options = new HTMLRenderOption();
        log.debug("Output Format is: {}", configuration.getProperties().getOutputFormat());
        options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
//        ((HTMLRenderOption) options).setEmbeddable(false);
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
        options.setOutputStream(out);
        task.setRenderOption(options);
        task.setErrorHandlingOption(IEngineTask.CANCEL_ON_ERROR);
        task.run(); // Run the Render Task
        task.close();
        log.debug(out.toString());

        //noinspection ResultOfMethodCallIgnored
        configuration.getProperties().getOutputFile().createNewFile();
        FileOutputStream fos = new FileOutputStream(configuration.getProperties().getOutputFile());
        fos.write(out.toByteArray());
        fos.close();

        log.info("Report has been rendered to {}", configuration.getProperties().getOutputFile().getAbsolutePath());
    }

    public InputStream getInputStream() throws IOException {
        return configuration.getProperties().getReport().getFile().getInputStream();
    }


}
