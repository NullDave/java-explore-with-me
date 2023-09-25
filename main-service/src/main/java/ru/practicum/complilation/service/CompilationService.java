package ru.practicum.complilation.service;

import ru.practicum.complilation.dto.CompilationDto;
import ru.practicum.complilation.dto.NewCompilationDto;
import ru.practicum.complilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto getByPublic(Long compilationId);

    List<CompilationDto> getAllByPublic(Boolean pinned, Integer from, Integer size);

    CompilationDto addByAdmin(NewCompilationDto newCompilationDto);

    CompilationDto updateByAdmin(UpdateCompilationDto compilationDto, Long compilationId);

    void deleteByAdmin(Long compilationId);
}
