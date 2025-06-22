package com.example.tourplanner.pdf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafConfiguration {
    @Bean(name = "pdfTemplateEngine")
    public TemplateEngine pdfTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(pdfTemplateResolver());
        return templateEngine;
    }

    @Bean(name = "mapTemplateEngine")
    public TemplateEngine mapTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(mapTemplateResolver());
        return templateEngine;
    }


    @Bean
    public ClassLoaderTemplateResolver pdfTemplateResolver() {
        ClassLoaderTemplateResolver pdfTemplateResolver = new ClassLoaderTemplateResolver();
        pdfTemplateResolver.setPrefix("pdf-templates/");
        pdfTemplateResolver.setSuffix(".html");
        pdfTemplateResolver.setTemplateMode("HTML5");
        pdfTemplateResolver.setCharacterEncoding("UTF-8");
        pdfTemplateResolver.setOrder(1);
        return pdfTemplateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver mapTemplateResolver() {
        ClassLoaderTemplateResolver mapTemplateResolver = new ClassLoaderTemplateResolver();
        mapTemplateResolver.setPrefix("map-templates/");
        mapTemplateResolver.setSuffix(".html");
        mapTemplateResolver.setTemplateMode("HTML5");
        mapTemplateResolver.setCharacterEncoding("UTF-8");
        mapTemplateResolver.setOrder(2);
        return mapTemplateResolver;
    }
}
