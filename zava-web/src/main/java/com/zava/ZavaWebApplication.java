package com.zava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring Boot application entry point for Zava Social.
 *
 * Extends SpringBootServletInitializer to support deployment as a WAR to
 * an external Tomcat 10.x container (Jakarta EE 10). The @ServletComponentScan
 * annotation enables discovery of @WebServlet-annotated servlet classes.
 */
@SpringBootApplication
@ServletComponentScan
public class ZavaWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ZavaWebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ZavaWebApplication.class, args);
    }
}
