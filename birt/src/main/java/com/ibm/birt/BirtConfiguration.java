package com.ibm.birt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:birt.properties")
public class BirtConfiguration {

    @Value("${birt.output.format}")
    String outputFormat;

    public BirtConfiguration() {
    }
}
