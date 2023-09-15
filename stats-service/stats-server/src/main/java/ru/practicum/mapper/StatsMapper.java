package ru.practicum.mapper;

import ru.practicum.model.EndpointHit;

public class StatsMapper {

    public static EndpointHit toEndPointHit(ru.practicum.dto.EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

}
