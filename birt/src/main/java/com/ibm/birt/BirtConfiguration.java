package com.ibm.birt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:birt.properties"})
public class BirtConfiguration {

    @Value("${birt.output.format}")
    private String outputFormat;

    @Value("${birt.output.path}")
    private String outputPath;

    @Value("${birt.output.file}")
    private String outputFile;

    public BirtConfiguration() {
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
