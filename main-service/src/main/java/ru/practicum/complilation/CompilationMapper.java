package ru.practicum.complilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.complilation.dto.CompilationDto;
import ru.practicum.complilation.dto.NewCompilationDto;
import ru.practicum.complilation.dto.UpdateCompilationDto;
import ru.practicum.complilation.model.Compilation;
import ru.practicum.event.EventMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(UpdateCompilationDto updateCompilationDto);

    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto> toCompilationDtoList(List<Compilation> compilation);
}
