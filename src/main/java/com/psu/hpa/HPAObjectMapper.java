package com.psu.hpa;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class HPAObjectMapper extends ObjectMapper {
    public HPAObjectMapper() {
        super();
        configure(Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        setDateFormat(new SimpleDateFormat("MM/dd/yyyy"));
    }
}
