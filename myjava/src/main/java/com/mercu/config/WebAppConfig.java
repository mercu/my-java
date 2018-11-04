package com.mercu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.mercu"})
public class WebAppConfig extends WebMvcConfigurerAdapter {
    @Bean
    public HandlerMapping handlerMapping() {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();

        Properties properties = new Properties();
        properties.put("/**", new UrlFilenameViewController());
        handlerMapping.setMappings(properties);

        return handlerMapping;
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/static/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
        requestMappingHandlerAdapter.setMessageConverters(
                Arrays.asList(
                        new HttpMessageConverter[]{stringHttpMessageConverter()}));
        return requestMappingHandlerAdapter;
    }

    @Bean
    public HttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType[]{
                MediaType.valueOf("text/html;charset=UTF-8"),
                MediaType.valueOf("application/json;charset=UTF-8")
        }));
        return stringHttpMessageConverter;
    }

}
