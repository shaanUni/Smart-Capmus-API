package com.mycompany.smart.campus.api.config;

import com.mycompany.smart.campus.api.filter.ApiLoggingFilter;
import com.mycompany.smart.campus.api.mapper.GenericExceptionMapper;
import com.mycompany.smart.campus.api.mapper.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.smart.campus.api.mapper.RoomNotEmptyExceptionMapper;
import com.mycompany.smart.campus.api.mapper.SensorUnavailableExceptionMapper;
import com.mycompany.smart.campus.api.resource.DiscoveryResource;
import com.mycompany.smart.campus.api.resource.RoomResource;
import com.mycompany.smart.campus.api.resource.SensorResource;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {

    public AppConfig() {
        register(JacksonFeature.class);

        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);

        register(RoomNotEmptyExceptionMapper.class);
        register(LinkedResourceNotFoundExceptionMapper.class);
        register(SensorUnavailableExceptionMapper.class);
        register(GenericExceptionMapper.class);

        register(ApiLoggingFilter.class);
    }
}