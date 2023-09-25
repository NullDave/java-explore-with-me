package ru.practicum.complilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.complilation.dto.CompilationDto;
import ru.practicum.complilation.dto.NewCompilationDto;
import ru.practicum.complilation.dto.UpdateCompilationDto;
import ru.practicum.complilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Add compilation={}", newCompilationDto);
        return compilationService.addByAdmin(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid UpdateCompilationDto updateCompilationDto,
                                 @PathVariable("compId") Long compilationId) {
        log.info("Update compilation={} by id={}", updateCompilationDto, compilationId);
        return compilationService.updateByAdmin(updateCompilationDto, compilationId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long compilationId) {
        log.info("Delete compilation by id={}", compilationId);
        compilationService.deleteByAdmin(compilationId);
    }
}
