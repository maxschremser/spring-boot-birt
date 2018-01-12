package com.ibm.birt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BirtApplication {
    public static void main(String[] args) {
//        BirtProcessor.renderReport();
        SpringApplication app = new SpringApplication(BirtApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
        ctx.close();
    }
}
