package com.csa.assistant.service;

import com.csa.assistant.entity.LogisticsEntity;
import com.csa.assistant.mapper.LogisticsMapper;
import com.csa.assistant.model.LogisticsInfo;
import com.csa.assistant.model.TrackingEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LogisticsService {

    private final LogisticsMapper logisticsMapper;
    private final ObjectMapper objectMapper;

    public LogisticsService(LogisticsMapper logisticsMapper, ObjectMapper objectMapper) {
        this.logisticsMapper = logisticsMapper;
        this.objectMapper = objectMapper;
    }

    public LogisticsInfo getLogisticsByTrackingNo(String trackingNo) {
        LogisticsEntity entity = logisticsMapper.selectByTrackingNo(trackingNo);
        return entity != null ? convertToModel(entity) : null;
    }

    private LogisticsInfo convertToModel(LogisticsEntity entity) {
        LogisticsInfo info = new LogisticsInfo();
        info.setTrackingNo(entity.getTrackingNo());
        info.setLogisticsCompany(entity.getLogisticsCompany());
        info.setStatus(entity.getStatus());
        
        try {
            List<TrackingEvent> events = objectMapper.readValue(
                    entity.getEventsJson(),
                    new TypeReference<List<TrackingEvent>>() {}
            );
            info.setTrackingEvents(events);
        } catch (Exception e) {
            info.setTrackingEvents(Collections.emptyList());
        }
        
        return info;
    }
}
