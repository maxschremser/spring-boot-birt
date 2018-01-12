package com.ibm.birt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Configuration
@PropertySource({"classpath:birt.properties"})
public class BirtConfiguration {
    private final Logger log = LoggerFactory.getLogger(BirtConfiguration.class);

    @Value("${birt.output.format}")
    private String outputFormat;
    @Value("${birt.output.file}")
    private File outputFile;

    @Value("${birt.report}")
    private String reportFile;
    @Value("${birt.report.param.dataSet}")
    private String dataSet;

    public BirtConfiguration() {
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public File getOutputFile() {
        //noinspection ResultOfMethodCallIgnored
        outputFile.getParentFile().mkdirs();
        return outputFile;
    }

    public InputStream getReportFile() throws FileNotFoundException {
        if (reportFile.startsWith("classpath:")) {
            try {
                return BirtConfiguration.class.getClassLoader().getResourceAsStream(reportFile.substring(10));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        return new FileInputStream(new File(reportFile));
    }

    public String getDataSet() {
        return dataSet;
    }

    public String getFieldValueByName(String key) {
        try {
            return BirtConfiguration.class.getDeclaredField(key).get(this).toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
