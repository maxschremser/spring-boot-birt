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

package com.ibm.birt.bean.rest;

import com.ibm.birt.bean.BirtConfiguration;
import com.ibm.birt.bean.BirtProperties;
import com.ibm.birt.renderer.BirtRenderer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Path("/birt")
public class BirtResource {

    BirtConfiguration configuration;
    BirtRenderer birtRenderer;

    private final FileAttribute<?>[] fileAttributes = new FileAttribute<?>[]{};

    public BirtResource(BirtConfiguration configuration, BirtRenderer birtRenderer) {
        this.configuration = configuration;
        this.birtRenderer = birtRenderer;
    }

    @GET
    @Path("/hello")
    public String test() {
        return "Hello World";
    }

    private ByteArrayOutputStream getForFormat(String dataSet, BirtProperties.OutputFormat outputFormat) throws Exception {
        if (dataSet == null) {
            dataSet = configuration.getProperties().getReport().getParams().get("dataSet");
        }
        Map<String, String> params = new HashMap<>();
        params.put("dataSet", dataSet);
        return birtRenderer.render(configuration.getProperties().getReport().getFile().getInputStream(), outputFormat, params);
    }

    @GET
    @Path("/report.html")
    @Produces(value = MediaType.TEXT_HTML)
    public StreamingOutput renderReportHtml(@QueryParam("dataSet") String dataSet) throws Exception {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    getForFormat(dataSet, BirtProperties.OutputFormat.HTML).writeTo(output);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        };
    }

    @GET
    @Path("/report.pdf")
    @Produces("application/x-pdf")
    public StreamingOutput renderReportPdf(@QueryParam("dataSet") String dataSet) throws Exception {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    getForFormat(dataSet, BirtProperties.OutputFormat.PDF).writeTo(output);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        };
    }

    @GET
    @Path("/report.doc")
    @Produces("application/msword")
    public StreamingOutput renderReportDoc(@QueryParam("dataSet") String dataSet) throws Exception {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    getForFormat(dataSet, BirtProperties.OutputFormat.MS_WORD).writeTo(output);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        };
    }
}
