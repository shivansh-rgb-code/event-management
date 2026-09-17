package com.example.event_management.service;
import com.example.event_management.entity.EventStatus;
import com.example.event_management.entity.Event;
import com.example.event_management.exception.BusinessRuleException;
import com.example.event_management.exception.DuplicateEventException;
import com.example.event_management.repository.EventRepository;
import org.springframework.stereotype.Service;
import com.example.event_management.exception.EventNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {

        if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException(
                    "Event cannot be created in the past"
            );
        }

        validateEventRules(event);
        validateEventStatus(event);
        if (eventRepository.existsByNameIgnoreCaseAndStartDateTimeAndVenueIgnoreCase(
                event.getName(),
                event.getStartDateTime(),
                event.getVenue())) {

            throw new DuplicateEventException(
                    "An event with the same name, start time and venue already exists"
            );
        }
        return eventRepository.save(event);
    }

    public Page<Event> getEvents(
            String search,
            String venue,
            EventStatus status,
            int page,
            int limit,
            String sort,
            String order) {

        if (search != null && search.isBlank()) {
            search = null;
        }

        if (venue != null && venue.isBlank()) {
            venue = null;
        }

        if (page < 1) {
            throw new BusinessRuleException(
                    "Page must be greater than 0"
            );
        }

        if (limit < 1) {
            throw new BusinessRuleException(
                    "Limit must be greater than 0"
            );
        }

        Sort.Direction direction;

        if (order.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }

        String sortField;

        if (sort.equalsIgnoreCase("date")) {
            sortField = "startDateTime";
        } else if (sort.equalsIgnoreCase("name")) {
            sortField = "name";
        } else if (sort.equalsIgnoreCase("capacity")) {
            sortField = "capacity";
        } else {
            throw new BusinessRuleException(
                    "Invalid sort field"
            );
        }

        Pageable pageable = PageRequest.of(
                page - 1,
                limit,
                Sort.by(direction, sortField)
        );

        return eventRepository.searchEvents(
                search,
                venue,
                status,
                pageable
        );
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Optional<Event> updateEvent(Long id, Event updatedEvent) {

        Optional<Event> existingEventOptional =
                eventRepository.findById(id);

        if (existingEventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event existingEvent = existingEventOptional.get();

        validateEventCanBeUpdated(existingEvent);

        if (eventRepository.existsByNameIgnoreCaseAndStartDateTimeAndVenueIgnoreCaseAndIdNot(
                updatedEvent.getName(),
                updatedEvent.getStartDateTime(),
                updatedEvent.getVenue(),
                id)) {

            throw new DuplicateEventException(
                    "An event with the same name, start time and venue already exists"
            );
        }

        existingEvent.setName(updatedEvent.getName());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setStartDateTime(updatedEvent.getStartDateTime());
        existingEvent.setEndDateTime(updatedEvent.getEndDateTime());
        existingEvent.setVenue(updatedEvent.getVenue());
        existingEvent.setCapacity(updatedEvent.getCapacity());
        existingEvent.setStatus(updatedEvent.getStatus());

        validateEventRules(existingEvent);
        validateEventStatus(existingEvent);

        return Optional.of(eventRepository.save(existingEvent));
    }

    public void deleteEvent(Long id) {

        Optional<Event> eventOptional = eventRepository.findById(id);

        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException(
                    "Event not found"
            );
        }

        Event event = eventOptional.get();

        if (event.getStatus() == EventStatus.ONGOING) {
            throw new BusinessRuleException(
                    "Ongoing events cannot be deleted"
            );
        }

        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Completed events cannot be deleted"
            );
        }

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Cancelled events cannot be deleted"
            );
        }

        eventRepository.delete(event);
    }

    private void validateEventRules(Event event) {

        if (!event.getEndDateTime().isAfter(event.getStartDateTime())) {
            throw new BusinessRuleException(
                    "End date and time must be after start date and time"
            );
        }
    }

    private void validateEventStatus(Event event) {

        LocalDateTime now = LocalDateTime.now();

        if (event.getStatus() == EventStatus.CANCELLED) {
            return;
        }

        if (event.getStatus() == EventStatus.UPCOMING) {

            if (!now.isBefore(event.getStartDateTime())) {
                throw new BusinessRuleException(
                        "Upcoming event must have a start time in the future"
                );
            }
        }

        if (event.getStatus() == EventStatus.ONGOING) {

            if (now.isBefore(event.getStartDateTime())
                    || !now.isBefore(event.getEndDateTime())) {

                throw new BusinessRuleException(
                        "Ongoing event must be currently in progress"
                );
            }
        }

        if (event.getStatus() == EventStatus.COMPLETED) {

            if (!now.isAfter(event.getEndDateTime())) {
                throw new BusinessRuleException(
                        "Completed event must have already ended"
                );
            }
        }
    }

    private void validateEventCanBeUpdated(Event event) {

        if (event.getStatus() == EventStatus.ONGOING) {
            throw new BusinessRuleException(
                    "Ongoing events cannot be edited"
            );
        }

        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Completed events cannot be edited"
            );
        }

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Cancelled events cannot be edited"
            );
        }
    }
}