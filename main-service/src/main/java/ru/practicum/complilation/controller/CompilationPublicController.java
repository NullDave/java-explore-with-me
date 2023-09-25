package ru.practicum.complilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.complilation.dto.CompilationDto;
import ru.practicum.complilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(name = "pinned", defaultValue = "false") Boolean pinned,
                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all compilations by pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.getAllByPublic(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable("compId") Long compilationId) {
        log.info("Get compilation by id={}", compilationId);
        return compilationService.getByPublic(compilationId);
    }
}
