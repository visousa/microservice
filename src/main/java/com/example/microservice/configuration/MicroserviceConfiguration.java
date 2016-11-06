package com.example.microservice.configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by bruno on 03-11-2016.
 */

@Configuration
@EnableAspectJAutoProxy
public class MicroserviceConfiguration {

    @Autowired
    private MetricRegistry registry;

    /**
     * create an JmxReport as bean and register the metrics
     * @return JmxReporter
     */
    @Bean
    public JmxReporter jmxReporter() {
        JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();
        return reporter;
    }

}