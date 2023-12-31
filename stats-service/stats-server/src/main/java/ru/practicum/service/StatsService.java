package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    void add(EndpointHitDto endpointHitDto);

}
