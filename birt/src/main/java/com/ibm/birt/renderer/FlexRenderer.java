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
import com.ibm.birt.bean.BirtProperties;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.util.TextCollectingVisitor;
import com.vladsch.flexmark.docx.converter.internal.DocxRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Slf4j
@Renderer(filetype = ".md")
public class FlexRenderer extends AbstractRenderer {
    public FlexRenderer(BirtConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void render(InputStream inputStream) throws Exception {
        InputStreamReader inputStreamReader = new InputStreamReader(configuration.getProperties().getReport().getFile().getInputStream());
        String input = IOUtils.toString(inputStreamReader);
        inputStream.close();

        log.info("Output Format is: {}", configuration.getProperties().getOutputFormat());
        BirtProperties.OutputFormat outputFormat = configuration.getProperties().getOutputFormat();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        switch (outputFormat) {
            case PDF:
                final MutableDataHolder PDF_OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
                        Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP)
                ).toMutable();
                
                final Parser PDF_PARSER = Parser.builder(PDF_OPTIONS).build();
                final HtmlRenderer PDF_RENDERER = HtmlRenderer.builder(PDF_OPTIONS).build();

                Node pdfDocument = PDF_PARSER.parse(input);
                String pdfHtml = PDF_RENDERER.render(pdfDocument);

                PdfConverterExtension.exportToPdf(out, pdfHtml,"", PDF_OPTIONS);
                break;
            case HTML:
                final MutableDataHolder HTML_OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
                        Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP)
                ).toMutable();

                final Parser HTML_PARSER = Parser.builder(HTML_OPTIONS).build();
                final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder(HTML_OPTIONS).build();

                Node htmlDocument = HTML_PARSER.parse(input);
                String html = HTML_RENDERER.render(htmlDocument);
                out.write(IOUtils.toByteArray(html));
                break;
            case MS_WORD:
                MutableDataHolder DOC_OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
                        Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP)
                ).toMutable()
                        .set(DocxRenderer.SUPPRESS_HTML, true)
                        // the DocxLinkResolver is added automatically, or alternately set it to false and add your own link resolver
                        .set(DocxRenderer.DEFAULT_LINK_RESOLVER, true)
                        // .set(DocxRenderer.DOC_RELATIVE_URL, "file:///Users/vlad/src/pdf") // this will be used for URLs like 'images/...' or './' or '../'
                        // .set(DocxRenderer.DOC_ROOT_URL, "file:///Users/vlad/src/pdf") // this will be used for URLs like: '/...'
                        ;
                final Parser DOC_PARSER = Parser.builder(DOC_OPTIONS).build();
                final DocxRenderer RENDERER = DocxRenderer.builder(DOC_OPTIONS).build();

                Node document = DOC_PARSER.parse(input);

                // or to control the package
                final WordprocessingMLPackage template = DocxRenderer.getDefaultTemplate();
                RENDERER.render(document, template);

                try {
                    template.save(out, Docx4J.FLAG_SAVE_ZIP_FILE);
                } catch (Docx4JException e) {
                    e.printStackTrace();
                }
                break;
            case TEXT:
                final DataHolder TEXT_OPTIONS = PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL);
                final MutableDataSet FORMAT_OPTIONS = new MutableDataSet();
                // copy extensions from Pegdown compatible to Formatting
                FORMAT_OPTIONS.set(Parser.EXTENSIONS, TEXT_OPTIONS.get(Parser.EXTENSIONS));
                final Parser TEXT_PARSER = Parser.builder(TEXT_OPTIONS).build();

                Node textDocument = TEXT_PARSER.parse(input);
                TextCollectingVisitor textCollectingVisitor = new TextCollectingVisitor();
                String text = textCollectingVisitor.collectAndGetText(textDocument);
                out.write(IOUtils.toByteArray(text));
                break;
            default:
                throw new UnsupportedFormatException("BIRT-001", new RuntimeException("OutputFormat '" + outputFormat.name() + "' is not supported for BIRT."));

        }

        File outputFile = configuration.getProperties().getOutputFile();
        if (!outputFile.getName().matches("^.*\\.(html|htm|pdf|txt|doc|docx)$"))
            outputFile = new File(outputFile.getParentFile(), outputFile.getName() + "." + getFileEnding(configuration.getProperties().getOutputFormat()));
        if (!outputFile.getParentFile().mkdirs()) {
            throw new RuntimeException("Cannot create directory " + outputFile.getParentFile().getAbsolutePath());
        }
        if (!outputFile.createNewFile()) {
            throw new RuntimeException("Cannot create file " + outputFile.getAbsolutePath());
        }
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(out.toByteArray());
        fos.close();
        log.info("Report has been rendered to {}", outputFile.getAbsolutePath());

    }
}
