package com.ibm.birt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ibm.birt"})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
public class BirtApplication {
    public static void main(String[] args) {
//        BirtProcessor.renderReport();
        SpringApplication app = new SpringApplication(BirtApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
        ctx.close();
    }
}
