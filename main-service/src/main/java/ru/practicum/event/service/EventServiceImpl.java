package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.EventMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.EventStateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ContradictionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Utility;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByPublic(Long eventId, String uri, String ip) {
        Event event = findEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event is not published, event by id:" + eventId);
        }
        addView(uri, ip);
        return toEventFullDtoAndViews(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime startTime, LocalDateTime endTime, Boolean onlyAvailable, String sort, Integer from, Integer size, String uri, String ip) {
        checkTime(startTime, endTime);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByPublic(text, categories, paid, onlyAvailable, startTime, endTime, sort, page);
        addView(uri, ip);
        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found user by id:" + userId);
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Not found event by id:" + eventId);
        }
        return toEventFullDtoAndViews(eventRepository.findByInitiatorIdAndId(userId, eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, page);

        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size) {
        checkTime(startTime, endTime);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, startTime, endTime, page);
        Map<Long, Long> views = getViews(events);
        events.forEach(event -> event.setViews(views.getOrDefault(event.getId(), 0L)));
        return eventMapper.toEventFullDtoList(events);
    }

    @Override
    public EventFullDto updateByUser(UpdateEventDto updateEventDto, Long userId, Long eventId) {
        Event event = findEvent(eventId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found user by id:" + userId);
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ContradictionException("User not is initiator by id:" + userId);
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ContradictionException("Event is published, event by id:" + eventId);
        }

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (updateEventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }
        event = eventRepository.save(updateEvent(updateEventDto, event));
        return toEventFullDtoAndViews(event);
    }

    @Override
    public EventFullDto updateByAdmin(UpdateEventDto updateEventDto, Long eventId) {
        Event event = findEvent(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ContradictionException("Event is not state: PENDING");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Event cannot be updated earlier than 1 hour");
        }

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        return toEventFullDtoAndViews(updateEvent(updateEventDto, event));
    }

    @Override
    public EventFullDto add(NewEventDto newEventDto, Long userId) {
        Category category = findCategory(newEventDto.getCategory());
        User user = findUser(userId);
        Location location = locationRepository.save(locationMapper.toLocation(newEventDto.getLocation()));
        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event by id:" + eventId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user by id:" + userId));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Not found event by id:" + categoryId));
    }

    private EventFullDto toEventFullDtoAndViews(Event event) {
        Map<Long, Long> views = getViews(List.of(event));
        event.setViews(views.getOrDefault(event.getId(), 0L));
        return eventMapper.toEventFullDto(event);
    }

    private void addView(String uri, String ip) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(Utility.APP_NAME)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.create(endpointHitDto);
    }

    private Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        List<String> urls = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        ResponseEntity<Object> response = statsClient.getStats(LocalDateTime.now().minusYears(50),
                LocalDateTime.now(),
                urls,
                true);
        try {
            List<ViewStatsDto> viewStats = Arrays.asList(objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
            viewStats.forEach(view -> views.put(Long.parseLong(view.getUri().split("/")[2]), view.getHits()));
            return views;
        } catch (JsonProcessingException e) {
            throw new ContradictionException("Stats: " + e.getMessage());
        }
    }


    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new BadRequestException("start after end is incorrect");
            }
        }
    }

    private Event updateEvent(UpdateEventDto eventDto, Event event) {
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(findCategory(eventDto.getCategory()));
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(locationRepository.save(locationMapper.toLocation(eventDto.getLocation())));
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        return event;
    }

}
