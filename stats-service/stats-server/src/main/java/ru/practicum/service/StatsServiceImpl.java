package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("start after end is incorrect");
        }

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return endpointHitRepository.findAllViewStatsWithUnique(start, end);
            } else {
                return endpointHitRepository.findAllViewStatsWithUrisAndUnique(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return endpointHitRepository.findAllViewStats(start, end);
            } else {
                return endpointHitRepository.findAllViewStatsWithUris(start, end, uris);
            }
        }
    }

    @Override
    @Transactional
    public void add(EndpointHitDto endpointHitDto) {
        endpointHitRepository.save(StatsMapper.toEndPointHit(endpointHitDto));
    }


}
