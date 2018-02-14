package com.example.configuration;

import edu.umuc.rest.support.configuration.AbstractWebAppInitializer;

public class WebAppInitializer extends AbstractWebAppInitializer {

    /**
     *
     * replace the traditional web.xml. This handles setup of DispatcherServlet, Multipart forms ,
     * CORS filter etc
     *
     */

    @Override
    protected Class<?> springConfiguration() {
        //provide your local Spring configuration class to be registered
        return SpringConfig.class;

    }



}
