package ru.practicum.complilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.complilation.CompilationMapper;
import ru.practicum.complilation.dto.CompilationDto;
import ru.practicum.complilation.dto.NewCompilationDto;
import ru.practicum.complilation.dto.UpdateCompilationDto;
import ru.practicum.complilation.model.Compilation;
import ru.practicum.complilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getByPublic(Long compilationId) {
        return compilationMapper.toCompilationDto(compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Not find compilation by id:" + compilationId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllByPublic(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return compilationMapper.toCompilationDtoList(compilationRepository.findByPinned(pinned, page));
    }

    @Override
    public CompilationDto addByAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(eventRepository.findByIdIn(newCompilationDto.getEvents()));
        } else {
            compilation.setEvents(Collections.emptySet());
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateByAdmin(UpdateCompilationDto updateCompilationDto, Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException("Not find compilation by id:" + compilationId)
        );
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        if (updateCompilationDto.getEvents() != null && !updateCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(eventRepository.findByIdIn(updateCompilationDto.getEvents()));
        } else {
            compilation.setEvents(Collections.emptySet());
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteByAdmin(Long compilationId) {
        existsById(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private void existsById(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Not find compilation by id:" + compilationId);
        }
    }
}
