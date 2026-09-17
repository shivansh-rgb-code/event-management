package com.example.event_management.controller;

import com.example.event_management.entity.Event;
import com.example.event_management.entity.EventStatus;
import com.example.event_management.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody Event event) {
        Event createdEvent = eventService.createEvent(event);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEvent);
    }

    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "asc") String order) {

        return ResponseEntity.ok(
                eventService.getEvents(
                        search,
                        venue,
                        status,
                        page,
                        limit,
                        sort,
                        order
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody Event event) {

        return eventService.updateEvent(id, event)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);

        return ResponseEntity.noContent().build();
    }
}