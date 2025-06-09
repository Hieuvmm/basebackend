package com.vworks.wms.main_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.vworks.wms"})
@EnableJpaRepositories(basePackages = {"com.vworks.wms"})
@EntityScan(basePackages = {"com.vworks.wms"})
@ComponentScan(basePackages = {"com.vworks.wms"})
@EnableFeignClients(basePackages = {"com.vworks.wms"})
public class WmsMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WmsMainApplication.class, args);
    }
}