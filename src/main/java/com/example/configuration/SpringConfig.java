package com.example.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import edu.umuc.rest.support.configuration.RestConfiguration;
//@EnableWebMvc imports the Spring MVC configuration from WebMvcConfigurationSupport
//@ComponentScan register which packages it can scan to find your Spring components.
//@Configuration indicates that the class can be used by the Spring IoC container as a source of bean definitions
//@Bean annotation will return an object that should be registered as a bean in the Spring application context.

@EnableWebMvc
@Configuration
@ComponentScan( value = { "com.example" }, excludeFilters = @Filter( Configuration.class ) )
public class SpringConfig extends RestConfiguration {
    /**
     * register all Spring-related beans. replace the need to create a SpringApplicationContext.xml file
     */
}
