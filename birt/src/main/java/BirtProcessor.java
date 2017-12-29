import org.apache.commons.io.IOUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;

public class BirtProcessor {
    private static Logger log = LoggerFactory.getLogger(BirtProcessor.class);

    public static void main(String[] args) throws Exception {
        BirtProcessor.renderReport();
    }

    public static void renderReport() throws BirtException, FileNotFoundException, IOException, URISyntaxException {
        IReportEngine engine;
        EngineConfig config = new EngineConfig();
        // System.setProperty("BIRT_HOME", "/Users/AT003053/jDev/github/birt-integration/birt-runtime-4_5_0/ReportEngine");
        // config.setEngineHome("file:/Users/AT003053/jDev/github/birt-integration/birt-runtime-4_5_0/ReportEngine");

        Platform.startup(config);

        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        engine = factory.createReportEngine(config);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        IReportRunnable design = engine.openReportDesign("RT001", new FileInputStream(new File(BirtProcessor.class.getClassLoader().getResource("simple.rptdesign").toURI())));
        IRunAndRenderTask task = engine.createRunAndRenderTask(design);
        IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
        for (Object o : paramTask.getParameterDefns(false)) {
            IParameterDefnBase rptParam = (IParameterDefnBase) o;
            if (rptParam.getParameterType() != IParameterDefnBase.SCALAR_PARAMETER) {
                continue; //only process scalar parameters
            }
            String paramName = rptParam.getName();
            String paramValue = null;

            if (paramValue != null) {
                task.setParameterValue(paramName, paramValue);
                String paramValueLog = paramValue;
                if (paramName.contains("pwd")) {
                    paramValueLog = "*****";
                }
                log.info("ReportParam {} resolved to {}", paramName, paramValueLog);
            } else {
                log.info("ReportParam {} cannot be resolved", paramName);
            }
        }

        task.validateParameters();

        //Set rendering options - such as file or stream output,
        IRenderOption options;
        options = new HTMLRenderOption();
        options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
        ((HTMLRenderOption) options).setEmbeddable(false);
        options.setImageHandler(new HTMLServerImageHandler() {
            @Override
            protected String handleImage(IImage image, Object context, String prefix, boolean needMap) {
                try {
                    final String content = Base64.encode(IOUtils.toByteArray(image.getImageStream()));
                    return "data:" + image.getMimeType() + ";base64," + content;
                } catch (Exception ignore) {
                }
                return "";
            }
        });
        options.setOutputStream(out);
        task.setRenderOption(options);
        task.setErrorHandlingOption(IEngineTask.CANCEL_ON_ERROR);
        task.run();
        task.close();
        log.info("finished");
        // log.info(out.toString());
        FileOutputStream fos = new FileOutputStream(new File("out", "test.html"));
        fos.write(out.toByteArray());
    }
}
