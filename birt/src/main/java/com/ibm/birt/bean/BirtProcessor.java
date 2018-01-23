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

package com.ibm.birt.bean;

import com.ibm.birt.annotation.Renderer;
import com.ibm.birt.renderer.BirtRenderer;
import com.ibm.birt.renderer.FlexRenderer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.report.engine.api.UnsupportedFormatException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class BirtProcessor {
    private BirtConfiguration configuration;
    private BirtRenderer birtRenderer;
    private FlexRenderer flexRenderer;

    public BirtProcessor(BirtConfiguration configuration, BirtRenderer birtRenderer, FlexRenderer flexRenderer) {
        this.configuration = configuration;
        this.birtRenderer = birtRenderer;
        this.flexRenderer = flexRenderer;
    }

    public void renderReport() throws Exception {
        // decide if using Birt or flexmark
        String filename = configuration.getProperties().getReport().getFile().getFilename();
        String suffix = filename.substring(filename.lastIndexOf("."), filename.length());

        InputStream inputStream = configuration.getProperties().getReport().getFile().getInputStream();
        if (suffix.equals(BirtRenderer.class.getAnnotation(Renderer.class).filetype()))
            birtRenderer.render(inputStream, configuration.getProperties().getOutputFile(), configuration.getProperties().getOutputFormat(), configuration.getProperties().getReport().getParams());
        else if (suffix.equals(FlexRenderer.class.getAnnotation(Renderer.class).filetype()))
            flexRenderer.render(inputStream, configuration.getProperties().getOutputFile(), configuration.getProperties().getOutputFormat(), configuration.getProperties().getReport().getParams());
        else
            throw new UnsupportedFormatException("BIRT-002", new RuntimeException("Report Template Type (" + suffix + ") does not match a valid Renderer. Use " +
                    BirtRenderer.class.getAnnotation(Renderer.class).filetype() + " or " +
                    FlexRenderer.class.getAnnotation(Renderer.class).filetype() + " as ReportTemplate."));
    }
}
