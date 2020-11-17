package com.ibm.ttscustomization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.annotation.WebServlet;

/**
 * The entry point of the Spring Boot application.
 */
//@SpringBootApplication
//@EnableAsync
//@WebServlet
public class TTSServletInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TTSServletInitializer.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(TTSServletInitializer.class);
    }

}
